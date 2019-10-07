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
    

pipeline {
    agent {
        stages {
            stage('Test') {
                steps {
                    sh 'docker build . -t test:latest'
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
    }
}
