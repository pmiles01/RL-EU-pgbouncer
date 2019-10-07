import groovy.json.JsonSlurper

def repoName() {
  return sh(returnStdout: true, script: "jq -r '.name' build.properties")
}

def repoVersion() {
  return sh(returnStdout: true, script: "jq -r '.version' $build.properties")
}

def git_repository(build_properties) {
  return "git_repository"
}

def docker_repository(build_properties) {
  return "docker_repository"
}
                    def imageName = repoName()+":"+repoVersion()

pipeline {
    agent any
        stages {
            stage('Test') {
                steps {
                    sh "docker build . -t ${imageName}"
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
