pipeline {
    dockerfile true,
    agent { 
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
