node {
    
    stage 'Checkout DSA' 
    checkout scm

  stage('SonarQube analysis') {
    withSonarQubeEnv('Sonar') {
      // requires SonarQube Scanner for Maven 3.2+
      sh 'chmod 755 mvnw'
      sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
    }
  }
  }