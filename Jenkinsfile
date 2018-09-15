

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
              sleep(5)
              waitForQualityGate abortPipeline: true
            }
          }



 stage('Create Docker images') {
 steps {

  script {
       docker.withRegistry('', 'docker-registry') {
        def dockerFileLocation = '.'
        def demo = docker.build("manickamsw/demo:latest",dockerFileLocation)
        demo.push()
    }
    
  }
}

}
//    stage('Deploy Demo App') {
//        agent {
//      kubernetes {
//      label 'kubectl'
//      containerTemplate {
//        name 'kubectl'
//        image 'lachlanevenson/k8s-kubectl:v1.8.8'
//        ttyEnabled true
//        command 'cat'
//      }
//    }
//  }
//      steps {
//        container('kubectl') {
//            sh "kubectl apply -f ./k8s/"
//          }
//      }
     
//    }

  }
              }
 