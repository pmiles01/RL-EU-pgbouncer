import groovy.json.JsonSlurper

def propertiesFile = 'build.properties'


def repoName() {
  return sh(returnStdout: true, script: "jq -r '.name' ${propertiesFile}")
}

def repoVersion() {
  return sh(returnStdout: true, script: "jq -r '.version' ${propertiesFile}")
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
                    sh "docker build . -t repoName():repoVersion()"
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
