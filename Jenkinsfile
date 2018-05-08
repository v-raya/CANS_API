def gitHubUrl = 'https://github.com/ca-cwds/cans-api.git'
def ansibleGitHubUrl = 'git@github.com:ca-cwds/de-ansible.git'
def dockerImageName = 'cwds/cans-api'

def artifactoryServerId = 'CWDS_DEV'
def sonarQubeServerName = 'Core-SonarQube'
def dockerCredentialsId = '6ba8d05c-ca13-4818-8329-15d41a089ec0'
def ansibleScmCredentialsId = '433ac100-b3c2-4519-b4d6-207c029a103b'

def javaEnvProps = ' -DRelease=$RELEASE_PROJECT -DBuildNumber=$BUILD_NUMBER -DCustomVersion=$OVERRIDE_VERSION '

// tests variables
def db2dataCredentialsId = 'db2data'
def testsDockerImageName = 'cwds/cans-api-tests'
def cansApiUrl = 'http://cans.dev.cwds.io:8089'
def smokeTestsDockerEnvVars = " -e CANS_API_URL=$cansApiUrl "
def integrationTestsDockerEnvVars = smokeTestsDockerEnvVars +
        ' -e TEST_TYPE=integration' +
        ' -e PERRY_URL=https://web.dev.cwds.io' +
        ' -e DB_CMS_JDBC_URL=jdbc:db2://db2.dev.cwds.io:50000/DB0TDEV' +
        ' -e DB_CMS_SCHEMA=CWSINT' +
        ' -e DB_CMS_USER=$DB_USERNAME' +
        ' -e DB_CMS_PASSWORD=$DB_PASSWORD' +
        ' -e DB_RS_JDBC_URL=jdbc:db2://db2.dev.cwds.io:50000/DB0TDEV' +
        ' -e DB_RS_SCHEMA=CWSRS1' +
        ' -e DB_RS_USER=$DB_USERNAME' +
        ' -e DB_RS_PASSWORD=$DB_PASSWORD '

def notifyBuild(String buildStatus, Exception e) {
    buildStatus = buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = """*${buildStatus}*: Job '${env.JOB_NAME} [${
        env.BUILD_NUMBER
    }]':\nMore detail in console output at <${env.BUILD_URL}|${env.BUILD_URL}>"""
    def details = """${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\n
    Check console output at ${env.BUILD_URL} """
    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        color = 'YELLOW'
        colorCode = '#FFFF00'
    } else if (buildStatus == 'SUCCESSFUL') {
        color = 'GREEN'
        colorCode = '#00FF00'
    } else {
        color = 'RED'
        colorCode = '#FF0000'
        details += "<p>Error message ${e.message}, stacktrace: ${e}</p>"
        summary += "\nError message ${e.message}, stacktrace: ${e}"
    }

    // Send notifications
    //slackSend channel: "#cans-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt3', color: colorCode, message: summary
    emailext(
            subject: subject,
            body: details,
            attachLog: true,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            to: "maksym.ivanov@osi.ca.gov"
    )
}

def publishUnitTestsHtml() {
    echo("Publish Unit Tests Html")
    publishHTML([
            allowMissing         : true,
            alwaysLinkToLastBuild: true,
            keepAll              : true,
            reportDir            : 'build/reports/tests/test',
            reportFiles          : 'index.html',
            reportName           : 'JUnitReports',
            reportTitles         : 'JUnit tests summary'
    ])
}

def publishLicenseReportHtml() {
    echo("Publish License Report Html")
    publishHTML([
            allowMissing         : true,
            alwaysLinkToLastBuild: true,
            keepAll              : true,
            reportDir            : 'build/reports/license',
            reportFiles          : 'license-dependency.html',
            reportName           : 'License Report',
            reportTitles         : 'License summary'
    ])
}

