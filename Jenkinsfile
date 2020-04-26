// Scripted Pipeline
// Requires libraries from https://github.com/Prouser123/jenkins-tools
// Made by @Prouser123 for https://ci.jcx.ovh.

node('docker-cli') {
  cleanWs()
  docker.image('jcxldn/jenkins-containers:jdk11-gradle-ubuntu').inside {

    stage('Setup') {

      checkout scm

      sh 'chmod +x ./gradlew'
    }

    stage('Build') {
      // Setup the build environment and build the code
      sh 'gradle wrapper && ./gradlew build -s'
        
      archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci")
    }
  }
}
