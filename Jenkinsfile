import groovy.json.JsonSlurper


def name(build_properties) {
  return "name"
}
 
def version(build_properties) {
  return "version"
}
 
def git_repository(build_properties) {
  return "git_repository"
}
 
def docker_repository(build_properties) {
  return "docker_repository"
}  
 
node {
  def parseJsonFile(String filename) {
    final slurper = new groovy.json.JsonSlurperClassic()
    return new HashMap<>(slurper.parseText(readFile('build.properties')))
  }
    

        stage('Test') {
            steps {
println(name(slurper))
                sh 'docker build . -t test:latest'
            }
        }
        stage('Vulnerability Scanner') {
            steps {
                aquaMicroscanner imageName: 'test:latest', notCompliesCmd: 'exit 4', onDisallowed: 'fail', outputFormat: 'html'
            }
        }
}
