def call(Closure configClosure) {
    def config = [
        clusterName: '',
        serviceName: '',
        taskDefinition: '',
        containerName: '',
        image: '',
        awsRegion: '',
        deploy: true // Default to deploy unless specified
    ]

    // Apply the user's configuration closure to the config map
    configClosure.resolveStrategy = Closure.DELEGATE_FIRST
    configClosure.delegate = config
    configClosure()

    node {
        if (config.deploy) {
            stage('ECS Deployment') {
                withAWS(credentials: 'sf-phillips-jenkins-deployment', region: config.awsRegion) {
                    try {
                        def ecs = new com.amazonaws.services.ecs.AmazonECSClient()

                        // Register new task definition revision with the updated image
                        def registerTaskDefinitionRequest = new com.amazonaws.services.ecs.model.RegisterTaskDefinitionRequest()
                        def containerDefinition = new com.amazonaws.services.ecs.model.ContainerDefinition()
                        containerDefinition.setName(config.containerName)
                        containerDefinition.setImage(config.image)
                        containerDefinition.setEssential(true)
                        registerTaskDefinitionRequest.setContainerDefinitions([containerDefinition])
                        registerTaskDefinitionRequest.setFamily(config.taskDefinition)
                        def registerTaskDefinitionResult = ecs.registerTaskDefinition(registerTaskDefinitionRequest)

                        // Update ECS service to use the new task definition revision
                        def updateServiceRequest = new com.amazonaws.services.ecs.model.UpdateServiceRequest()
                        updateServiceRequest.setCluster(config.clusterName)
                        updateServiceRequest.setService(config.serviceName)
                        updateServiceRequest.setTaskDefinition(registerTaskDefinitionResult.getTaskDefinition().getTaskDefinitionArn())
                        ecs.updateService(updateServiceRequest)

                        echo "ECS service ${config.serviceName} updated with new task definition revision using image ${config.image}"
                    } catch (Exception e) {
                        echo "Error deploying to ECS: ${e.message}"
                        error "Failed to deploy to ECS"
                    }
                }
            }
        } else {
            echo "ECS deployment skipped as 'deploy' option is set to false"
        }
    }
}
