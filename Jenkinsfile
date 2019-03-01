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

          stage('Unit Testing') {
           
            steps {
            container('maven') {            
                    
                                     
                    sh 'mvn  -Dtest=com.steerwise.sat.junit.ProductIntTest test -Dmaven.test.failure.ignore=true'
                                    
                    
                
            }
            }
        } 

      stage('Code Quality Analysis') {
           
            steps {
            container('maven') {
                withSonarQubeEnv('SONAR') {
                   sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.profile="Sonar way"  -Dsonar.projectName="demo quality analysis"' 
                    
                }
                sleep(60)
                    waitForQualityGate abortPipeline: true   
            }
            }
        }
stage('Code Vulnerability Analysis') {
           
            steps {
            container('maven') {
                withSonarQubeEnv('SONAR') {
                  
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.profile="FindBugs Security Audit" -Dsonar.projectName="demo-security analysis"  -Dsonar.projectKey=demo-security'  
                    }
                sleep(60)
                    waitForQualityGate abortPipeline: true   
            }
            }
        }




  stage("Build Docker Image") {
    
    steps {

        container('maven') {
        sh 'mvn  dockerfile:build   -D repo=$DOCKER_REGISTRY/tmp -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
    }
    }
  }
  stage("Push Docker Temp Image") {
    
    steps {

        container('maven') {
        sh 'mvn  dockerfile:push -D repo=$DOCKER_REGISTRY/tmp -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
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
  stage("Push Docker Test Image After Vulnerability Analysis") {
    
    steps {

        container('maven') {
        sh 'docker tag $DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER $DOCKER_REGISTRY/test:$DOCKER_REPO-v$BUILD_NUMBER'
        sh 'docker images'
        sh 'mvn  dockerfile:push -D repo=$DOCKER_REGISTRY/test -D tag=$DOCKER_REPO-v$BUILD_NUMBER'
    }
    }
  }

  stage('Cleanup Temp Images') {
      
            steps {
             container('maven') {
                sh'''
                    docker rmi $DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER $DOCKER_REGISTRY/test:$DOCKER_REPO-v$BUILD_NUMBER
                '''
            }
            }
  }
        

  stage ("Deploy in Testing ") {
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
                 error("Build failed because of this and that..")
              }
              
              
            }
          }
       }
  }
  stage('Load Testing') {
           
            steps {
            container('maven') {                
                    
                    sh 'Xvfb :0 >& /dev/null &'
                    sh' mvn jmeter:jmeter -Pjmeter'                    
                    perfReport 'target/jmeter/results/DEMO.jtl'
                    
                
            }
            }
        } 

  stage('FUnctional Testing') {
           
            steps {
            container('maven') {                
                    
                    sh 'Xvfb :0 >& /dev/null &'                    
                    sh 'mvn    -Dtest=com.steerwise.sat.selenium.JMeterSeleniumDemoTest test  -Dmaven.test.failure.ignore=true'
                                      
                    
                
            }
            }
  } 

  // stage ("Deploy in Staging ") {
  //       steps
  //       {
  //       container('maven') 
  //         {
  //           script
  //           {
  //             callback_url = registerWebhook()
  //             echo "Waiting for POST to ${callback_url.getURL()}"

  //             sh "curl -X POST -H 'Content-Type: application/json' -d '{\"callback\":\"${callback_url.getURL()}\",\"image\":\"$DOCKER_REGISTRY/tmp:$DOCKER_REPO-v$BUILD_NUMBER\"}' https://spinnaker.assetdevops.steerwise.io/gate/webhooks/webhook/demo-stg"

  //             data = waitForWebhook callback_url
  //             echo "Webhook called with data: ${data}"
             
              
  //             def props = readJSON text: data
              
  //             if (props['status']=='success')
  //             {
  //                 echo "success from stg"
                  
  //             }
  //             else
  //             {
  //                 echo "failure"
  //             }
              
              
  //           }
  //         }
  //      }
  // }

   
        
     
  }
  post {
        failure {
           
            emailext body: '${DEFAULT_CONTENT}',subject: '${DEFAULT_SUBJECT}', to: '$DEFAULT_RECIPIENTS'
            
            }
            success
            {
                bitbucketStatusNotify(buildState: 'SUCCESSFUL')
                emailext body: '${DEFAULT_CONTENT}',subject: '${DEFAULT_SUBJECT}', to: '$DEFAULT_RECIPIENTS'
                
            }
       
       

            
       }
}