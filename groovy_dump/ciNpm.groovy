// In your vars/ciNpm.groovy
def call(Closure configClosure) {
    def config = [:]

    // Configure defaults or any initial settings
    config.build = false // Default value

    // Apply the user's configuration closure to the config map
    configClosure.resolveStrategy = Closure.DELEGATE_FIRST
    configClosure.delegate = config
    configClosure()

    echo "Configuration: ${config}"

    pipeline {
        agent any
        stages {
            stage('NPM Build') {
                when {
                    expression { config.build }
                }
                steps {
                    echo "Running npm build..."
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
    }
}
