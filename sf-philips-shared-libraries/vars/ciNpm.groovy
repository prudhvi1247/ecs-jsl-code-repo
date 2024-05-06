// ciNpm.groovy should be in the vars directory of your Jenkins shared library
def call(Closure configClosure) {
    def config = [
        npmInstall: false,  // Default to run npm install
        build: false,       // Default to not run build unless specified
        unitTest: false,     // Default to run tests unless specified
    ]

    // Apply the user's configuration closure to the config map
    configClosure.resolveStrategy = Closure.DELEGATE_FIRST
    configClosure.delegate = config
    configClosure()

    node {
        if (config.npmInstall) {
            stage('NPM Install') {
                echo 'Running npm install'
                // Run npm install command
                sh 'npm ci'
            }
        }
        
        if (config.build) {
            stage('NPM Build') {
                echo 'Running build'
                // Run npm build command
                sh 'npm run build'
            }
        }

        if (config.unitTest) {
            stage('NPM Test') {
                echo 'Running unit tests'
                // Run npm test command
                sh 'npm test'
            }
        }
    }
}
