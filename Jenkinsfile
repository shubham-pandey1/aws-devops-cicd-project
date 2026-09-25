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
			
			stage('Trivy check'){
				steps{
					bat '"C:\\trivy_0.74.0_windows-64bit\\trivy.exe" --version'
					}
				}
			
			stage('Trivy Scan'){
				steps{
					bat '"C:\\trivy_0.74.0_windows-64bit\\trivy.exe" image --timeout 15m --exit-code 1 --severity CRITICAL,HIGH --ignore-unfixed employee-api:%BUILD_NUMBER%'
					}
			}
			
			stage('Verify AWS CLI'){
				steps {
					bat 'where aws'
					bat 'aws --version'
					}
				}
			
			stage('ECR Login') {
				steps {
					bat '''
						set AWS_ACCESS_KEY_ID=test
						set AWS_SECRET_ACCESS_KEY=test
						set AWS_DEFAULT_REGION=us-east-1
						
						aws ecr get-login-password ^
						--endpoint-url http://localhost:4566 ^
						--region us-east-1 ^
						| docker login --username AWS ^
						--password-stdin 000000000000.dkr.ecr.us-east-1.localhost:4566
					'''
					}
				}
				
				stage('Push Image to ECR'){
					steps{
						bat '''
							docker tag employee-api:%BUILD_NUMBER% 000000000000.dkr.ecr.us-east-1.localhost:4566/employee-api:%BUILD_NUMBER%
							docker push 000000000000.dkr.ecr.us-east-1.localhost:4566/employee-api:%BUILD_BUMBER%
						'''
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