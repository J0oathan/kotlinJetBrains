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
    vcsRoot(Hola)

    buildType(Build)
    buildType(TestSecondBc)

    features {
        bitbucketCloudConnection {
            id = "PROJECT_EXT_2"
            displayName = "Bitbucket Cloud"
            key = "devoperations_pwm"
            clientSecret = "zxx7ffb2d96f814141ae029cbed20565f7b88e0cfa715f954be"
        }
        githubConnection {
            id = "PROJECT_EXT_3"
            displayName = "GitHub.com"
            clientId = "J0oathan"
            clientSecret = "zxxd38e666bff5c7c122415529f8732bed4"
        }
        githubAppConnection {
            id = "PROJECT_EXT_4"
            displayName = "TeamCityCustomJcruz"
            appId = "693873"
            clientId = "Iv1.f9c86dfa817d84cb"
            clientSecret = "zxx379b1285a9ffe178cd5560c2ff64299af292b7a3134281dea0f7560f878f532411828edd3803ed09775d03cbe80d301b"
            privateKey = "zxx0ead0f89b53aab8c18829126191f80d8f851ffcd3e40676b5c7ef97fc4e4128b024ef5cf5973ed28bb3dcb2414a832ecef31078429e646af7c2abe17db76c7380478323d2f608295f10b928c723ee2ee0f930acda89a0a5240b583688fc5368cc2783312b401dc76fe413f028bb0350155c272d0680c52fb00b60bdcd2f5fdcc6f6ccb891c56b1e210d475af51c6e964733bbe56fbd833a8bf81c6230c267bd8ef6ac0b00516598a930c8ef8fec9bc9c162cc547a30ab3b218a948fa3a33ac2557abe0fd1dda415cdbda4053e0522d156c488cd9f6d66184f5cf7410be9d6a2f00207f6e3dc61be7a6ec2905a024d0426e3821268cd2f887b8bf10b7369d036cb438b6dfdbd12ab75ee0336b4b1c3e24c06b17979ce0402d50a8c9ea5ed7922cd06a32eb85cdfb280264d5697a56f91c3a1c84a54cba4a3355e022de9912537d42b41fb46d1dbd2110415ccd7aff24b83375834102d1991216b438345e7ef3d0168dc6c0809a1cbf0ae68bd1aa14f9143e9b86d717b3fcaf583dfbb0d094f534b66af41a8efe9ecb8b27c6ebde2b7160218c0b70381c59e80639d3fa73c6d5f2b114efe2d2ca9f9b0b2321db24e7ab2afd05f0f7c967a51d3e4ca1981990a21880909983b5008cfc6c0c7188da90aecc546e0df97543e3e7942be747b491b4aa80601b2a99bf36189cd45577ea3b9f165ad301a51f353efd5494ca41f5ea661b134225c3b4259f8ad9a64e43af56b78a8f9007d3744661048adb2f701e7908853f8d304948dba1d5960c1e7c7ee4c5e9a34b570d7df02bf3abdfd03952f0be8a764836a87617f1910ae147f2a9dfc8300d6bf347a781099ff93a9889b0271e378f8c9fbbde176aeac6d2e90a5409a80075380450d749b99dd9a163879ba6e874b5afd1333e57b84464778daf77e757df57cce44dd00381d972958344de5576ed7962ac49ee999f4ec013bea79916caf0c2fd81080e27306e4b1917e431872ef85bc9e91c59ff929af6e5681bc2a2619f9da54acf313cea7c25d8b44fa3dcffc2e8a2de05a2e5112b5bccfe253afbdfc06d834499e8ecdd5a05249f3dfe008900de8c386cdfe9fcf6e8d0c3670377eceddd06fb965bbd614e549a674d026afb260a423db6120373b3dbb1147fd015d000c5628c13c62f927a0e1755b3a95d1e29d184d2d2186fb13ff505c0677f1b22829e3a1208d4ddd1aef789a89b981b3c1f062018f7b85951fa22c6ae2be10944ed0de5c2ceff1907ddf33e713d335b82aba996f1a638b8935dd699347e0e9221264c2741d16ea8574d5cfd98dd2fbc2e7be120f7235d60c1fba3d5805f88174d4de8873083b7c5378d4e1b5e066f9a8d317033706f7694d24baf2c70c88c7cdc874d2ea286c93072a064c7a0583b8eeb12568155397b8195b973ce8968732cd045babfdeb2d6929552658ce377e4de2dac7b9c1d20f26de432c5e7eb9fbc3469485587ef4bca2a7a74d1c49861d5d0463f5db248d94caeb4a26c3aec1bf1be1cb92655cad1a2182ba25b7fd3d54592e955eff5b851b2a906a979fd17f39351b73395c01cee206fdeaa6ccc7039b3db1f5cdf7d713711f0521cdab36963e96c19244adfc707c3405dd05d9b9742a88074fc512b99c9742f4b4e0dd8ca146c65f4fd2e8e71a8ef52ebda9b12c7b958e73767a65b53a8afa7eccf4f9116d6ab654225d3d4ebf5bcd607d6b04cfb105f284d1a450f2f1d93a9d06ad23e13664eceae10ce3de6bcfbfca853c828fb52906dcad3fbb20e218334c8aa66ebd50592b2532d8b71195ec64c3274fc50d4fb666a5cefb0db290f4ce91c63ef0eed06fe7eefd621e73a2d617e0aefb8833146b5e545d240a3295a2b9949e35faca554d2a32ed31372fa2acecc14cf8f3f56b069b2ddd3d489231d8a95df5a72ced736e08ba1e23680702f0b472e511839529291410587d333ec13d43a81a6320c97db97aeb50c94c3a32d8ca45ce7ee67d09243be538c3016f346fb54f880c56a23b93e5b9f168868f12ade50094b7c535e2301ebc5529b741a8d73a773092a44f18fa0134f4b1db29860cec69faa02e6d36b2c38f154bd34514651b5f783355bf66f76c0a0472d55f939f6af80cde6f4b445ee35784e1c1876acb949428c59a4446e84ee52a96aa6f76fdb0373697e117061921eff1c802a15eed25ec241c4115159c64d2d120960c38f2dc6d09cbc6d0a2d245ed72f47742869c37e4d52a6e62f4c7abe88aca8f9a66277244ebd349cbc68db220329b60d7f4c4a84cc19163344e32652af6c6166d4182948a5b5ecf7f5eaa65a9eb63447034498dd3ddf5e90681562345677e6aa75a3dece9e38bff65766c9a8593dabe6eefdb3407f43576803c15fde2c42"
            ownerUrl = "https://github.com/J0oathan"
        }
    }
}

