pipeline {
  agent any
  tools {
    maven 'M3'
  }
  stages {
   stage ('Checkout DSA') 
    {
    steps {
    	checkout scm
	}
	}
  stage('SonarQube analysis') {
  steps {
    withSonarQubeEnv('Sonar') {
        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.projectKey=code-quality-demo    -Dsonar.sources=.    -Dsonar.host.url=http://52.90.103.188:31000/sonar -Dsonar.login=f43cacba04afa6a34bad4b9247a7f144b5238c63'
    }
    
    }
  }
  }
}
