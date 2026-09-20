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
				
			stage('SonarQube Analysis'){
				steps{
					withSonarQubeEnv('sonarqube'){
						bat 'mvnw.cmd verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.8.0.7211:sonar -Dsonar.projectKey=employee-api'
						}
					}
				}
			
			stage('Quality Gate'){
				steps{
					timeout(time:5, unit: 'MINUTES'){
						waitForQualityGate abortPipeline: true
						}
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