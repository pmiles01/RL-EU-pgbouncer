pipeline {
  environment {
       repoName = sh(returnStdout: true, script: "jq -r '.name' build.properties").trim().toLowerCase()
       repoVersion = sh(returnStdout: true, script: "jq -r '.version' build.properties").trim()
   }

  agent any
  stages {

    stage('Test') {
       steps {
  def mysh(cmd) {
    println (cmd)
    sh('#!/bin/sh -e\n' + cmd)
  }

                    mysh "docker build . -t ${repoName}:${repoVersion}"
                }
            }
            stage('Vulnerability Scanner') {
                steps {
                    aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
                }
            }
            stage('Package Helm Chart') {
                steps {
                    mysh "docker run --rm --entrypoint \"/bin/sh\" -v \$(pwd)/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm -c \"helm init --client-only && helm package ${repoName}\""
                }
            }
        }
}
