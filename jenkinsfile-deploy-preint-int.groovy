import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = '433ac100-b3c2-4519-b4d6-207c029a103b'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'
@Field
def envProps = [
  'preint': [
    'DORA_URL': 'https://dora.preint.cwds.io/'.toString(),
    'PERRY_URL': 'https://web.preint.cwds.io'.toString()
  ],
  'integration': [
    'DORA_URL': 'https://doraapi.integration.cwds.io/'.toString(),
    'PERRY_URL': 'https://web.integration.cwds.io'.toString()
  ]
]

deploy('preint')
deploy('integration')

def deploy(envName) {
  node(envName) {
    try {
      checkoutStage(envName)
      deployStage(envName, env.version)
      updateManifestStage(envName, env.version)
      testsStage(envName)
    } catch(Exception e) {
      currentBuild.result = 'FAILURE'
      throw e
    } finally {
      publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'dora-api/build/reports/tests/smokeTest', reportFiles: 'index.html', reportName: "Smoke Tests Report for $envName", reportTitles: ''])
      cleanWs()
    }
  }
}

def checkoutStage(envName) {
  stage("Checkout for $envName") {
    deleteDir()
    checkout scm
  }
}

def deployStage(envName, version) {
  stage("Deploy to $envName") {
    ws {
      environmentDashboard(addColumns: false, buildJob: '', buildNumber: version, componentName: 'Dora', data: [], nameOfEnv: 'PREINT', packageName: 'Dora') {
        git branch: 'master', credentialsId: githubCredentialsId, url: deAnsibleGithubUrl
        sh "ansible-playbook -e NEW_RELIC_AGENT=$env.USE_NEWRELIC -e DORA_API_VERSION=$version -i inventories/$envName/hosts.yml deploy-dora.yml --vault-password-file ~/.ssh/vault.txt"
      }
    }
  }
}

def updateManifestStage(envName, version) {
  stage("Update Manifest for $envName") {
    updateManifest('dora', envName, githubCredentialsId, version)
  }
}

def testsStage(envName) {
  stage("Smoke tests on $envName"){
    def serverArti = Artifactory.newServer url: 'http://pr.dev.cwds.io/artifactory'
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "Gradle_35"
    rtGradle.resolver server: serverArti
    rtGradle.useWrapper = true
    String doraUrl = envProps[envName].DORA_URL.toString()
    rtGradle.run buildFile: './dora-api/build.gradle', tasks: "clean smokeTest --stacktrace -Pdora.url=$doraUrl"
  }
}
