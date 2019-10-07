import groovy.json.JsonSlurper


def propertiesFile = 'build.properties'

def repoName(build_properties) {
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
  return new HashMap<>(slurper.parseText(readFile($filename)))
}

@Field String repoName = repoName(parseJsonFile(${PropertiesFile}))


pipeline {
    agent any
        stages {
            stage('Test') {
                steps {
                    println parseJsonFile('build.properties')
                    sh 'docker build . -t ${repoName}'
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
