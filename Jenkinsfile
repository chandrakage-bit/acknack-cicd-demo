pipeline {
    agent any

    options {
        timeout(time: 20, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        IMAGE      = 'acknack-service'
        TAG        = "${env.BUILD_NUMBER}"
        CLUSTER    = 'demo'
        KUBECONFIG = '/var/jenkins_home/.kube/config'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Unit Test') {
            steps { sh 'mvn -B -ntp clean verify' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }

        stage('Package Artifact') {
            steps { archiveArtifacts artifacts: 'target/*.jar', fingerprint: true }
        }

        stage('Docker Build') {
            steps { sh 'docker build -t ${IMAGE}:${TAG} .' }
        }

        stage('Load Image into kind') {
            steps { sh 'kind load docker-image ${IMAGE}:${TAG} --name ${CLUSTER}' }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    sed "s/IMAGE_TAG/${TAG}/" k8s/deployment.yaml | kubectl apply -f -
                    kubectl apply -f k8s/service.yaml
                    kubectl rollout status deployment/acknack-service --timeout=180s
                '''
            }
        }

        stage('Smoke Test') {
            steps { sh 'sh scripts/smoke-test.sh' }
        }
    }

    post {
        failure {
            echo 'Pipeline failed - rolling back deployment'
            sh 'kubectl rollout undo deployment/acknack-service || true'
        }
        success { echo "Build ${TAG} deployed successfully" }
    }
}
