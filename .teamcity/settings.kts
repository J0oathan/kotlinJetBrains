import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.projectFeatures.bitbucketCloudConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs

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

    features {
        bitbucketCloudConnection {
            id = "PROJECT_EXT_2"
            displayName = "Bitbucket Cloud"
            key = "devoperations_pwm"
            clientSecret = "credentialsJSON:0e0a28a6-596f-44ba-9fb2-6a29c58c98bc"
        }
    }
}

object Build : BuildType({
    name = "Maven Kotlin 2024"

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
    name = "Test second BC 2024"
    description = "Create new file"

    steps {
        powerShell {
            scriptMode = script {
                content = """
                    ${'$'}FilePath = "C:\ScriptsCustoms\MyFile2.txt"
                     
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
