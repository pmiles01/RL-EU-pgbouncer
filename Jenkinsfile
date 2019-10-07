pipeline {
    agent any
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
