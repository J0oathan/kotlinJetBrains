import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.v2018_2.failureConditions.failOnTex
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.PowerShellStep

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    buildType(Build)
    buildType(TestSecondBc)
    buildType(DataloopWebInterface_2_Fsr)
}

object Build : BuildType({
    name = "Maven Kotlin"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "my custom step name edit"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object TestSecondBc : BuildType({
    name = "Test second BC"
    description = "Create new file"

    steps {
        powerShell {
            scriptMode = script {
                content = """
                    ${'$'}FilePath = "C:\ScriptsCustoms\MyFile.txt"
                     
                    #Check if file exists
                    if (Test-Path ${'$'}FilePath) {
                        Write-host "File '${'$'}FilePath' already exists!" -f Yellow
                    }
                    Else {
                        #Create a new file
                        New-Item -Path ${'$'}FilePath -ItemType "File"
                        Write-host "New File '${'$'}FilePath' Created!" -f Green
                    }
                """.trimIndent()
            }
        }
    }
})

object DataloopWebInterface_2_Fsr : BuildType({
    id("Fsr")
    name = "Continuos Integration"
    description = "This build configuration is used to run unitTest BE/FE when any release branch is pushed"

    artifactRules = """
        client\unitTest\karma_html\report-summary-filename=> karmaa.zip
        client\funcTest\allure-report => allure.zip
        client\codeAnalysis\reports\current=> codeanalysis.zip
        client\unitTest\coverage\%env.ChromeVersion% => coverage.zip
        client\unitTest\utils\mock\preloadedMockData.js => mocksFile.zip
        server\report\unittest => unitTestBE.zip
        server\report\coverage => coverageBackend.zip
    """.trimIndent()

    params {
        checkbox("Group", "--group=", display = ParameterDisplay.HIDDEN,
                checked = "--group=")
        checkbox("Run_UnitTest_FE", "npm install --loglevel=error",
                checked = "npm install --loglevel=error", unchecked = "cls")
        text("env.ChromeVersion", "Chrome 116.0.0 (Windows 10 0.0.0)", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        checkbox("Run_E2E", "npm install",
                checked = "npm install", unchecked = "cls")
        checkbox("Smoke", "--smoke",
                checked = "--smoke")
        param("env.VersionCodeAnalysis", "2020")
        checkbox("Mobile", "--mobile",
                checked = "--mobile")
        checkbox("groupname", "", display = ParameterDisplay.HIDDEN,
                checked = "true")
        param("Pipeline", "*")
    }

    vcs {
        root(DataloopWebInterface_2_DataloopBitbucket20191063822)

        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
        checkoutDir = "ContinuosIntegration"
    }

    steps {
        powerShell {
            name = "Get user and email"
            scriptMode = script {
                content = """
                    ${'$'}mail = git log -1 --pretty=format:'%ae'
                    ${'$'}name = git log -1 --pretty=format:'%an'
                    
                    ${'$'}pathToDir = "E:\ContinuousIntegration_Emails\"
                    ${'$'}pathToJson = "E:\ContinuousIntegration_Emails\FSRdata.json"
                    
                    if(${'$'}mail -ne "devoperations@polyworksmexico.com")
                    {
                    	# Validate directory 
                    	if (-not (Test-Path ${'$'}pathToDir))
                    	{
                            New-Item ${'$'}pathToDir -itemType Directory
                            Write-OutPut "Directory does not exist, creaing directory ${'$'}pathToDir..."
                    	} 
                        
                        #Validate Json File
                        if (-not (Test-Path ${'$'}pathToJson -PathType Leaf))
                        { 
                            New-Item -Path ${'$'}pathToJson -ItemType File
                            Write-OutPut "File does not exist, creaing file ${'$'}pathToJson..."
                            Add-Content -Path ${'$'}pathToJson -Value '{ "user": "user", "name": "name", "cc": "100", "status": "0" }'
                        }
                        else 
                        {
                        	${'$'}json = Get-Content ${'$'}pathToJson | ConvertFrom-Json
                        	foreach(${'$'}item in ${'$'}json) {
                            	if(-not (${'$'}prop = ${'$'}item.PSObject.Properties['status'])) {
                                	${'$'}json | Add-Member -Type NoteProperty -Name 'status' -Value '0'
                                    Write-OutPut "Adding status to json file..."
                            	}
                        	}
                            ${'$'}json.user = "${'$'}mail"
                      		${'$'}json.name = "${'$'}name"
                        	${'$'}json | ConvertTo-Json | set-content ${'$'}pathToJson
                            Write-OutPut "Adding ${'$'}name and ${'$'}mail to the json file."
                        }
                    }
                    else{ Write-OutPut "Last push was made by ${'$'}mail" }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Run script backend"
            scriptMode = script {
                content = """
                    #cd 'C:\BackEnd-Generator-Identity-FSR\'
                    #.\BackEnd-Generator.ps1
                    Start-Process powershell "-ExecutionPolicy Bypass -NoProfile -Command `"cd \`"C:\BackEnd-Generator-Identity-FSR\`"; & \`".\BackEnd-Generator.ps1\`"`"" -Verb RunAs 
                    Start-Sleep -Seconds 60
                    
                    #Start-Process powershell -Verb runAs "C:\BackEnd-Generator-Identity\BackEnd-Generator.ps1"
                """.trimIndent()
            }
            param("jetbrains_powershell_script_file", "BackEnd-Generator.ps1")
        }
        powerShell {
            name = "Stop IIS"
            scriptMode = script {
                content = """
                    IISReset /stop
                    #Start-Process -Wait powershell "-ExecutionPolicy Bypass -NoProfile  -Command IISReset /stop" -Verb RunAs
                """.trimIndent()
            }
        }
        powerShell {
            name = "Publish IIS FrontEnd"
            scriptMode = script {
                content = """
                    ${'$'}copyToDir= "C:\inetpub\wwwroot\polyworks\dataloop"
                    
                    Write-Host "----------------repositorio--------------------------"
                    Write-Host ${'$'}copyToDir
                    Write-Host -Foreground Yellow "Now proceding to copy backend to '${'$'}(${'$'}copyToDir)'..."
                    
                    if (!(Test-Path "${'$'}(${'$'}copyToDir)")) {
                    		mkdir "${'$'}(${'$'}copyToDir)"
                    }
                    ${'$'}ruta = Get-Childitem -Path "client\static\" 
                    Write-Host ${'$'}ruta 
                    
                    robocopy "client\static\" ${'$'}copyToDir /MIR
                """.trimIndent()
            }
        }
        powerShell {
            name = "Start IIS"
            scriptMode = script {
                content = """
                    IISReset /START
                    #Start-Process -Wait powershell "-ExecutionPolicy Bypass -NoProfile  -Command IISReset /START" -Verb RunAs
                """.trimIndent()
            }
        }
        powerShell {
            name = "Change enviroment in teamcity to run code or msi"
            scriptMode = script {
                content = """
                    ${'$'}pathPolyworks = "C:\inetpub\wwwroot\polyworks"
                    ${'$'}pathDataloop = "${'$'}pathPolyworks\dataloop\"
                    ${'$'}pathRest = "${'$'}pathPolyworks\rest"
                     
                    
                    function Install-DLWI-Environment {
                     if (Exist-Deploy-Paths){
                        if(Exist-Dataloop-Applications){
                            #This means dlwi msi is installed, unistall is required
                            Write-Host "Application found, this should not happen!"
                            Uninstall-DLWI-MSI
                        } 
                        Create-VD-IIS
                        Set-Access-Rule
                     }
                    }
                    
                    function Uninstall-DLWI-Environment {
                        Remove-WebApplication -Name "\polyworks\rest" -Site "Default Web Site"
                        Remove-Item "IIS:\Sites\Default Web Site\polyworks\dataloop" -Force -Recurse
                    }
                    
                    
                    
                    function Exist-Deploy-Paths {
                        Write-Host "Testing deploy paths"
                        ${'$'}correctedPaths = ${'$'}true
                        ${'$'}paths = @(${'$'}pathPolyworks, ${'$'}pathDataloop, ${'$'}pathRest)
                        For (${'$'}i = 0; ${'$'}i -lt ${'$'}paths.Length; ${'$'}i++) {
                            if (-not (Test-Path -Path ${'$'}paths[${'$'}i])) {
                                Write-Host ${'$'}paths[${'$'}i] " path does not exist"
                                ${'$'}correctedPaths = ${'$'}false
                            }
                        }
                        if (${'$'}correctedPaths){Write-Host "All paths exist"}
                    
                        return ${'$'}correctedPaths
                    }
                    
                    function Exist-Dataloop-Applications {
                        Write-Host "Verify a current DLWI application exits in IIS context"
                        ${'$'}existApplication = ${'$'}false
                        ${'$'}applications = Get-WebApplication -Site "Default Web Site" | where{${'$'}_.path.Contains("polyworks")}
                        if (${'$'}applications.Length -gt 0){
                            Write-Host "DLWI application found!!!!!!!!!!!!!!"
                            ${'$'}existApplication = ${'$'}true
                        }else {
                            Write-Host "No DLWI application in ISS found" 
                            ${'$'}existApplication = ${'$'}false
                        }
                        return ${'$'}existApplication
                    }
                    
                    function Uninstall-DLWI-MSI {
                        Write-Host "Trying to find DLWI msi wmiObject"
                        ${'$'}innovMetricFolder = "C:\inetpub\wwwroot\InnovMetric"
                        ${'$'}product = Get-WmiObject win32_product | where{${'$'}_.name.Contains("PolyWorks|DataLoop(TM) Web Interface Server")}
                        if (${'$'}product -eq ${'$'}null){
                            Write-Host "Msi of DLWI not found !!!"
                        } else {
                            Write-Host "DLWI msi found name: " ${'$'}product.Name " with version: " ${'$'}product.Version
                            Write-Host "Proceeding to uninstall"
                            Start-Process "msiexec.exe" -ArgumentList "/x ${'$'}(${'$'}product.IdentifyingNumber ) /quiet"  -Wait
                            Start-Sleep -Seconds 40
                            #Remove-Item ${'$'}innovMetricFolder -Recurse -Force
                        }
                    }
                    
                    function Create-VD-IIS {
                        #Does not use force method to create virtual directory
                        Write-Host "Creating rest web application"
                        New-WebApplication -Name "\polyworks\rest" -Site "Default Web Site" -PhysicalPath ${'$'}pathRest -ApplicationPool "restAppPoolCode"
                     
                        Write-Host "Creating dataloop virtual directory"
                        New-WebVirtualDirectory -Site "Default Web Site" -Name "\polyworks\dataloop" -PhysicalPath ${'$'}pathDataloop 
                        Start-WebAppPool -Name "restAppPoolCode"
                    }
                    
                    function Set-Access-Rule {
                        Write-Host "Setting fullControl access to polyworks path"
                        ${'$'}accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule("IIS_IUSRS", "FullControl", "ContainerInherit,ObjectInherit", "None", "Allow")
                        ${'$'}acl = Get-ACL ${'$'}pathPolyworks
                        ${'$'}acl.AddAccessRule(${'$'}accessRule)
                        Set-ACL -Path ${'$'}pathPolyworks -ACLObject ${'$'}acl
                    }
                    
                    ${'$'}agent = '%system.agent.name%'
                    Write-Host 'Agente ' ${'$'}agent
                    
                    if (${'$'}agent -eq 'DLWI_Agent_1' -or ${'$'}agent -eq 'DLWI_Agent_2' ) { #50.60 o 50.67
                    	Install-DLWI-Environment
                    } else { #50.27
                    	Write-Host "Run in DLWI_Agent_old not change the enviroment"
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Delete Folder unitTest Reports"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "client/unitTest"
            scriptMode = script {
                content = """
                    ${'$'}folderTestResults = Test-Path "karma_html\report-summary-filename"
                    if(${'$'}folderTestResults -eq ${'$'}True)
                    {
                       Remove-Item -path "karma_html" -Force -Recurse
                       Write-OutPut "Removing unitTest Reports Folder"
                    }
                    else{
                    Write-OutPut "unitTest reports not found"
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Run UnitTest Backend and Code coverage"
            scriptMode = script {
                content = """
                    Write-Host  "Run Unitest and code coverage backend"
                    
                    ${'$'}pathCoverResults = 'E:\BuildAgent\work\ContinuosIntegration\server\CoverResults'
                    
                    if (-not (Test-Path ${'$'}pathCoverResults))
                    {
                    Write-Host 'Create CoverResults folder'
                    New-Item ${'$'}pathCoverResults -itemType Directory 
                    }
                    
                    
                    ${'$'}date = (Get-Date -Format 'MM-dd-yyyy')
                    Write-Host 'date: ' ${'$'}date
                    
                    ${'$'}nameCoverFile = [System.String]::Concat('CodeCover-',${'$'}date,'.xml')
                    Write-Output ${'$'}nameCoverFile
                    
                    
                    ${'$'}pathReportFolder = 'E:\BuildAgent\work\ContinuosIntegration\server\report'
                    ${'$'}pathReportUnitTest = 'E:\BuildAgent\work\ContinuosIntegration\server\report\unittest'
                    ${'$'}pathReportCoverage = 'E:\BuildAgent\work\ContinuosIntegration\server\report\coverage'
                    
                    if (-not (Test-Path ${'$'}pathReportFolder))
                    {
                    Write-Host 'Create Report folder'
                    New-Item ${'$'}pathReportFolder -itemType Directory 
                    }
                    
                    if (-not (Test-Path ${'$'}pathReportUnitTest))
                    {
                    Write-Host 'Create unitTest folder'
                    New-Item ${'$'}pathReportUnitTest -itemType Directory 
                    }
                    
                    if (-not (Test-Path ${'$'}pathReportCoverage))
                    {
                    Write-Host 'Create Coverage folder'
                    New-Item ${'$'}pathReportCoverage -itemType Directory 
                    }
                    
                    
                    Set-Location 'E:\BuildAgent\work\ContinuosIntegration\server\'
                    Start-Process 'E:\tools\opencover.4.7.1221\OpenCover.Console.exe' -ArgumentList '-target:E:\tools\runTestUnitContIntegration.bat -log:Off -oldstyle -register:user -output:CoverResults\file.xml' -Wait
                    
                    Rename-Item -Path 'E:\BuildAgent\work\ContinuosIntegration\server\CoverResults\file.xml' -NewName ${'$'}nameCoverFile
                """.trimIndent()
            }
        }
        powerShell {
            name = "generate repports BE"
            scriptMode = script {
                content = """
                    Write-Host  "Create reports backend"
                    
                    
                    ${'$'}pathTestResults = 'E:\BuildAgent\work\ContinuosIntegration\server\TestResults'
                    ${'$'}pathReportUnitTest = 'E:\BuildAgent\work\ContinuosIntegration\server\report\unittest'
                    
                    Set-Location 'E:\BuildAgent\work\ContinuosIntegration\server'
                    
                    if (-not (Test-Path ${'$'}pathTestResults))
                    {
                      Write-Host 'Create testResults folder'
                      New-Item ${'$'}pathTestResults -itemType Directory 
                    }
                    
                    ${'$'}fileTRX = Get-ChildItem 'testResults\*.trx' -Name
                    
                    
                    foreach (${'$'}file in ${'$'}fileTRX )
                    {
                      ${'$'}newestTest = ${'$'}file
                      Write-Host 'trx file: ' ${'$'}newestTest
                    }
                    
                    ${'$'}fileXML = Get-ChildItem 'CoverResults\*.xml' -Name
                    
                    
                    foreach (${'$'}file in ${'$'}fileXML )
                    {
                      ${'$'}newestCover = ${'$'}file
                      Write-Host 'xml file: ' ${'$'}newestCover
                    }
                    
                    ${'$'}testResults = 'testResults\' + ${'$'}newestTest
                    ${'$'}coverResults = '-reports:CoverResults\' + ${'$'}newestCover + ' -targetdir:report\coverage -reporttypes:Html'
                    Write-Host 'testReults: ' ${'$'}testResults
                    Write-Host 'coverResults: ' ${'$'}coverResults
                    Start-Process 'E:\tools\TrxerTestReport\TrxerConsole.exe' -ArgumentList ${'$'}testResults -Wait 
                    Start-Process 'E:\tools\ReportGenerator\ReportGenerator.exe' -ArgumentList ${'$'}coverResults -Wait 
                    
                    Set-Location ${'$'}pathTestResults
                    
                    #robocopy ${'$'}pathTestResults ${'$'}pathReportUnitTest DL_CI-AGENT03-VM*.html 
                    robocopy ${'$'}pathTestResults ${'$'}pathReportUnitTest *.html 
                    
                    
                    ${'$'}nameFileHtml = Get-ChildItem ${'$'}pathReportUnitTest -Name
                    Write-Host 'nameFileHtml: ' ${'$'}nameFileHtml
                    
                    ${'$'}renamefile = 'E:\BuildAgent\work\ContinuosIntegration\server\report\unittest\' + ${'$'}nameFileHtml
                    Rename-Item -Path ${'$'}renamefile -NewName 'unitTest.html'
                """.trimIndent()
            }
        }
        powerShell {
            name = "Install requirements UnitTest"
            workingDir = "client/unitTest"
            scriptMode = script {
                content = "%Run_UnitTest_FE%"
            }
            scriptExecMode = PowerShellStep.ExecutionMode.STDIN
            noProfile = false
        }
        script {
            name = "Update mocks"

            conditions {
                equals("Run_UnitTest_FE", "npm install --loglevel=error")
            }
            workingDir = "client/unitTest"
            scriptContent = "node updateMocks.js"
        }
        script {
            name = "Run Unit test"

            conditions {
                equals("Run_UnitTest_FE", "npm install --loglevel=error")
            }
            workingDir = "client/unitTest"
            scriptContent = "node utlauncher.js --reporters teamcity,kjhtml,progress,html,coverage"
        }
        powerShell {
            name = "Run Unit test (1)"
            enabled = false

            conditions {
                equals("Run_UnitTest_FE", "npm install --loglevel=error")
            }
            scriptMode = script {
                content = "node utlauncher.js --reporters teamcity,kjhtml,progress,html,coverage"
            }
        }
        powerShell {
            name = "Get the Chrome version and update the environment variable"
            scriptMode = script {
                content = """
                    ${'$'}ChromeVersion = Get-ChildItem "client\unitTest\coverage\" -directory | Sort-Object CreationTime -Descending | Select-Object -First 1
                    
                    Write-Host "##teamcity[setParameter name='env.ChromeVersion' value='${'$'}ChromeVersion']"
                """.trimIndent()
            }
        }
        powerShell {
            name = "Stop Process Chrome"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptMode = script {
                content = """
                    Stop-Process -Name Chrome
                    Stop-Process -Name powershell
                """.trimIndent()
            }
        }
        powerShell {
            name = "Check CC Percentage and UT status"
            scriptMode = script {
                content = """
                    ${'$'}pathUT = "E:\BuildAgent\work\ContinuosIntegration\client\unitTest\karma_html\report-summary-filename\index.html"
                    ${'$'}pathUTCC = "E:\BuildAgent\work\ContinuosIntegration\client\unitTest\coverage"
                    ${'$'}pathToJson = "E:\ContinuousIntegration_Emails\FSRdata.json"
                    
                    ${'$'}json = Get-Content ${'$'}pathToJson | ConvertFrom-Json
                    ${'$'}email = ${'$'}json.user
                    
                    Write-OutPut ${'$'}json
                    
                    ${'$'}UTPassed = 0
                    ${'$'}UTFailed = 0
                    ${'$'}UTCC = 0
                      
                      #UNIT TEST 
                    
                    if (Test-Path ${'$'}pathUT) {
                    
                        ${'$'}xdocUT = (Get-Content -Path ${'$'}pathUT -TotalCount 24)
                        
                        ${'$'}UTPassed = ${'$'}xdocUT[18] -replace "tests /" -replace "" 
                        ${'$'}UTFailed = ${'$'}xdocUT[20] -replace "failures /" -replace "" 
                    
                        ${'$'}UTPassed = ${'$'}UTPassed.Trim()
                        ${'$'}UTFailed = ${'$'}UTFailed.Trim()
                    
                    }
                    
                    Write-OutPut "UT Passed: ${'$'}UTPassed"
                    Write-OutPut "UT Failures: ${'$'}UTFailed"
                    
                    
                    #code coverage
                    if (Test-Path ${'$'}pathUTCC) {
                        ${'$'}getFolderNames = (Get-ChildItem ${'$'}pathUTCC).Name 
                        ${'$'}lastVersionBuildName = ${'$'}getFolderNames | Measure-Object -Maximum
                        Write-Host "Last chrome version:" ${'$'}lastVersionBuildName.Maximum -ForegroundColor Cyan
                    
                    
                        ${'$'}pathUTCC = ${'$'}pathUTCC + "\" + ${'$'}lastVersionBuildName.Maximum + "\index.html"
                        # Write-Host "pathUTCC: ${'$'}pathUTCC"
                    
                        ${'$'}xdocUT = (Get-Content -Path ${'$'}pathUTCC -TotalCount 24)
                    
                        ${'$'}UTCC = ${'$'}xdocUT[22] -replace '<span class="strong">' -replace "" 
                        ${'$'}UTCC = ${'$'}UTCC -replace '% </span>' -replace "" 
                        ${'$'}UTCC = ${'$'}UTCC.Trim()
                        ${'$'}UTCC = [math]::truncate(${'$'}UTCC) # truncar número
                    }
                    
                    Write-OutPut "UTCC: "${'$'}UTCC
                    
                    
                    # Read current CC from json 
                    ${'$'}CurrentUTCC = ${'$'}json.cc
                    Write-OutPut "Last cc: ${'$'}CurrentUTCC"
                    
                    if(${'$'}email -ne "devoperations@polyworksmexico.com"){
                        if(${'$'}UTPassed -gt 0) #greater than 
                        {
                            if(${'$'}UTFailed -eq 0)
                            {
                                if(${'$'}UTCC -lt ${'$'}CurrentUTCC) #less than 
                                {
                                    Write-OutPut "status 1 bajo CC"
                                    ${'$'}json.status = "1"
                                    ${'$'}json | ConvertTo-Json | set-content ${'$'}pathToJson
                                    Write-output "Updating Status 1 data in json file."
                                }
                                else
                                {
                                    Write-OutPut "status 0 todo bien "
                                    ${'$'}json.status = "0"
                                    ${'$'}json.cc = "${'$'}UTCC"
                                    ${'$'}json | ConvertTo-Json | set-content ${'$'}pathToJson
                                    Write-output "Updating Status 0 data in json file."
                                    #E:\FSRemails\mailSender.ps1 -status 0 -previousCC ${'$'}CurrentUTCC -currentCC ${'$'}UTCC -name ${'$'}json.name -email ${'$'}json.user -tcFSRpath ${'$'}tcFSRpath
                                }
                            }
                            else
                            {
                                    Write-OutPut "status 2 unit test failed."
                                    ${'$'}json.status = "2"
                                    ${'$'}json | ConvertTo-Json | set-content ${'$'}pathToJson
                                    Write-output "Updating Status 2 data in json file."
                            }
                        }
                        else
                        {
                            Write-OutPut "status 3 the unit tests could not be executed"
                            ${'$'}json.status = "3"
                            ${'$'}json | ConvertTo-Json | set-content ${'$'}pathToJson
                            Write-output "Updating Status 2 data in json file."
                        }
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Send notification email"
            scriptMode = script {
                content = """
                    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                    ${'$'}tcFSRpath = "https://pw0.mx.polyworks.com:8443/viewType.html?buildTypeId=DataloopWebInterface_2_Fsr&tab=buildTypeStatusDiv"
                    ${'$'}pathToJson = "E:\ContinuousIntegration_Emails\FSRdata.json"
                    
                    ${'$'}json = Get-Content ${'$'}pathToJson | ConvertFrom-Json
                    ${'$'}name = ${'$'}json.name
                    ${'$'}mail = ${'$'}json.user
                    ${'$'}status = ${'$'}json.status
                    ${'$'}previousCC = ${'$'}json.cc
                    
                    ${'$'}emailFrom = "devOperations@polyworksmexico.com"
                    
                    Write-output "Status: ${'$'}status"
                    
                    
                    if(${'$'}status -gt 0)
                    {
                        ${'$'}SMTPServer = "smtp.office365.com"
                        ${'$'}Username = "devOperations@polyworksmexico.com";
                        ${'$'}Password = "Bat94523";
                    
                        if(${'$'}status -eq 1)
                        {
                            ${'$'}subject = "Continuous integration - CODE COVERAGE REPORT";
                            ${'$'}message = "Hello ${'$'}name, the code coverage percentage in the project decreased since the last commit.`n`nCC before commit: ${'$'}previousCC%`n`nCheck the results here: ${'$'}tcFSRpath";
                        }
                    
                        elseif(${'$'}status -eq 2)
                        {	
                        	Write-Host "Status 2: TEST FAILED message" ;
                            ${'$'}subject = "Continuous integration - TEST FAILED";
                            ${'$'}message = "Hello ${'$'}name, at least one unit test failed.`n`nCheck the results here: ${'$'}tcFSRpath";
                        }
                    
                        elseif(${'$'}status -eq 3)
                        {
                    
                            ${'$'}subject = "Continuous integration - ERROR";
                            ${'$'}message = "Hello ${'$'}name, the unit tests could not be executed.`n`nCheck the results here: ${'$'}tcFSRpath";
                        }
                        
                        Write-output "Message: ${'$'}message"
                    
                        ${'$'}smtp = New-Object Net.Mail.SmtpClient(${'$'}SMTPServer, 587);
                        ${'$'}smtp.EnableSSL = ${'$'}true;
                        ${'$'}smtp.Credentials = New-Object System.Net.NetworkCredential(${'$'}Username, ${'$'}Password);
                        ${'$'}smtp.Send(${'$'}Username, ${'$'}mail, ${'$'}subject, ${'$'}message);
                        write-host "Mail Sent" ;
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "Delete virtual directory"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptMode = script {
                content = """
                    ${'$'}agent = '%system.agent.name%'
                    Write-Host 'Agente ' ${'$'}agent
                    
                    if (${'$'}agent -eq 'DLWI_Agent_1' -or ${'$'}agent -eq 'DLWI_Agent_2') { #50.60 o 50.67
                    	Remove-WebApplication -Name "\polyworks\rest" -Site "Default Web Site"
                    	Remove-Item "IIS:\Sites\Default Web Site\polyworks\dataloop" -Force -Recurse
                    } else { #50.27
                    	Write-Host "Run in DLWI_Agent_old not Delete virtual directory"
                    }
                """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
            triggerRules = """
                +:client/**
                +:server/**
                -:server/DataLoopWebInterfaceServer/DataLoopWebInterfaceServer.API/localization/**
                -:versioning/*
            """.trimIndent()
            branchFilter = """
                +:epic/2022.9/DLWI-5994-hide-show-all-annotations
                +:epic/2023.4/DLWI-6648-Ttest
                +:epic/2023.8/DLWI-7216-updateAngularJs1.4.7To1.8.2
            """.trimIndent()
        }
        vcs {
            triggerRules = """
                +:client/**
                +:server/**
                -:server/DataLoopWebInterfaceServer/DataLoopWebInterfaceServer.API/localization/**
                -:versioning/*
            """.trimIndent()
            branchFilter = "+:release/*"
        }
    }

    failureConditions {
        executionTimeoutMin = 90
        nonZeroExitCode = false
        errorMessage = true
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "HTML fail"
            failureMessage = "CodeAnalysis HTML fail"
            reverse = false
        }
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "JS fail"
            failureMessage = "CodeAnalysis JS fail"
            reverse = false
        }
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "CSS fail"
            failureMessage = "CodeAnalysis CSS fail"
            reverse = false
        }
        failOnMetricChange {
            metric = BuildFailureOnMetric.MetricType.TEST_FAILED_COUNT
            threshold = 10
            units = BuildFailureOnMetric.MetricUnit.PERCENTS
            comparison = BuildFailureOnMetric.MetricComparison.LESS
            compareTo = build {
                buildRule = lastSuccessful()
            }
        }
    }

    requirements {
        contains("teamcity.agent.name", "DLWI")
    }
})
