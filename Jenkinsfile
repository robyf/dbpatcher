node {
    stage("Checkout") {
        checkout scm
    }

    stage("Clean") {
        sh "./gradlew --info clean"
    }

    stage("Build") {
        sh "./gradlew --info build"
    }
    
    stage("Publish reports") {
        publishUnitTestResults()
        publishCheckstyleTestResults()
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
