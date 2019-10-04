pipeline {
    dockerfile {
      agent { 
        args '--entrypoint=\'\''
      }
    }
    stages {
        stage('Test') {
            steps {
                sh 'echo hello'
            }
        }
    }
}
