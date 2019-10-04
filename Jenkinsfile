pipeline {
    agent {
      dockerfile true,
      dockerfile {
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
