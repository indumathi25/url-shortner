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