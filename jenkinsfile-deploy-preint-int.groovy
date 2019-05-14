import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = 'fa29964d-237e-4ecb-96bc-1350dda63f79'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'
@Field
def envProps = [
  'preint': [
    'DORA_URL': 'https://dora.preint.cwds.io/'.toString(),
    'AUTH_MODE': 'dev'.toString(),
    'PERRY_URL': 'https://web.preint.cwds.io'.toString()
  ],
  'integration': [
    'DORA_URL': 'https://doraapi.integration.cwds.io/'.toString(),
    'AUTH_MODE': 'integration'.toString()
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
      buildTestsDockerImageStage()
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

def buildTestsDockerImageStage() {
  stage('Build Tests Docker Image') {
    def serverArti = Artifactory.newServer url: 'http://pr.dev.cwds.io/artifactory'
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.tool = "Gradle_35"
    rtGradle.resolver server: serverArti
    rtGradle.useWrapper = true
    rtGradle.run buildFile: 'build.gradle', tasks: ':dora-api:docker-tests:dockerTestsCreateImage'
  }
}

def testsStage(envName) {
  stage("Smoke tests on $envName") {
    String doraUrl = envProps[envName].DORA_URL.toString()
    String authMode = envProps[envName].AUTH_MODE.toString()
    String dockerEnv = "-e DORA_URL=$doraUrl -e TEST_TYPE=smoke -e AUTH_MODE=$authMode".toString()
    if (envName == 'preint') {
      String perryUrl = envProps[envName].PERRY_URL.toString()
      sh "docker run $dockerEnv -e PERRY_URL=$perryUrl cwds/dora-tests:latest"
    } else {
      withCredentials([
        string(credentialsId: 'c24b6659-fd2c-4d31-8433-835528fce0d7', variable: 'SMOKE_TEST_USER'),
        string(credentialsId: '48619eb9-4a74-4c84-bc25-81557ed9dd7d', variable: 'SMOKE_TEST_PASSWORD'),
        string(credentialsId: 'f75da5fa-b2c8-4ca5-896a-b8a85fa30572', variable: 'SMOKE_VERIFICATION_CODE')
      ]) {
        sh "docker run $dockerEnv \
          -e SMOKE_TEST_USER=$SMOKE_TEST_USER \
          -e SMOKE_TEST_PASSWORD=$SMOKE_TEST_PASSWORD \
          -e SMOKE_VERIFICATION_CODE=$SMOKE_VERIFICATION_CODE \
          cwds/dora-tests"
      }
    }
  }
}
