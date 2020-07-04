// Scripted Pipeline
// Requires libraries from https://github.com/Prouser123/jenkins-tools
// Made by @Prouser123 for https://ci.jcx.ovh.

node('docker-cli') {
  postJobGhStatus() {
    cleanWs()

    docker.image('jcxldn/jenkins-containers:jdk11-gradle-ubuntu').inside {

      stage('Setup') {
        checkout scm

        sh 'chmod +x ./gradlew'
      }

      stage('Build') {
        // 'gradle wrapper' is not required here - it is only needed to update / generate a NEW wrapper, not use an existing one.
        sh './gradlew test build -s'
        
        archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
				
        ghSetStatus("The build passed.", "success", "ci")
      }
    }
  }
}
