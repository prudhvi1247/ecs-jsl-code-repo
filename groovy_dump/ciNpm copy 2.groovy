// ciNpm.groovy should be in the vars directory of your Jenkins shared library.
def call(Map params) {
    echo "Starting ciNpm with params: ${params}"
    if (params.build) {
        echo "Running build..."
        sh 'npm install'
        sh 'npm run build'
    } else {
        echo "Build parameter not set or false"
    }
}
