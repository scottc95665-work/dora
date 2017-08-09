def notifyBuild(String buildStatus, Exception e) {
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

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
    details +="<p>Error message ${e.message}, stacktrace: ${e}</p>"
    summary +="\nError message ${e.message}, stacktrace: ${e}"
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

node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'latest', description: '', name: 'APP_VERSION'), string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')]), pipelineTriggers([githubPush()])])
   def errorcode = null;
   def buildInfo = '';

 try {

   stage('Preparation') {
          cleanWs()
		  checkout([$class: 'GitSCM', branches: [[name: '*/development']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/dora.git']]])
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti
   }
   stage('Build'){
		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'jar'
   }
   stage('Unit Tests') {
		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'test jacocoTestReport'
   }
   stage('License Report') {
   		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'downloadLicenses'
   }
   stage('SonarQube analysis'){
		withSonarQubeEnv('Core-SonarQube') {
			buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
		}
    }
	stage ('Push to Artifactory'){
	    rtGradle.deployer repo:'libs-snapshot', server: serverArti
	    //rtGradle.deployer repo:'libs-release', server: serverArti
	    rtGradle.deployer.deployArtifacts = true
		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'artifactoryPublish'
		rtGradle.deployer.deployArtifacts = false
	}
	stage ('Build Docker'){
	  withEnv(['ELASTIC_HOST=127.0.0.1']) {
	        buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'printConfig'
	        buildInfo = rtGradle.run buildFile: './docker-dora/build.gradle', tasks: 'dockerCreateImage'
	        withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
                    buildInfo = rtGradle.run buildFile: './docker-dora/build.gradle', tasks: 'dockerDoraPublish'
            }
    }
	}
    stage('Archive Artifacts') {
		    archiveArtifacts artifacts: '**/dora*.jar,readme.txt', fingerprint: truels -la
	}
	stage('Deploy Application'){
	   git changelog: false, credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', poll: false, url: 'git@github.com:ca-cwds/de-ansible.git'
	   sh 'ansible-playbook -e DORA_API_VERSION=$APP_VERSION -i $inventory deploy-dora.yml --vault-password-file ~/.ssh/vault.txt -vv'
	}
 } catch (Exception e)    {
	   errorcode = e;
	   currentBuild.result = "FAIL"
	   notifyBuild(currentBuild.result,errorcode)
	   throw e;

	}finally {
   		publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/license/', reportFiles: 'license-dependency.html', reportName: 'License Report', reportTitles: 'License summary'])
   		publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'dora-api/build/reports/tests/test/', reportFiles: 'index.html', reportName: 'JUnit Reports', reportTitles: 'JUnit tests summary'])
	}
}