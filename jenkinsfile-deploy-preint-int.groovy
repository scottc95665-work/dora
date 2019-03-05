import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = '433ac100-b3c2-4519-b4d6-20node7c029a103b'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'
@Field
def envProps = [
  'preint': [
    'DORA_URL': 'https://dora.preint.cwds.io',
    'PERRY_URL': 'https://web.preint.cwds.io'
  ],
  'integration': [
    'DORA_URL': 'https://doraapi.integration.cwds.io',
    'PERRY_URL': 'https://web.integration.cwds.io'
  ]
]

deploy('preint')
deploy('integration')

def deploy(environment) {
  node(environment) {
    try {
      checkoutStage(environment)
      deployStage(environment, env.version)
      updateManifestStage(environment, env.version)
    } catch(Exception e) {
      currentBuild.result = 'FAILURE'
      throw e
    } finally {
      cleanWs()
    }
  }
}

def checkoutStage(environment) {
  stage("Checkout for $environment") {
    deleteDir()
    checkout scm
  }
}

def deployStage(environment, version) {
  stage("Deploy to $environment") {
    ws {
      environmentDashboard(addColumns: false, buildJob: '', buildNumber: version, componentName: 'Dora', data: [], nameOfEnv: 'PREINT', packageName: 'Dora') {
        git branch: 'master', credentialsId: githubCredentialsId, url: deAnsibleGithubUrl
        sh "ansible-playbook -e NEW_RELIC_AGENT=$env.USE_NEWRELIC -e DORA_API_VERSION=$version -i inventories/$environment/hosts.yml deploy-dora.yml --vault-password-file ~/.ssh/vault.txt"
      }
    }
  }
}

def updateManifestStage(environment, version) {
  stage("Update Manifest for $environment") {
    updateManifest('dora', environment, githubCredentialsId, version)
  }
}
