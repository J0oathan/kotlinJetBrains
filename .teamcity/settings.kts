import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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

version = "2023.05"

project {

    vcsRoot(SistemaDeCitas_HttpsGithubComJ0oathanSistemaDeCitasRefsHeadsMaster)

    buildType(JcruzCustom1302_Build)
    buildType(JcruzCustom1302_Utils)
}

object JcruzCustom1302_Build : BuildType({
    id("Build")
    name = "Maven Kotlin"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "my custom step name"
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

object JcruzCustom1302_Utils : BuildType({
    id("Utils")
    name = "Utils"
    description = "recurrent task"

    steps {
        script {
            name = "permisions"
            scriptContent = """rd /s /q C:\${'$'}Recycle.bin"""
        }
        powerShell {
            name = "Delete recycle bin"
            scriptMode = script {
                content = """
                    ${'$'}log = "C:\ScriptsCustoms\garbageCleanup.log"
                    
                    ${'$'}Shell = New-Object -ComObject Shell.Application
                    ${'$'}RecBin = ${'$'}Shell.Namespace(0xA)
                    ${'$'}RecBin.Items() | %{Remove-Item ${'$'}_.Path -Recurse -Confirm:${'$'}false}
                    
                    Write-OutPut "Recycle bin deleted"
                    
                    ${'$'}currentDate = Get-Date
                    
                    Add-Content ${'$'}log "${'$'}ClearedSpace MB cleared at ${'$'}currentDate"
                """.trimIndent()
            }
            noProfile = false
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

object SistemaDeCitas_HttpsGithubComJ0oathanSistemaDeCitasRefsHeadsMaster : GitVcsRoot({
    id = AbsoluteId("SistemaDeCitas_HttpsGithubComJ0oathanSistemaDeCitasRefsHeadsMaster")
    name = "https://github.com/J0oathan/SistemaDeCitas#refs/heads/master"
    url = "https://github.com/J0oathan/SistemaDeCitas"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/master2023"
    authMethod = password {
        userName = "J0oathan"
        password = "credentialsJSON:cb92e419-e5e2-4baf-b649-6214926718d2"
    }
})
