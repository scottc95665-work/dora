node ('dora-slave'){
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '3')), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], pipelineTriggers([pollSCM('H/5 * * * *')])])

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
	        buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dockerCreateImage'
	        withDockerRegistry([credentialsId: '6ba8d05c-ca13-4818-8329-15d41a089ec0']) {
                    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dockerPublish'
            }
    }
	}
	stage('Clean WorkSpace') {
		    buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'dockerRemoveContainer'
		    archiveArtifacts artifacts: '**/cals-data-model*.jar,readme.txt', fingerprint: true
		    cleanWs()
	}
	}
//	    emailext attachLog: true, body: 'For detail see log', recipientProviders: [[$class: 'DevelopersRecipientProvider']], subject: 'Cals-api unstable', to: 'Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov'
	    cleanWs()
}
