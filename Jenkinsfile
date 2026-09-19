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
				
			stage('Check Docker'){
				steps{
					bat 'docker version'
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