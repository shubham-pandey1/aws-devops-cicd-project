pipeline{
	agent any 
	
	environment{
		ECR_REPOSITORY = 'localhost:4566/000000000000/us-east-1/employee-api'
		}
		
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
					bat '"C:\\Program Files\\trivy_0.76.1_windows-64bit\\trivy.exe" --version'
					}
				}
			
			stage('Trivy Scan'){
				steps{
					bat '"C:\\Program Files\\trivy_0.76.1_windows-64bit\\trivy.exe" image --timeout 15m --exit-code 1 --severity CRITICAL,HIGH --ignore-unfixed employee-api:%BUILD_NUMBER%'
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
						--password-stdin localhost:4566
					'''
					}
				}
				
				stage('Push Image to ECR'){
					steps{
						bat '''
							docker tag employee-api:%BUILD_NUMBER% %ECR_REPOSITORY%:%BUILD_NUMBER%
							docker push %ECR_REPOSITORY%:%BUILD_NUMBER%
						'''
						}
				}
				
				stage('Test Kubernetes Access'){
					steps{
						withCredentials([file(credentialsId: 'kubeconfig-k3s', variable: 'KUBECONFIG')]){
							bat '''
								echo Kubernetes configuration:
								kubectl config current-context
								
								echo Kubernetes nodes:
								kubectl get nodes
								kubectl get deployment employee-api
								kubectl get pods
								'''
								}
							}
						}
				
				stage('Deploye to Kubernetes'){
					steps {
						withCredentials([file(credentialsId: 'kubeconfig-k3s', variable: 'KUBECONFIG'),
						string(credentialsId: 'mysql-root-password', variable: 'MYSQL_ROOT_PASSWORD')
						]) {
						bat '''
							echo Updating MySQL Kubernetes Secret...
							
							kubectl create secret generic mysql-secret ^
							--from-literal=MYSQL_ROOT_PASSWORD="%MYSQL_ROOT_PASSWORD" ^
							--dry-run=client -o yaml | kubectl apply -f -
							
							if errorlevel 1 exit /b 1
							
							echo Applying MySQL manifests...
							kubectl apply -f k8s/mysql.yaml
							
							if errorlevel 1 exit /b 1
							
							kubectl rollout status deployment/mysql --timeout=5m
							
							if errorlevel 1 exit /b 1
							
							echo Applying Employee API manifests...
							
							kubectl apply -f k8s/deployment.yaml
							
							if errorlevel 1 exit /b 1
							
							echo Updating Employee API image...
							
							kubectl set image deployment/employee-api ^
							employee-api=%ECR_REPOSITORY%:%BUILD_NUMBER%
							
							if errorlevel 1 exit /b 1
							
							echo Waiting for Employee API rollout...
							kubectl rollout status deployment/employee-api --timeout=5m
							
							if errorlevel 1 exit /b 1
							
							echo Final Kubernetes status...
							
							kubectl get pods -o wide
							kubectl get services
							kubectl get deployments
						'''
						}
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
					echo 'CI pipeline completed successfully. Employee API deployed to Floci Kubernetes.'
					}
					
					failure {
					echo 'CI pipeline failed.'
					}
				}
			}