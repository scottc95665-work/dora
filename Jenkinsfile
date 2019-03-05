@Library('jenkins-pipeline-utils') _

def notifyBuild(String buildStatus, Exception e) {
    buildStatus = buildStatus ?: 'SUCCESSFUL'

    // Default values
    def colorName = 'RED'
    def colorCode = '#FF0000'
    def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
    def summary = """*${buildStatus}*: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\nMore detail in console output at <${env.BUILD_URL}|${env.BUILD_URL}>"""
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

    slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', color: colorCode, message: summary
    emailext(
            subject: subject,
            body: details,
            attachLog: true,
            recipientProviders: [[$class: 'DevelopersRecipientProvider']],
            to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
    )
}

node('dora-slave') {
    def serverArti = Artifactory.server 'CWDS_DEV'
    def rtGradle = Artifactory.newGradleBuild()
    def docker_credentials_id = '6ba8d05c-ca13-4818-8329-15d41a089ec0'
    def github_credentials_id = '433ac100-b3c2-4519-b4d6-207c029a103b'
    def newTag
    if (env.BUILD_JOB_TYPE=="master" ) {
    triggerProperties = pullRequestMergedTriggerProperties('dora-master')
    properties([pipelineTriggers([triggerProperties]), buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '50')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
    parameters([
        booleanParam(defaultValue: true, description: '', name: 'USE_NEWRELIC'),
        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
        string(defaultValue: 'master', description: '', name: 'branch'),
        booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
        string(description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
        string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')
     ]), pipelineTriggers([pollSCM('H/5 * * * *')])
     ])
     } else {
       properties([disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
       parameters([
         string(defaultValue: 'master', description: '', name: 'branch'),
         booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
         string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')])])
    }
    def errorcode = null;
    def buildInfo = '';

    try {
        stage('Preparation') {
            def scmInfo = checkout scm
            rtGradle.tool = "Gradle_35"
            rtGradle.resolver repo: 'repo', server: serverArti
            rtGradle.useWrapper = true
        }
        if (env.BUILD_JOB_TYPE=="master" ) {
             stage('Increment Tag') {
               newTag = newSemVer()
              }
        } else {
             stage('Check for Label') {
               checkForLabel("dora")
             }
       }
       stage('Build'){
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "jar -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
       }
       stage('Unit Tests') {
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport'
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'dora-api/build/reports/tests/test/', reportFiles: 'index.html', reportName: 'JUnit Reports', reportTitles: 'JUnit tests summary'])
        }

       stage('SonarQube analysis') {
            lint(rtGradle)
       }
       if (env.BUILD_JOB_TYPE=="master" ) {
          stage('License Report') {
             buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
          }
          stage('Tag Git') {
            tagGithubRepo(newTag, github_credentials_id)
          }
          stage('Push to Artifactory') {
             rtGradle.deployer.deployArtifacts = true
             buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "publish -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
             rtGradle.deployer.deployArtifacts = false
          }
          stage('Build Docker') {
            withEnv(['ELASTIC_HOST=127.0.0.1']) {
                buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "printConfig -DnewVersion=${newTag}".toString()
                buildInfo = rtGradle.run buildFile: './docker-dora/build.gradle', tasks: "dockerCreateImage -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
                withDockerRegistry([credentialsId: docker_credentials_id]) {
                    buildInfo = rtGradle.run buildFile: './docker-dora/build.gradle', tasks: "dockerDoraPublish -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
                }
            }
          }
          stage ('Build Tests Docker'){
            buildInfo = rtGradle.run buildFile: './dora-api/docker-tests/build.gradle', switches: '--stacktrace',  tasks: "dockerTestsCreateImage -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
            withDockerRegistry([credentialsId: docker_credentials_id]) {
                buildInfo = rtGradle.run buildFile: './dora-api/docker-tests/build.gradle', switches: '--stacktrace',  tasks: "dockerTestsPublish -DRelease=\$RELEASE_PROJECT -DBuildNumber=\$BUILD_NUMBER -DCustomVersion=\$OVERRIDE_VERSION -DnewVersion=${newTag}".toString()
            }
          }
          stage('Archive Artifacts') {
            archiveArtifacts artifacts: '**/dora*.jar,readme.txt', fingerprint: true
          }
          stage('Deploy to Dev') {
            withDockerRegistry([credentialsId: docker_credentials_id]) {
               sh "cd localenv; docker-compose pull ; docker-compose up -d"
               sh "if [ ! -d /var/log/elasticsearch ]; then sudo mkdir /var/log/elasticsearch/; fi"
            }
            git changelog: false, credentialsId: github_credentials_id, poll: false, url: 'git@github.com:ca-cwds/de-ansible.git'
            sh 'ansible-playbook -e NEW_RELIC_AGENT=$USE_NEWRELIC -e DORA_API_VERSION=$APP_VERSION -i $inventory deploy-dora.yml --vault-password-file ~/.ssh/vault.txt -vv'
            cleanWs()
          }
          stage('Smoke Tests on Dev') {
            git branch: '$branch', url: 'https://github.com/ca-cwds/dora.git'
            sh "curl http://dora.dev.cwds.io:8083/system-information"
            buildInfo = rtGradle.run buildFile: './dora-api/build.gradle', tasks: 'smokeTest --stacktrace'
          }
          stage('Clean WorkSpace') {
           buildInfo = rtGradle.run buildFile: './docker-dora/build.gradle', tasks: 'dockerCleanUpTagged'
           cleanWs()
          }
          stage('Deploy to Pre-int and Integration') {
            withCredentials([usernameColonPassword(credentialsId: 'fa186416-faac-44c0-a2fa-089aed50ca17', variable: 'jenkinsauth')]) {
              sh "curl -u $jenkinsauth 'http://jenkins.mgmt.cwds.io:8080/job/PreInt-Integration/job/deploy-dora/buildWithParameters?token=deployDoraToPreint&version=${newTag}'"
            }
          }
      }
    } catch (Exception e) {
        errorcode = e;
        currentBuild.result = "FAIL"
        notifyBuild(currentBuild.result, errorcode)
        throw e;
    } finally {
        cleanWs()
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/license/', reportFiles: 'license-dependency.html', reportName: 'License Report', reportTitles: 'License summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'dora-api/build/reports/tests/test/', reportFiles: 'index.html', reportName: 'JUnit Reports', reportTitles: 'JUnit tests summary'])
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'dora-api/build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: 'Smoke Tests Reports', reportTitles: 'Smoke tests summary'])

    }
}
