node {
    stage("Checkout") {
        checkout scm
    }

    stage("Clean") {
        sh "./gradlew --info clean"
    }

    try {
        stage("Build") {
            sh "./gradlew --info build"
        }
/*    
        stage("Upload archives") {
            sh "./gradlew --info uploadArchives"
        }
        
        if (env.BRANCH_NAME == "master") {
            stage("Publish plugin") {
                sh "./gradlew --info bintrayUpload publishPlugins"
            }
        }
*/
    } finally {
        stage("Publish reports") {
            sh "./gradlew --info sonarqube"
            publishUnitTestResults()
            publishCheckstyleResults()
            publishCpdResults()
        }
    }
}

def publishUnitTestResults() {
    step([$class: "JUnitResultArchiver", testResults: "build/**/TEST-*.xml"])
}

def publishCheckstyleResults() {
    step([$class: "CheckStylePublisher",
          canComputeNew: false,
          defaultEncoding: "",
          healthy: "",
          pattern: "build/reports/checkstyle/*.xml",
          unHealthy: ""])
}

def publishCpdResults() {
    step([$class: 'DryPublisher', canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'build/reports/cpd/*.xml', unHealthy: ''])
}
