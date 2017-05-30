node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'latest', description: '', name: 'APP_VERSION'), string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')]), pipelineTriggers([pollSCM('H/5 * * * *')])])

   catchError {

   stage('Preparation') {
		  git branch: 'development', credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/dora.git'
		  rtGradle.tool = "Gradle_35"
		  rtGradle.resolver repo:'repo', server: serverArti

		  
   }
   stage('Build'){
        // TODO: use gradlew (wrapper) for build
		def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'jar'
   }
    stage('CoverageCheck_and_Test') {
		buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'coberturaCheck test coberturaReport'
    }
   stage('SonarQube analysis'){
		withSonarQubeEnv('Core-SonarQube') {
			buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'sonarqube'
		}
    }
	stage ('Push to artifactory'){
	    rtGradle.deployer repo:'libs-snapshot', server: serverArti
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
    stage('Archive artifacts') {
		    archiveArtifacts artifacts: '**/dora*.jar,readme.txt', fingerprint: true
	}
	stage('Deploy app'){
	   git branch: 'master', credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/de-ansible.git'
	   sh 'ansible-playbook -e DORA_API_VERSION=$APP_VERSION -i $inventory deploy-dora.yml --vault-password-file ~/.ssh/vault.txt -vv'
	   cleanWs()
       slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Succes: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
	}
	}
    cleanWs()

}
