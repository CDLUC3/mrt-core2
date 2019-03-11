pipeline {
  agent any

  environment {
    // TODO: fix tests so we don't have to do this
    TZ = 'America/Los_Angeles'
  }

  tools {
    maven 'maven'
  }

  stages {
    stage('Cross-JDK build') {
      // TODO: can we use parallel{} if we isolate workspaces somehow?
      stages {
        // TODO: DRY these
        stage('JDK 8') {
          tools {
            jdk 'jdk8'
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

        stage('JDK 11') {
          tools {
            jdk 'jdk11'
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
      }
    }
  }
}