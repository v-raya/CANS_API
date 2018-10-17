def dockerImageName = 'cwds/cans-api'
def testsDockerImageName = 'cwds/cans-api-test'
def dockerCredentialsId = '6ba8d05c-ca13-4818-8329-15d41a089ec0'

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
    emailext(
            subject: subject,
            body: details,
            attachLog: true,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            to: "Denys.Davydov@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
    )
}


node('linux') {
    def serverArti = Artifactory.server 'CWDS_DEV'
    def rtGradle = Artifactory.newGradleBuild()
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
                parameters([
                        string(defaultValue: 'master', description: '', name: 'branch'),
                        string(defaultValue: '', description: 'Used for mergerequest default is empty', name: 'refspec'),
                ])
    ])
    try {
        stage('Preparation') {
            checkout([$class: 'GitSCM', branches: [[name: '$branch']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', refspec: '$refspec', url: 'git@github.com:ca-cwds/cans-api.git']]])
            rtGradle.tool = "Gradle_35"
            rtGradle.resolver repo: 'repo', server: serverArti
            rtGradle.useWrapper = true
        }
        stage('Build') {
            echo("BUILD_NUMBER: ${BUILD_NUMBER}")
            rtGradle.run buildFile: 'build.gradle', tasks: 'jar'
        }
        stage('Unit Tests') {
            rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport', switches: '--stacktrace'
        }
        stage('SonarQube analysis') {
            withSonarQubeEnv('Core-SonarQube') {
                rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
            }
        }
        stage('Build Docker Images') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: 'createDockerImage'
                )
                rtGradle.run(
                        buildFile: 'build.gradle',
                        tasks: 'dockerTestsCreateImage'
                )
            }
        }
        stage('Run docker-compose environment') {
            withDockerRegistry([credentialsId: dockerCredentialsId]) {
                sh "docker-compose up -d"
                sh "sleep 120"
            }
        }
        stage('Run Functional Tests') {
            sh "docker-compose exec -T -e TEST_TYPE=functional cans-api-test ./entrypoint.sh"
        }
        stage('Performance Tests (Short Run)') {
            sh "docker-compose exec -T -e TEST_TYPE=performance cans-api-test ./entrypoint.sh"
        }
    } catch (Exception e) {
        errorcode = e
        currentBuild.result = "FAIL"
        notifyBuild(currentBuild.result, errorcode)
        throw e
    } finally {
        archiveArtifacts allowEmptyArchive: true, artifacts: 'version.txt', fingerprint: true, onlyIfSuccessful: true
        fingerprint 'version.txt'
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/tests/test', reportFiles: 'index.html', reportName: 'JUnit Report', reportTitles: 'JUnit tests summary'])
        sh "docker-compose down || true"
        sh "docker rmi $dockerImageName || true"
        sh "docker rmi $testsDockerImageName || true"
        cleanWs()
    }
}
