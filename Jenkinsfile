def label = "worker-${UUID.randomUUID().toString()}"

podTemplate(label: label, containers: [
        containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),

  containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
  // containerTemplate(name: 'helm', image: 'lachlanevenson/k8s-helm:latest', command: 'cat', ttyEnabled: true)
],
volumes: [
  hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
]) {
  node(label) {
   
 
    stage('Create Docker images') {
    checkout scm 
     container('maven') {
        sh 'mvn clean install -DskipTests=true'
      }
      container('docker') {
        docker.withRegistry('', 'docker-registry') {
        def dockerFileLocation = '.'
        def demo = docker.build("manickamsw/demo:latest",dockerFileLocation)
        demo.push()
    }
      }
    }
    stage('Deploy Demo App') {
      container('kubectl') {
        sh "kubectl delete -f ./k8s/"
        sh "kubectl apply -f ./k8s/"
       
      }
    }

//  stage('SonarQube analysis') {
//  steps {
//    withSonarQubeEnv('SONAR') {
//      sh 'mvn clean install -DskipTests=true'
//      sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar' 
//      sh 'cp ./target/*.jar ./test.jar'
//    }
//    
//    }
//  }

  }
}