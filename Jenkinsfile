pipeline {
    agent {
      dockerfile {
        filename 'Dockerfile'
        args '--entrypoint=\'\''
      }
    }
    stages {
        stage('Test') {
            steps {
                sh 'echo hello'
            }
        }
        stage('Vulnerability Scanner') {
            steps {
                aquaMicroscanner imageName: 'pgbouncer/pgbouncer', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
            }
        }
    }
}
