pipeline{
	agent any 
	stages {
		stage('Checkout') {
			steps {
				checkout scm
				}
			}
			
			stage('Build and Test'){
				steps{
					bat 'mvnw.cmd clean test package'
					}
				}
				
			stage('Docker Build'){
				steps{
					bat 'docker build -t employee-api:%BUILD_NUMBER% .'
				}
			}
			
			stage('Docker Image check'){
				steps{
					bat 'docker images employee-api'
					}
				}
			
				
				stage('Verify JAR'){
					steps {
						bat 'dir target'
						}
					}
				}
				
				post {
					success {
					echo 'CI pipeline completed successfully.'
					}
					
					failure {
					echo 'CI pipeline failed.'
					}
				}
			}