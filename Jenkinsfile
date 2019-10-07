import groovy.json.JsonSlurper


def propertiesFile = 'build.properties'

def version(build_properties) {
  sh "jq -r '.version' ${propertiesFile}"
  return "version"
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
                    sh 'docker build . -t repoName'
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
