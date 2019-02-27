pipeline {
  agent {
    kubernetes {
      label 'jenkins-slave'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: maven
    image: 931604932544.dkr.ecr.us-east-2.amazonaws.com/jenkins-slave:devops
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
  environment {
        DOCKER_REGISTRY= '931604932544.dkr.ecr.us-east-2.amazonaws.com'
        DOCKER_REPO    = 'demo'

    }
  stages {
    

    stage('Build') {
           
            steps {
            container('maven') {
            
            
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

        stage('SonarQube analysis') {
           
            steps {
            container('maven') {
                withSonarQubeEnv('SONAR') {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'  
                              
                    
                    
                }
                sleep(60)
                    waitForQualityGate abortPipeline: true   
            }
            }
        }

  stage("Build & Push Docker tmp Image for Sanitary Test") {
    
    steps {

        container('maven') {
        sh 'mvn  dockerfile:build   dockerfile:push -D repo=$DOCKER_REGISTRY/tmp -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
    }
    }
  }

  stage('Image Vulnerability Analysis') {
     
            steps {
             container('maven') {
                sh 'echo "$DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER ${WORKSPACE}/Dockerfile " > anchore_images'
                anchore name: 'anchore_images',bailOnFail: false, bailOnPluginFail: false
                
            }
            }
        }
        

  stage ("Spinnaker") {
        steps
        {
        container('maven') 
          {
            script
            {
              callback_url = registerWebhook()
              echo "Waiting for POST to ${callback_url.getURL()}"

              sh "curl -X POST -H 'Content-Type: application/json' -d '{\"callback\":\"${callback_url.getURL()}\",\"image\":\"$DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER\"}' https://spinnaker.assetdevops.steerwise.io/gate/webhooks/webhook/demo"

              data = waitForWebhook callback_url
              echo "Webhook called with data: ${data}"
             
              
              def props = readJSON text: data
              
              if (props['status']=='success')
              {
                  echo "success"
                  
              }
              else
              {
                  echo "failure"
              }
              
              
            }
          }
       }
  }

   stage('Cleanup Temp Images') {
      
            steps {
             container('maven') {
                sh'''
                    for i in `cat anchore_images | awk '{print $1}'`;do docker rmi $i; done
                '''
            }
            }
        }
        
     
  }
  post {
        failure {
           
            emailext body: '${DEFAULT_CONTENT}',subject: '${DEFAULT_SUBJECT}', to: '$DEFAULT_RECIPIENTS'
            
            }
            success
            {
                 bitbucketStatusNotify(buildState: 'SUCCESS')
                
            }
       
       

            
       }
}