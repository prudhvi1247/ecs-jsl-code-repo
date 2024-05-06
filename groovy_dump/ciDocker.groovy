def call(Closure configClosure) {
    def config = [
        dockerBuild: false, // Default to not build Docker images unless specified
        imageName: '',
        tag: '',
        registryUrl: '',
        environment: '',
        awsRegion: ''
    ]

    // Apply the user's configuration closure to the config map
    configClosure.resolveStrategy = Closure.DELEGATE_FIRST
    configClosure.delegate = config
    configClosure()

    // Define the repository name with the environment included
    def repositoryName = "${config.imageName}-${config.environment}"

    // Authenticate with ECR using Jenkins credentials
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'sf-phillips-jenkins-prudhvi', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        
        // Run Docker-related steps inside a stage for better visualization in Jenkins UI
        stage('Docker Build & Push') {
            // Run inside a node to provide workspace context
            node {
                // Login to ECR
                sh "aws ecr get-login-password --region ${config.awsRegion} | docker login --username AWS --password-stdin ${config.registryUrl}"
                
                // Check if there are any Docker images
                def dockerImages = sh(script: 'docker images -q', returnStdout: true).trim()
                
                // Remove Docker images if any exist
                if (dockerImages) {
                    sh "docker rmi --force ${dockerImages}"
                } else {
                    echo "No Docker images found to remove."
                }
    
                // Build the Docker image
                sh "docker build -t ${config.imageName}:${config.tag} ."
                
                // Tag the Docker image
                sh "docker tag ${config.imageName}:${config.tag} ${config.registryUrl}/${repositoryName}:${config.tag}"
                
                // Push the Docker image to ECR
                sh "docker push ${config.registryUrl}/${repositoryName}:${config.tag}"
                
                // Remove all local Docker images
                sh "docker rmi --force \$(docker images -q)"         
            }
        }
    }
}


// def call(Closure configClosure) {
//     def config = [
//         dockerBuild: false, // Default to not build Docker images unless specified
//         imageName: '',
//         tag: '',
//         registryUrl: '',
//         environment: '',
//         awsRegion: ''
//     ]

//     // Apply the user's configuration closure to the config map
//     configClosure.resolveStrategy = Closure.DELEGATE_FIRST
//     configClosure.delegate = config
//     configClosure()

//     // Define the repository name with the environment included
//     def repositoryName = "${config.imageName}-${config.environment}"

//     // Authenticate with ECR using Jenkins credentials
//     withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'sf-phillips-jenkins-prudhvi', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
        
//         // Run Docker-related steps inside a stage for better visualization in Jenkins UI
//         stage('Docker Build & Push') {
//             // Run inside a node to provide workspace context
//             node {
//                 // Login to ECR
//                 sh "aws ecr get-login-password --region ${config.awsRegion} | docker login --username AWS --password-stdin ${config.registryUrl}"
                
//                 // Remove all local Docker images
//                 sh "docker rmi --force \$(docker images -q)" 
    
//                 // Build the Docker image
//                 sh "docker build -t ${config.imageName}:${config.tag} ."
                
//                 // Tag the Docker image
//                 sh "docker tag ${config.imageName}:${config.tag} ${config.registryUrl}/${repositoryName}:${config.tag}"
                
//                 // Push the Docker image to ECR
//                 sh "docker push ${config.registryUrl}/${repositoryName}:${config.tag}"
                
//                 // Remove all local Docker images
//                 sh "docker rmi --force \$(docker images -q)"         
//             }
//         }
//     }
// }



// def call(Closure configClosure) {
//     def config = [
//         dockerBuild: false, // Default to not build Docker images unless specified
//         imageName: '',
//         tag: '',
//         registryUrl: '',
//         environment: '',
//         awsRegion: ''
//     ]

//     // Apply the user's configuration closure to the config map
//     configClosure.resolveStrategy = Closure.DELEGATE_FIRST
//     configClosure.delegate = config
//     configClosure()

//     // Define the repository name with the environment included
//     def repositoryName = "${config.imageName}-${config.environment}"

//     // Authenticate with ECR using Jenkins credentials
//     withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'sf-phillips-jenkins-prudhvi', accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
//         // Run inside a node to provide workspace context
//         node {
//             // Login to ECR
//             sh "aws ecr get-login-password --region ${config.awsRegion} | docker login --username AWS --password-stdin ${config.registryUrl}"
            
//             // Remove all local Docker images
//             sh "docker rmi --force \$(docker images -q)" 

//             // Build the Docker image
//             sh "docker build -t ${config.imageName}:${config.tag} ."
            
//             // Tag the Docker image
//             sh "docker tag ${config.imageName}:${config.tag} ${config.registryUrl}/${repositoryName}:${config.tag}"
            
//             // Push the Docker image to ECR
//             sh "docker push ${config.registryUrl}/${repositoryName}:${config.tag}"
            
//             // Remove all local Docker images
//             sh "docker rmi --force \$(docker images -q)"         
//         }
//     }
// }