node('tpt3-slave') {
    def artifactoryServer = Artifactory.server artifactoryServerId
    def rtGradle = Artifactory.newGradleBuild()
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
                parameters([
                        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
                        string(defaultValue: 'development', description: '', name: 'branch'),
						booleanParam(defaultValue: false, description: '', name: 'ONLY_TESTING'),
                        booleanParam(defaultValue: false, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
                        string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
                        booleanParam(defaultValue: true, description: '', name: 'USE_NEWRELIC'),
                        string(defaultValue: 'inventories/tpt3dev/hosts.yml', description: '', name: 'inventory'),
                ]), pipelineTriggers([pollSCM('H/5 * * * *')])])
    try {
        stage('Preparation') {
            cleanWs()
            git branch: '$branch', url: gitHubUrl
            rtGradle.tool = 'Gradle_35'
            rtGradle.resolver repo: 'repo', server: artifactoryServer
            rtGradle.useWrapper = true
        }
        stage('Build') {
            echo("RELEASE: ${params.RELEASE_PROJECT}")
            echo("BUILD_NUMBER: ${BUILD_NUMBER}")
			echo("ONLY_TESTING: ${ONLY_TESTING}")
            echo("OVERRIDE_VERSION: ${params.OVERRIDE_VERSION}")
            rtGradle.run(
                    buildFile: 'build.gradle',
                    tasks: 'jar' + javaEnvProps
            )
        }
		
        stage('Unit Tests') {
            rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: '--stacktrace'
            publishUnitTestsHtml()
        }
        stage('License Report') {
            rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
            publishLicenseReportHtml()
        }
        stage('SonarQube analysis') {
            withSonarQubeEnv(sonarQubeServerName) {
                rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
            }
        }
		if("${params.ONLY_TESTING}" == "true") {
			currentBuild.result = 'SUCCESS'
			return
			}
        stage('Push to artifactory') {
            rtGradle.deployer.deployArtifacts = true
            rtGradle.run(
                    buildFile: 'build.gradle',
                    tasks: 'publish ' + javaEnvProps
            )
            rtGradle.deployer.deployArtifacts = false
        }
        stage('Publish Docker Image') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: 'publishDocker' + javaEnvProps
                )
            }
        }
        stage('Build Tests Docker Image') {
            rtGradle.run(
                    buildFile: 'build.gradle',
                    tasks: 'dockerTestsCreateImage' + javaEnvProps
            )
        }
        stage('Archive Artifacts') {
            archiveArtifacts artifacts: '**/cans-api-*.jar,readme.txt', fingerprint: true
        }
        stage('Deploy Application') {
            cleanWs()
            checkout(
                    changelog: false,
                    poll: false,
                    scm: [
                            $class                           : 'GitSCM',
                            branches                         : [[name: '*/master']],
                            doGenerateSubmoduleConfigurations: false,
                            extensions                       : [],
                            submoduleCfg                     : [],
                            userRemoteConfigs                : [[credentialsId: ansibleScmCredentialsId, url: ansibleGitHubUrl]]
                    ]
            )
            sh 'ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e APP_VERSION=$APP_VERSION -i $inventory deploy-cans-api.yml --vault-password-file ~/.ssh/vault.txt -vv'
            sleep(150)
        }
        stage('Smoke Tests') {
            sh "docker run --rm $smokeTestsDockerEnvVars $testsDockerImageName:$APP_VERSION"
        }
        stage('Integration Tests') {
            withCredentials([[
                     $class: 'UsernamePasswordMultiBinding',
                     credentialsId: db2dataCredentialsId,
                     usernameVariable: 'DB_USERNAME',
                     passwordVariable: 'DB_PASSWORD']]) {
                sh "docker run --rm $integrationTestsDockerEnvVars $testsDockerImageName:$APP_VERSION"
            }
        }
        stage('Publish Tests Docker Image') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                sh "docker push $testsDockerImageName:$APP_VERSION"
            }
        }
    } catch (Exception e) {
        errorcode = e
        currentBuild.result = "FAIL"
        notifyBuild(currentBuild.result, errorcode)
        throw e
    } finally {
        sh "docker rmi $dockerImageName || true"
        sh "docker rmi $testsDockerImageName || true"
        cleanWs()
    }
}
