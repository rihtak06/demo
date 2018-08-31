pipeline {
  agent any
  tools {
    maven 'M3'
  }
  stages {
   stage 'Checkout DSA' 
    checkout scm

  stage('SonarQube analysis') {
    withSonarQubeEnv('Sonar') {
      // requires SonarQube Scanner for Maven 3.2+
      sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
    }
  }
  }
}
