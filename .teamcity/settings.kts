import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.projectFeatures.bitbucketCloudConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubAppConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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

    vcsRoot(HttpsGithubComJ0oathanPokemonsRefsHeadsMaster)

    buildType(Build)
    buildType(TestSecondBc)

    features {
        bitbucketCloudConnection {
            id = "PROJECT_EXT_2"
            displayName = "Bitbucket Cloud"
            key = "devoperations_pwm"
            clientSecret = "credentialsJSON:0e0a28a6-596f-44ba-9fb2-6a29c58c98bc"
        }
        githubConnection {
            id = "PROJECT_EXT_3"
            displayName = "GitHub.com"
            clientId = "J0oathan"
            clientSecret = "credentialsJSON:28f48ddb-9392-414d-8732-cfe9f52c6d00"
        }
        githubAppConnection {
            id = "PROJECT_EXT_4"
            displayName = "TeamCityCustomJcruz"
            appId = "693873"
            clientId = "Iv1.f9c86dfa817d84cb"
            clientSecret = "credentialsJSON:67ccfc65-55a8-47bc-ad4d-addf5dfec753"
            privateKey = "credentialsJSON:a21e8d67-95f0-4ddf-8286-9ab11295f1c4"
            ownerUrl = "https://github.com/J0oathan"
        }
    }
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

    vcs {
        root(HttpsGithubComJ0oathanPokemonsRefsHeadsMaster)
    }

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

object HttpsGithubComJ0oathanPokemonsRefsHeadsMaster : GitVcsRoot({
    name = "gith"
    url = "https://github.com/J0oathan/pokemons"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = token {
        userName = "oauth2"
        tokenId = "tc_token_id:CID_be335eb4b881cf69fe03c3750742d1fc:-1:b0951779-991a-4a78-9da1-9ea328dba38b"
    }
})