object Build : BuildType({
    name = "Maven Kotlin 2023"

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
    name = "Test second BC 2023"
    description = "Create new file"

    vcs {
        root(Hola)
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
        powerShell {
            name = "get log"
            id = "get_log"
            scriptMode = script {
                content = """
                    ${'$'}mail = git log -1 --pretty=format:'%ae'
                    ${'$'}name = git log -1 --pretty=format:'%an'
                    ${'$'}commit = git log -1 --pretty=format:'%cn'
                    ${'$'}Date = git log -1 --pretty=format:'%ai'
                    
                    Write-Host "mail" ${'$'}mail
                    Write-Host "name" ${'$'}name
                    Write-Host "commit" ${'$'}commit
                    Write-Host "Date" ${'$'}Date
                """.trimIndent()
            }
        }
    }
})

object Hola : GitVcsRoot({
    name = "hola"
    url = "https://github.com/J0oathan/pokemons2"
    branch = "release/2023.x"
    branchSpec = """
        +:refs/heads/FT/*
        +:refs/heads/BF/*
        +:refs/heads/*
    """.trimIndent()
    authMethod = token {
        userName = "oauth2"
        tokenId = "tc_token_id:CID_be335eb4b881cf69fe03c3750742d1fc:-1:c11ac663-a81c-4739-bbe3-35a295d5fb96"
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
