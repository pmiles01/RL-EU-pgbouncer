pipeline {
   environment {
       repoName = sh(returnStdout: true, script: "jq -r '.name' build.properties")
       repoVersion = sh(returnStdout: true, script: "jq -r '.version' build.properties")
   }

    agent any
        stages {
            stage('Test') {
                steps {
                    sh "docker build . -t ${repoName}"
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
        }
}
