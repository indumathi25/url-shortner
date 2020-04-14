pipeline {
  agent any
    tools {
      maven 'maven3'
                 jdk 'JDK8'
    }
    stages {
        stage('Build maven ') {

            steps {
                    sh 'pwd'
                    sh 'ls'
                    sh 'whoami'
                    sh 'export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.242.b08-0.50.amzn1.x86_64/jre'
                    sh 'export PATH=$PATH:$JAVA_HOME'
                    sh 'mvn clean install'
            }
        }

        stage('Copy Artifact') {
           steps {
                   sh 'pwd'
		   sh 'cp -r target/*.jar docker'
           }
        }

        stage('Build docker image') {
           steps {
               script {
                 def customImage = docker.build('9626320431/urlshortner', "./docker")
                 docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                 customImage.push("v1.0." + "${env.BUILD_NUMBER}")
                 }
           }
        }
	  }
    }
}