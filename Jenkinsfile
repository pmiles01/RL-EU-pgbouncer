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
 
    def slurper = new groovy.json.JsonSlurper().parseText(readFile('build.properties'))
    
pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
println(name(slurper))
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
