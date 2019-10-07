pipeline {
    agent any
    stages {

  stage('Load') {
    code = load 'example.groovy'
  }

  stage('Execute') {
    code.example1()
  }
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
