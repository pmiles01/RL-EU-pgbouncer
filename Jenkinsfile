import groovy.json.JsonSlurper

pipeline {
    agent any

    def rootDir = pwd()
    def code = load "${rootDir}@script/library.Groovy "

  stage('Execute') {
    code.example1()
  }
    stages {
        stage('Test') {
            steps {
                sh 'jq ".name" -r build.properties'
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
