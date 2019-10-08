  def mysh(cmd) {
    println (cmd)
    sh('#!/bin/sh -e\n' + cmd)
  }

pipeline {
  environment {
       repoName = sh(returnStdout: true, script: "jq -r '.name' build.properties").trim().toLowerCase()
       repoVersion = sh(returnStdout: true, script: "jq -r '.version' build.properties").trim()
   }

  agent any
  stages {

    stage('Docker Build') {
      steps {
        mysh "gcloud auth activate-service-account --project=rl-global-eu --key-file=/var/lib/jenkins/.gcp/key.json"
        mysh "docker build . -t ${repoName}:${repoVersion}"
        mysh "docker tag ${repoName} gcr.io/rl-global-eu/${repoName}:${repoVersion}"
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
    stage('Push to Repository') {
      steps {
        mysh "#gcloud config set project rl-global-eu"
        mysh "echo y | gcloud auth configure-docker"
        mysh "docker push gcr.io/rl-global-eu/${repoName}"

      }
    }

  }
}
