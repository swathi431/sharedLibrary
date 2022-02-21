def call(String registryCred = 'a', String registryin = 'a', String docTag = 'a', String contName = 'a') {

pipeline {
environment { 
		registryCredential = "${registryCred}"
		registry = "$registryin" 	
		dockerTag = "${docTag}_$BUILD_NUMBER"
		containerName = "${contName}"
	}
		
	agent { label 'docker' }
	
	triggers {
		pollSCM '* * * * *'
	}

	stages {
		stage("POLL SCM"){
			steps {
				  git branch: 'main', url: 'https://github.com/JevitaD/nodejs-k8s.git'
			}
		}	
					
		stage('BUILD IMAGE') { 
			 steps { 
				 script { 
					 dockerImage = docker.build('"$registry:$dockerTag"') 
				 }
			} 
		}
					
		stage('PUSH HUB') { 
			 steps { 
				 script { 
					 docker.withRegistry( '', "$registryCredential" ) { 
						 dockerImage.push() 
					}
				}		
			} 
		}
					
	}
			  
}

}