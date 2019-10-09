def sh(cmd) {
  println (cmd)
  sh('#!/bin/sh -e\n' + cmd)
}

def sh(cmd) {
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
        sh "gcloud auth activate-service-account --project=rl-global-eu --key-file=/var/lib/jenkins/.gcp/key.json"
        sh "docker build . -t ${repoName}:${repoVersion}"
        sh "docker tag ${repoName} gcr.io/rl-global-eu/${repoName}:${repoVersion}"
      }
    }
    stage('Vulnerability Scanner') {
      steps {
        aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
      }
    }
    stage('Package and Push Helm Chart') {
      steps {
        sh "docker run --rm -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm init --client-only"
        sh "docker run --rm -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm package \${repoName}"
        sh "mkdir -p \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_repo && cp \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm/\${repoName}-\${repoVersion}.tgz \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_repo"
        sh "docker run --rm -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_repo:/apps -v ~/.kube:/root/.kube -v ~/.helm:/root/.helm alpine/helm repo index /apps --url https://rl-helm.storage.googleapis.com"

        sh "echo '#!/bin/sh' > \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/sync_repo.sh"
        sh "echo 'gcloud auth activate-service-account --project=rl-global-eu --key-file=/key.json' >> \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/sync_repo.sh"
        sh "echo 'gsutil -m rsync -r /mnt/ gs://rl-helm' >> \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/sync_repo.sh"
        sh "chmod 755 \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/sync_repo.sh"
        sh "docker run --rm -v /var/lib/jenkins/.gcp/key.json:/key.json -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/sync_repo.sh:/sync_repo.sh -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_repo:/mnt google/cloud-sdk /sync_repo.sh"
      }
    }
    stage('Push Container Image to Repository') {
      steps {
        sh "echo y | gcloud auth configure-docker"
        sh "docker push gcr.io/rl-global-eu/${repoName}:${repoVersion}"
      }
    }
//    stage('Deploy to All Dev environments') {
//      steps {
//        sh "mkdir -p \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_config"
//        sh "docker -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/kubeconfig/dev02:/root/.kube/config -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_config:/root/.helm dtzar/helm-kubectl helm init --upgrade --stable-repo-url https://rl-helm.storage.googleapis.com"
//        sh "docker -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/kubeconfig/dev02:/root/.kube/config -v \${JENKINS_HOME}/workspace/${ITEM_FULL_NAME}/helm_config:/root/.helm dtzar/helm-kubectl helm install stable/rl-eu-pgbouncer"
//      }
//    }
  }
}
