

pipeline {
  agent none
  
  stages {
   stage ('Checkout DSA') 
    {
        agent any
    steps {
      checkout scm
  }
  }
  stage('SonarQube analysis') {
    agent any 
    tools {
    maven 'M3'
  }
  steps {
    withSonarQubeEnv('SONAR') {
      sh 'mvn clean install -DskipTests=true'
      sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar' 
      sh 'cp ./target/*.jar ./test.jar'
         
    }
 
    
    
    }
  }
  
    stage("Quality Gate") {
            steps {
              sleep(30)
              waitForQualityGate abortPipeline: true
            }
          }



 stage('Create Docker images') {
    podTemplate(label: label, containers: [
  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
  hostPathVolume(mountPath: './', hostPath: './')

]) {
  node(label) { 
      container('docker') {
         sh """
            
            docker build -t namespace/my-image:${gitCommit} .
            """
      }
    
}
}
}
    stage('Deploy Demo App') {
        agent {
      kubernetes {
      label 'kubectl'
      containerTemplate {
        name 'kubectl'
        image 'lachlanevenson/k8s-kubectl:v1.8.8'
        ttyEnabled true
        command 'cat'
      }
    }
  }
      steps {
        container('kubectl') {
            sh "kubectl apply -f ./k8s/"
          }
      }
     
    }

  }
              }
 