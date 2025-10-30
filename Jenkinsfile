pipeline {
    agent any

    parameters {
        string(name: 'DOCKER_IMAGE', defaultValue: 'your-dockerhub-username/cowrite', description: 'Docker image name')
        string(name: 'DOCKER_TAG', defaultValue: "${env.BUILD_NUMBER}", description: 'Docker tag')
        string(name: 'REGISTRY_CREDENTIALS', defaultValue: 'docker-hub-credentials', description: 'Registry credentials ID')
        string(name: 'SSH_CONFIG', defaultValue: 'your-ssh-config-name', description: 'Jenkins SSH host config')
        string(name: 'CONTAINER_NAME', defaultValue: 'cowrite', description: 'Runtime container name')
        string(name: 'PORTS', defaultValue: '-p 8080:8080', description: 'Run port mappings')
    }

    tools {
        maven 'Maven3'
        jdk 'JDK11'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${params.DOCKER_IMAGE}:${params.DOCKER_TAG}")
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    docker.withRegistry('', params.REGISTRY_CREDENTIALS) {
                        docker.image("${params.DOCKER_IMAGE}:${params.DOCKER_TAG}").push()
                    }
                }
            }
        }

        stage('Deploy to Remote Server') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: params.SSH_CONFIG,
                            transfers: [
                                sshTransfer(
                                    execCommand: """
                                        docker pull ${params.DOCKER_IMAGE}:${params.DOCKER_TAG} &&
                                        docker stop ${params.CONTAINER_NAME} || true &&
                                        docker rm ${params.CONTAINER_NAME} || true &&
                                        docker run -d --name ${params.CONTAINER_NAME} ${params.PORTS} ${params.DOCKER_IMAGE}:${params.DOCKER_TAG}
                                    """
                                )
                            ]
                        )
                    ]
                )
            }
        }
    }
}
