pipeline {
   environment {
       repoName = sh(returnStdout: true, script: "jq -r '.name' build.properties").trim().toLowerCase()
       repoVersion = sh(returnStdout: true, script: "jq -r '.version' build.properties").trim()
   }

    agent any
        stages {
            stage('Test') {
                steps {
                    sh "docker build . -t ${repoName}:${repoVersion}"
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
            stage('Package Helm Chart') {
                steps {
                    sh "docker run -ti --rm -v \$(pwd):/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm package ${repoName}"
                }
            }
        }
}
