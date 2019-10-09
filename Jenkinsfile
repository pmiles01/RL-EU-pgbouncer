  def mysh(cmd) {
    println (cmd)
    sh('#!/bin/sh -e\n' + cmd)
  }

pipeline {
  environment {
       repoName = sh(returnStdout: true, script: "yq -r '.name' helm/Chart.yaml").trim().toLowerCase()
       repoVersion = sh(returnStdout: true, script: "yq -r '.version' helm/Chart.yaml").trim()
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
    stage('Package and Push Helm Chart') {
      steps {
        mysh "docker run --rm -v \$(pwd)/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm init --client-only"
        mysh "docker run --rm -v \$(pwd)/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm package \${repoName}"
        mysh "mkdir -p \$(pwd)/helm_repo && cp \$(pwd)/helm/\${repoName}-\${repoVersion}.tgz \$(pwd)/helm_repo"
        mysh "docker run --rm -v \$(pwd)/helm_repo:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm repo index /apps --url https://rl-helm.storage.googleapis.com"
        mysh "#docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json google/cloud-sdk gcloud auth activate-service-account --project=rl-global-eu --key-file=/key.json"
        mysh "docker run --rm -v ~/.config/gcloud:/root/.config/gcloud -v \$(pwd)/helm_repo:/apps google/cloud-sdk gsutil -m rsync -r /apps/ gs://rl-helm"
      }
    }
    stage('Push Container Image to Repository') {
      steps {
        mysh "#gcloud config set project rl-global-eu"
        mysh "echo y | gcloud auth configure-docker"
        mysh "docker push gcr.io/rl-global-eu/${repoName}"
      }
    }

  }
}
