import groovy.json.JsonSlurper


def name(build_properties) {
  return "name"
}

def version(build_properties) {
  return "version"
}

def git_repository(build_properties) {
  return "git_repository"
}

def docker_repository(build_properties) {
  return "docker_repository"
}

@NonCPS
def parseJsonFile(String filename) {
  final slurper = new groovy.json.JsonSlurperClassic()
  return new HashMap<>(slurper.parseText(readFile('build.properties')))
}

pipeline {
    agent any
        stages {
            stage('Test') {
                steps {
                    def buildProperty = parseJsonFile('build.properties')
                    sh 'docker build . -t ${buildProperty{"name"}:${buildProperty{"version"}'
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
