pipeline {
  agent any

  environment {
    // TODO: fix tests so we don't have to do this
    TZ = 'America/Los_Angeles'
  }

  tools {
    maven 'maven'
    jdk 'jdk8' // TODO: figure out how to parameterize this
  }

  stages {
    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
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
    stage('Archive') {
      steps {
        archive 'target/*.jar'
      }
    }
  }
}