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
    withSonarQubeEnv('SONAR') {
    	sh 'mvn clean install -DskipTests=true'
      sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar' 
      sh 'cp ./target/*.war ./test.war'
    }
    
    }
  }
  stage "Build Docker Image and push to Registry"  
    docker.withRegistry('https://hub.docker.com/', 'docker-registry') {
        def dockerFileLocation = '.'
        def dsa = docker.build("manickamsw/demo",dockerFileLocation)
        dsa.push()
    }

  stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    // Requires SonarQube Scanner for Jenkins 2.7+
                    waitForQualityGate abortPipeline: true
                }
            }
        }
  }
}
