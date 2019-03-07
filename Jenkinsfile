pipeline 
{
  agent 
  {
    kubernetes 
    {
      label 'jenkins-slave'
      defaultContainer 'jnlp'
      yaml """
          apiVersion: v1
          kind: Pod
          metadata:
            labels:
              some-label: Jenkins-slave
          spec:
            containers:
            - name: slave-pod
              image: $DOCKER_REGISTRY/jenkins-slave:devops
              imagePullPolicy: Always
              command:
              - cat
              tty: true
              volumeMounts:
              - mountPath: /var/run/docker.sock
                name: docker-socket-volume
              securityContext:
                privileged: true
            volumes:
                - name: docker-socket-volume
                  hostPath:
                    path: /var/run/docker.sock
                    type: File
            
      """
    }
  }

  environment 
  {
       
        DOCKER_REPO    = 'demo'
  }

  stages 
  {
    

    stage('Build') 
    {
        when{  expression { return params.BUILD==true; } }
        steps 
        {
          container('slave-pod') 
          {
                    withCredentials([[
                          $class: 'AmazonWebServicesCredentialsBinding',
                          credentialsId: 'ECR',
                          accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                          secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                             ]]) {                    
                                       sh'aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID'
                                      sh'aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY'
                                      sh'aws configure set region us-east-2'
                                      sh '/tmp/refresh.sh'
                                  }                
                    sh 'mvn clean install -DskipTests'           
            }
        }
    }

    stage('Unit Testing') 
    {
      when {  expression { return params.UNITTEST==true; } }
      steps 
      {
            container('slave-pod') 
            {               
                                     
                    sh 'mvn  -Dtest=com.steerwise.sat.junit.ProductIntTest test -Dmaven.test.failure.ignore=true'                  
                
            }
      }
    } 

    stage('Code Quality Analysis') 
    {
      
      when{  expression { return params.CODEQUALITY==true && params.BUILD==true ; } }
      steps 
      {
        container('slave-pod') 
          {
                withSonarQubeEnv('SONAR') {
                   sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.profile="Sonar way"  -Dsonar.projectName="demo quality analysis"' 
                    
                }
                sleep(60)
                    waitForQualityGate abortPipeline: true   
          }
      }
    }

    stage('Code Vulnerability Analysis') 
    {
      when{  expression { return params.CODEVULNERABILITY==true  && params.BUILD==true; } }
      steps 
      {
            container('slave-pod') {
                withSonarQubeEnv('SONAR') {
                  
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.profile="FindBugs Security Audit" -Dsonar.projectName="demo-security analysis"  -Dsonar.projectKey=demo-security'  
                    }
                sleep(60)
                    waitForQualityGate abortPipeline: true   
            }
      }
    }

    stage("Build Docker Image") 
    {
      when{  expression { return params.DOCKERIMAGE==true; } }
      steps 
      {
            container('slave-pod') {
              sh 'mvn  dockerfile:build   -D repo=$DOCKER_REGISTRY/tmp -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
            }
      }
    }

    stage("Push Docker Temp Image") 
    {
      when{  expression { return params.PUSHIMAGETOSCAN==true && params.DOCKERIMAGE==true ; } }    
      steps 
      {

        container('slave-pod') {
          sh 'mvn  dockerfile:push -D repo=$DOCKER_REGISTRY/tmp -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
        }
      }
    }

    stage('Image Vulnerability Analysis') 
    {
      when{  expression { return params.IMAGESCAN==true && params.PUSHIMAGETOSCAN==true && params.DOCKERIMAGE==true; } }
      steps 
      {
          container('slave-pod') {
                sh 'echo "$DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER ${WORKSPACE}/Dockerfile " > anchore_images'
                anchore name: 'anchore_images',bailOnFail: false, bailOnPluginFail: false
                
          }
      }
    }

    stage("Push Docker Test Image After Vulnerability Analysis")
    {
      when { expression { return params.PUSHIMAGETOTEST==true && params.IMAGESCAN==true && params.PUSHIMAGETOSCAN==true && params.DOCKERIMAGE==true; } }    
      steps 
      {

        container('slave-pod') {
            sh 'docker tag $DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER $DOCKER_REGISTRY/test:$DOCKER_REPO-v$BUILD_NUMBER'
            sh 'mvn  dockerfile:push -D repo=$DOCKER_REGISTRY/test -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
            sh 'docker rmi $DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER $DOCKER_REGISTRY/test:$DOCKER_REPO-v$BUILD_NUMBER'
            sh 'aws ecr batch-delete-image --repository-name tmp --image-ids imageTag=$DOCKER_REPO-v$BUILD_NUMBER'
        }
      }
    }    
  }

  post 
  {
        failure 
        {
           
            emailext body: '${DEFAULT_CONTENT}',subject: '${DEFAULT_SUBJECT}', to: '$DEFAULT_RECIPIENTS'
            
        }
        success
        {
                //bitbucketStatusNotify(buildState: 'SUCCESSFUL')
                emailext body: '${DEFAULT_CONTENT}',subject: '${DEFAULT_SUBJECT}', to: '$DEFAULT_RECIPIENTS'
                
        } 
  }

}