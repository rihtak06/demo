node {
    
    stage 'Checkout DSA' 
    checkout scm

    stage('SonarQube analysis') {
    withSonarQubeEnv('My SonarQube Server') {
      sh 'sonar-scanner \
  -Dsonar.projectKey=code-quality-demo \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://52.90.103.188:31957/sonar \
  -Dsonar.login=f43cacba04afa6a34bad4b9247a7f144b5238c63'
    }
  }