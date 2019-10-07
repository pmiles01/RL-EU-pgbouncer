pipeline {
    agent any
    stages {
        stage('Vulnerability Scanner') {
            steps {
                aquaMicroscanner imageName: 'alpine:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
            }
        }
    }
}
