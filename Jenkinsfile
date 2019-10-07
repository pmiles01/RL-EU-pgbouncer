pipeline {
    agent any
        stages {
            stage('Test') {
                steps {
                    repoName = sh(returnStdout: true, script: "jq -r '.name' build.properties")
                    repoVersion = sh(returnStdout: true, script: "jq -r '.version' build.properties")
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
