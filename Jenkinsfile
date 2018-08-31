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
  	    sh 'mvn clean install -DskipTests=true'
  }
  steps {
    withSonarQubeEnv('Sonar') {
        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar' 
    }
    
    }
  }
  }
}
