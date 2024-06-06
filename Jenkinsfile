pipeline {
      agent any
    tools {
        // Specify the Maven installation named "TestMaven"
        maven 'TestMaven'
    }
    stages {
        stage('Build') {
            steps {
                // Checkout the code from the repository
                checkout scm

                // Build the project
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                // Run the tests
                sh 'mvn test'
            }
        }
    }
} //working?