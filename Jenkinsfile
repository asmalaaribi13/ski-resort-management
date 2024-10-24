pipeline {
    agent any
    stages {
        stage('Clean and compile') {
            steps {
                sh "mvn clean"
                sh "mvn compile"
                sh "mvn package"
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('SonarQube') {
            steps {
                sh "mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=123456789"
            }
        }

    }
}

