#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x gradlew"
        sh "./gradlew clean"
    }


    stage('install') {
        try {
            sh "./gradlew build publishToMavenLocal"
        } catch (err) {
            throw err
        }
    }
}

