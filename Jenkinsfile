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
        mysh "docker run --rm -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm init --client-only"
        mysh "docker run --rm -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm package \${repoName}"
        mysh "mkdir -p \${WORKSPACE}/helm_repo && cp \${WORKSPACE}/helm/\${repoName}-\${repoVersion}.tgz \${WORKSPACE}/helm_repo"
        mysh "docker run --rm -v \${WORKSPACE}/helm_repo:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm repo index /apps --url https://rl-helm.storage.googleapis.com"
        mysh "#docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json google/cloud-sdk gcloud auth activate-service-account --project=rl-global-eu --key-file=/key.json"
        mysh "echo '/bin/sh' > \${WORKSPACE}/sync_repo.sh"
        mysh "echo 'gcloud auth activate-service-account --project=rl-global-eu --key-file=/key.json' >> \${WORKSPACE}/sync_repo.sh"
        mysh "echo 'gsutil -m rsync -r /mnt/ gs://rl-helm' >> \${WORKSPACE}/sync_repo.sh"
        mysh "chmod 755 \${WORKSPACE}/sync_repo.sh
        mysh "docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json -v \${WORKSPACE}/sync_repo.sh:/sync_repo.sh -v \${WORKSPACE}/helm_repo:/mnt google/cloud-sdk /sync_repo.sh
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
