pipeline {
  agent any
  parameters {
    string(name: 'JDK', description: 'JDK tool name (e.g. jdk8, jdk11)')
  }

  environment {
    // TODO: fix tests so we don't have to do this
    TZ = 'America/Los_Angeles'
  }

  tools {
    maven 'maven'
    jdk params.JDK
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }
    stage('Archive') {
      // TODO: fix tests so they don't delete JARs we want to keep
      steps {
        archiveArtifacts '**/target/*.jar'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
      }
      post {
        always {
          junit '**/target/surefire-reports/TEST-*.xml'
        }
      }
    }
  }
}
