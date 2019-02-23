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
            }
            }
        }


     stage("Code Quality Gate") {
            steps {
              container('maven') {
                sleep(60)
                waitForQualityGate abortPipeline: true
                }
            }
        }

    stage("Build Docker Image") {
    
    steps {
        container('maven') {
        sh 'mvn -f ./pom.xml dockerfile:build dockerfile:push -D repo=931604932544.dkr.ecr.us-east-2.amazonaws.com/demo -D tag=v$BUILD_NUMBER'
    }
    }
  }

  stage('Image Vulnerability Analysis') {
     
            steps {
             container('maven') {
                sh 'echo "931604932544.dkr.ecr.us-east-2.amazonaws.com/demo:v$BUILD_NUMBER ${WORKSPACE}/Dockerfile " > anchore_images'
                anchore name: 'anchore_images',bailOnFail: false, bailOnPluginFail: false
                
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
}