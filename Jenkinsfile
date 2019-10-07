import groovy.json.JsonSlurper


def propertiesFile = 'build.properties'

def repoName(build_properties) {
  sh "jq -r '.name' ${propertiesFile}"
  return
}

def version(build_properties) {
  sh "jq -r '.version' ${propertiesFile}"
  return
}

def git_repository(build_properties) {
  return "git_repository"
}

def docker_repository(build_properties) {
  return "docker_repository"
}

@NonCPS
def parseJsonFile() {
  final slurper = new groovy.json.JsonSlurperClassic()
  return slurper.parseText(readFile('build.properties'))
}


pipeline {
    agent any
        stages {
            stage('Test') {
                steps {
                    println parseJsonFile()
                    sh 'docker build . -t ${repoName}:${version}'
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
