pipeline {
    agent any
  //triggers {pollSCM('* * * * *')}
    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git branch: "main", url: 'https://github.com/mtai0/PlanetsAndMoons.git'
            }
        }
        stage('Build') {
            steps {
                // sh 'chmod a+x mvn'
                sh 'mvn clean package'
                sh 'mvn test'
            }

            post {
                always {
                    archiveArtifacts 'target/*.jar'
                }
            }
        }
    }
}