// ciMaven.groovy should be in the vars directory of your Jenkins shared library
def call(Closure configClosure) {
    def config = [
        build: false,  // Default to not build unless specified
        unitTest: false,  // Default to not run tests unless specified
        sonar: false,  // Default to not perform SonarQube analysis unless specified
        buildCommand: 'mvn clean install -DskipTests',
        testCommand: 'mvn test',
        sonarCommand: 'mvn sonar:sonar'
    ]

    // Apply the user's configuration closure to the config map
    configClosure.resolveStrategy = Closure.DELEGATE_FIRST
    configClosure.delegate = config
    configClosure()

    node {
        if (config.build) {
            stage('Build') {
                echo "Running Build with command: ${config.buildCommand}"
                sh config.buildCommand
            }
        }
        if (config.unitTest) {
            stage('Test') {
                echo "Running Tests with command: ${config.testCommand}"
                sh config.testCommand
            }
        }
        if (config.sonar) {
            stage('SonarQube Analysis') {
                echo "Running SonarQube Analysis with command: ${config.sonarCommand}"
                sh config.sonarCommand
            }
        }
    }
}
