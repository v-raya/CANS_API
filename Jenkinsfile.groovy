@Library('jenkins-pipeline-utils') _

def gitHubUrl = 'https://github.com/ca-cwds/cans-api.git'
def ansibleGitHubUrl = 'git@github.com:ca-cwds/de-ansible.git'
def dockerImageName = 'cwds/cans-api'

def artifactoryServerId = 'CWDS_DEV'
def sonarQubeServerName = 'Core-SonarQube'
def dockerCredentialsId = '6ba8d05c-ca13-4818-8329-15d41a089ec0'
def github_credentials_id = '433ac100-b3c2-4519-b4d6-207c029a103b'
def ansibleScmCredentialsId = '433ac100-b3c2-4519-b4d6-207c029a103b'

def javaEnvProps
def newTag

// tests variables
def testsDockerImageName = 'cwds/cans-api-test'
def cansApiUrl = 'https://cans-api.dev.cwds.io'
def smokeTestsDockerEnvVars = " -e CANS_API_URL=$cansApiUrl "
def functionalTestsDockerEnvVars = smokeTestsDockerEnvVars +
        ' -e TEST_TYPE=functional' +
        ' -e PERRY_URL=https://web.dev.cwds.io';
def performanceTestsDockerEnvVars = ' -e TEST_TYPE=performance' +
        ' -e JM_TARGET=api' +
        ' -e JM_PERRY_MODE=DEV' +
        ' -e JM_USERS_COUNT=3' +
        ' -e JM_UPDATE_REQUESTS_PER_USER=3' +
        ' -e JM_PERRY_PROTOCOL=https' +
        ' -e JM_PERRY_HOST=web.dev.cwds.io' +
        ' -e JM_PERRY_PORT=443' +
        ' -e JM_CANS_API_PROTOCOL=https' +
        ' -e JM_CANS_API_HOST=cansapi.dev.cwds.io' +
        ' -e JM_USER_COUNTY_CODE=20' +
        ' -e JM_WEB_DRIVER_PATH=/usr/local/bin/chromedriver' +
        ' -e JM_CANS_API_PORT=443';

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

node('linux') {
    def artifactoryServer = Artifactory.server artifactoryServerId
    def rtGradle = Artifactory.newGradleBuild()
// DO NOT DELETE THIS BLOCK
//    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
//                parameters([
//                        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
//                        string(defaultValue: 'master', description: '', name: 'branch'),
//                        booleanParam(defaultValue: true, description: 'Runs liquibase ddl on application start', name: 'UPGRADE_CANS_DB_ON_START'),
//                        booleanParam(defaultValue: false, description: '', name: 'ONLY_TESTING'),
//                        booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
//                        string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
//                        booleanParam(defaultValue: true, description: '', name: 'USE_NEWRELIC'),
//                        string(defaultValue: 'inventories/cans/hosts.yml', description: '', name: 'inventory'),
//                ]), pipelineTriggers([pollSCM('H/5 * * * *')])])
// DO NOT DELETE THIS BLOCK
    try {
        stage('Preparation') {
            cleanWs()
            checkout scm
            checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'ansible']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: ansibleScmCredentialsId, url: ansibleGitHubUrl]]]
            rtGradle.tool = 'Gradle_35'
            rtGradle.resolver repo: 'repo', server: artifactoryServer
            rtGradle.useWrapper = true
        }
        stage('Increment Tag') {
          newTag = newSemVer()
        }

        stage('Build') {
            echo("RELEASE: ${params.RELEASE_PROJECT}")
            echo("BUILD_NUMBER: ${BUILD_NUMBER}")
            echo("ONLY_TESTING: ${ONLY_TESTING}")
            echo("newTag: ${newTag}")
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
        if ("${params.ONLY_TESTING}" == "true") {
            currentBuild.result = 'SUCCESS'
            return
        }
        stage('Tag Git') {
           tagGithubRepo(newTag, github_credentials_id)
        }
        stage('Push to artifactory') {
            rtGradle.deployer.deployArtifacts = true
            rtGradle.run(
                    buildFile: 'build.gradle',
                    tasks: "publish -DRelease=true -DnewVersion=${newTag}".toString()
            )
            rtGradle.deployer.deployArtifacts = false
        }
        stage('Build Docker Image') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: "pushDockerLatest -DRelease=true -DnewVersion=${newTag}".toString()
                )
            }
        }
        stage('Build Tests Docker Image') {
            rtGradle.run(
                    buildFile: 'build.gradle',
                    tasks: "dockerTestsCreateImage -DRelease=true -DnewVersion=${newTag}".toString()
            )
        }
        stage('Archive Artifacts') {
            archiveArtifacts artifacts: '**/cans-api-*.jar,readme.txt', fingerprint: true
        }
        stage('Deploy Application') {
            sh 'cd ansible ; ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e APP_VERSION=$APP_VERSION -e UPGRADE_CANS_DB_ON_START=$UPGRADE_CANS_DB_ON_START -i $inventory deploy-cans-api.yml --vault-password-file ~/.ssh/vault.txt -vv'
        }
        stage('Smoke Tests') {
            sh "docker run --rm $smokeTestsDockerEnvVars $testsDockerImageName:$APP_VERSION"
        }
        stage('Functional Tests') {
            sh "docker run --rm $functionalTestsDockerEnvVars $testsDockerImageName:$APP_VERSION"
        }
        stage('Performance Tests (Short Run)') {
            sh "docker run --rm -v `pwd`/performance-results-api:/opt/cans-api-perf-test/results/api $performanceTestsDockerEnvVars $testsDockerImageName:$APP_VERSION"
            perfReport errorFailedThreshold: 10, errorUnstableThreshold: 5, modeThroughput: true, sourceDataFiles: '**/resultfile'
        }
        stage('Publish Docker Image') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: 'publishDocker' + javaEnvProps
                )
            }
        }
        stage('Publish Tests Docker Image') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: ':docker-tests:dockerTestsPublish' + javaEnvProps
                )
            }
        }
        stage('Trigger Security scan') {
            def props = readProperties file: 'build/resources/main/version.properties'
            def build_version = props["build.version"]
            sh "echo build_version: ${build_version}"
            build job: 'tenable-scan', parameters: [
                    [$class: 'StringParameterValue', name: 'CONTAINER_NAME', value: 'cans-api'],
                    [$class: 'StringParameterValue', name: 'CONTAINER_VERSION', value: "${build_version}"]
            ]
        }
        stage('Deploy to Pre-int') {
          withCredentials([usernameColonPassword(credentialsId: 'fa186416-faac-44c0-a2fa-089aed50ca17', variable: 'jenkinsauth')]) {
            sh "curl -u $jenkinsauth 'http://jenkins.mgmt.cwds.io:8080/job/PreInt-Integration/job/deploy-cans-api/buildWithParameters?token=deployCansApiToPreint&version=${newTag}'"
          }
        }
        stage('Update Pre-int Manifest') {
          updateManifest("cans-api", "preint", github_credentials_id, newTag)
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

