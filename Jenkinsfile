pipeline {
    agent any
    environment {
        SHELL = '/bin/bash'
    }
    stages {
        stage('Clean and compile') {
            steps {
                sh 'echo "Hello, World!"'
                sh 'env'
                echo 'Running Maven clean...'
                sh 'mvn clean'
                echo 'Compiling project...'
                sh 'mvn compile'
                echo 'Packaging project...'
                sh 'mvn package'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('SonarQube') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=123456789'
            }
        }

    }
}

