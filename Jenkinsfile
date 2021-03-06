def verboseSH(cmd) {
  println (cmd)
  sh('#!/bin/sh -e\n' + cmd)
  return
}

def silentSH(cmd) {
  println (cmd)
  sh('#!/bin/sh -e\n' + cmd)
  return
}

def deployHelmChart(environment) {
//  silentSH "docker -v \${WORKSPACE}/kubeconfig/" + environment + ":/root/.kube/config -v \${WORKSPACE}/helm_config:/root/.helm dtzar/helm-kubectl \"helm gcs init --upgrade --stable-repo-url https://rl-helm.storage.googleapis.com\""
  silentSH "docker -v \${WORKSPACE}/kubeconfig/" + environment + ":/root/.kube/config -v \${WORKSPACE}/helm_config:/root/.helm dtzar/helm-kubectl helm install stable/rl-eu-pgbouncer"
}

def packageHelmChart() {
  repoName = sh(returnStdout: true, script: "yq -r '.name' helm/rl-eu-pgbouncer/Chart.yaml").trim().toLowerCase()
  repoVersion = sh(returnStdout: true, script: "yq -r '.version' helm/rl-eu-pgbouncer/Chart.yaml").trim()

  // Instance the native GCS plugin for helm
  silentSH "docker run --rm -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm dtzar/helm-kubectl helm plugin install https://github.com/hayorov/helm-gcs || true"

  silentSH "docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm -e GOOGLE_APPLICATION_CREDENTIALS=/key.json dtzar/helm-kubectl helm gcs init gs://rl-helm"
  silentSH "docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm -e GOOGLE_APPLICATION_CREDENTIALS=/key.json dtzar/helm-kubectl helm repo add stable gs://rl-helm"

  silentSH "mkdir -p \${WORKSPACE}/helm_repo && cp \${WORKSPACE}/helm/\${repoName}-\${repoVersion}.tgz \${WORKSPACE}/helm_repo"



  silentSH "docker run --rm -v \${WORKSPACE}/helm_repo:/helm_repo -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm dtzar/helm-kubectl helm package --destination /helm_repo /apps/\${repoName}"
  silentSH "docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json -v \${WORKSPACE}/helm_repo:/helm_repo -v \${WORKSPACE}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm -e GOOGLE_APPLICATION_CREDENTIALS=/key.json dtzar/helm-kubectl helm gcs push /helm_repo/" + repoName + "-" + repoVersion + ".tgz stable --force"

}

def buildContainer() {
  repoName = sh(returnStdout: true, script: "yq -r '.name' helm/rl-eu-pgbouncer/Chart.yaml").trim().toLowerCase()
  repoVersion = sh(returnStdout: true, script: "yq -r '.version' helm/rl-eu-pgbouncer/Chart.yaml").trim()

  silentSH "gcloud auth activate-service-account --project=rl-global-eu --key-file=/var/lib/jenkins/.gcp/key.json"
  silentSH "docker build . -t "+ repoName + ":"+ repoVersion
  silentSH "docker tag " + repoName + " gcr.io/rl-global-eu/"+ repoName + ":" + repoVersion
}

def pushContainer() {
  repoName = sh(returnStdout: true, script: "yq -r '.name' helm/rl-eu-pgbouncer/Chart.yaml").trim().toLowerCase()
  repoVersion = sh(returnStdout: true, script: "yq -r '.version' helm/rl-eu-pgbouncer/Chart.yaml").trim()

  silentSH "echo y | gcloud auth configure-docker"
  silentSH "docker push gcr.io/rl-global-eu/"+ repoName + ":" + repoVersion
}

pipeline {
  environment {
    repoName = sh(returnStdout: true, script: "yq -r '.name' helm/rl-eu-pgbouncer/Chart.yaml").trim().toLowerCase()
    repoVersion = sh(returnStdout: true, script: "yq -r '.version' helm/rl-eu-pgbouncer/Chart.yaml").trim()
  }

  agent any
  stages {
    stage('Docker Build') {
      steps {
        buildContainer()
      }
    }
    stage('Vulnerability Scanner') {
      steps {
        aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
      }
    }
    stage('Package and Push Helm Chart') {
      steps {
        packageHelmChart()
      }
    }
    stage('Push Container Image to Repository') {
      steps {
        pushContainer()
      }
    }
    stage('Deploy to All Dev environments') {
      steps {
        deployHelmChart('dev02')
      }
    }
  }
}
