pipeline {
    agent any
    stages {
        stage('Clean and compile') {
            steps {
                sh "env"
                echo 'Running Maven clean...'
                sh "mvn clean"
                echo 'Compiling project...'
                sh "mvn compile"
                echo 'Packaging project...'
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

