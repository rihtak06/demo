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
          sh 'ls -l'
          sh 'ls -l target'
        docker.withRegistry('', 'docker-registry') {
        def dockerFileLocation = '.'
        def demo = docker.build("manickamsw/demo:latest",dockerFileLocation)
        demo.push()
    }
      }
    }
    stage('Deploy Demo App') {
      container('kubectl') {
        sh "kubectl run --image=manickamsw/demo demo-app --port=8085"
        sh "kubectl expose deployment demo-app --port=8085 --name=demo-app-http"
      }
    }

  }
}