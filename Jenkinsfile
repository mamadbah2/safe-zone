// filepath: /home/mamadbah/Java/mr-jenk/Jenkinsfile.optimized
pipeline {
    agent any

    // NOTE: Tous les stages s'exécutent sur le même agent (Jenkins choisit un agent disponible)

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_HUB_USER = 'mamadbah2'
        IMAGE_VERSION = "${env.BUILD_NUMBER}"
        GITHUB_TOKEN = credentials('GITHUB_TOKEN')

        // Media Service credentials
        MONGODB_URI = credentials('MONGODB_URI_BOBO')
        MONGODB_DATABASE = credentials('MONGODB_DATABASE')
        SUPABASE_PROJECT_URL = credentials('SUPABASE_PROJECT_URL')
        SUPABASE_API_KEY = credentials('SUPABASE_API_KEY')
        SUPABASE_BUCKET_NAME = credentials('SUPABASE_BUCKET_NAME')

        // Cache directories
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        NPM_CONFIG_CACHE = '.npm-cache'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                echo '🧹 Nettoyage de l\'espace de travail...'
                sh '''
                    # Nettoyer seulement les conteneurs et images de l'ancien build
                    docker-compose down -v --remove-orphans || true
                    docker images | grep "my_buy01_pipeline2" | grep -v "${IMAGE_VERSION}" | awk '{print $3}' | xargs -r docker rmi -f || true
                '''
            }
        }

        stage('Build & Unit Tests') {
            parallel {
                stage('Frontend') {
                    steps {
                        echo '🧪 Tests Frontend Angular (Headless)...'
                        dir('buy-01-frontend') {
                            sh '''
                                npm ci --cache ${NPM_CONFIG_CACHE}
                                npm run test:headless
                            '''
                        }
                    }
                }

                stage('Backend Services') {
                    steps {
                        echo '🚀 Build et Tests des Services Backend...'
                        sh '''
                            # Build tous les services Maven (utilise le cache MAVEN_OPTS configuré)
                            mvn -T 1C verify \
                                -f discovery-service/pom.xml \
                                -f config-service/pom.xml \
                                -f api-gateway/pom.xml \
                                -f product-service/pom.xml \
                                -f user-service/pom.xml \
                                -f media-service/pom.xml \
                                --batch-mode \
                                -Dmaven.test.failure.ignore=false
                        '''
                    }
                }
            }
            post {
                always {
                    // Les rapports JUnit et les artefacts proviennent des agents; nous archiveons depuis le contrôleur
                    // pour éviter les problèmes d'accès, on copie d'abord les artefacts si nécessaire.
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
                }
            }
        }

        // Ajout : SonarQube analysis inspiré de Jenkinsfile(safe)
        stage('Sonar Analysis') {
            steps {
                echo '🔍 Exécution des analyses Sonar pour les services backend...'
                script {
                    // Utilise l'environnement Sonar configuré dans Jenkins (withSonarQubeEnv)
                    withSonarQubeEnv('safe-zone-mr-jenk') {
                        withCredentials([string(credentialsId: 'SONAR_USER_TOKEN', variable: 'SONAR_USER_TOKEN')]) {
                            def services = ['discovery-service','config-service','api-gateway','product-service','user-service','media-service']
                            services.each { svc ->
                                echo "🔎 Sonar pour ${svc}..."
                                def pom = "${svc}/pom.xml"
                                // Certaines applications produisent des rapports JaCoCo
                                def jacocoOption = ''
                                if (svc in ['product-service','user-service','media-service']) {
                                    jacocoOption = "-Dsonar.coverage.jacoco.xmlReportPaths=${svc}/target/site/jacoco/jacoco.xml"
                                }

                                sh """
                                    mvn -f ${pom} sonar:sonar \
                                        -Dsonar.projectKey=sonar-${svc.replace('-service','')} \
                                        -Dsonar.host.url=$SONAR_HOST_URL \
                                        -Dsonar.token=$SONAR_USER_TOKEN \
                                        -Dsonar.java.binaries=${svc}/target/classes \
                                        ${jacocoOption}
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Sonar Quality Gate') {
            steps {
                timeout(time:25, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo '🐳 Construction des images Docker en parallèle...'
                script {
                    def services = ['eureka-server', 'config-service', 'api-gateway', 'product-service', 'user-service', 'media-service', 'frontend']
                    def parallelBuilds = [:]

                    services.each { service ->
                        parallelBuilds[service] = {
                            def serviceDir = service == 'frontend' ? 'buy-01-frontend' : service.replace('eureka-server', 'discovery-service')
                            echo "🔨 Construction de ${service}..."
                            sh """
                                docker build -t my_buy01_pipeline2-${service}:latest \
                                    --build-arg BUILDKIT_INLINE_CACHE=1 \
                                    -f ${serviceDir}/Dockerfile \
                                    ${serviceDir}
                            """
                        }
                    }

                    parallel parallelBuilds
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🧪 Tests d\'intégration...'
                script {
                    timeout(time: 10, unit: 'MINUTES') {
                        try {
                            withEnv([
                                "IMAGE_VERSION=${env.BUILD_NUMBER}",
                                "GITHUB_TOKEN=${env.GITHUB_TOKEN}",
                                "SUPABASE_PROJECT_URL=${env.SUPABASE_PROJECT_URL}",
                                "SUPABASE_API_KEY=${env.SUPABASE_API_KEY}",
                                "SUPABASE_BUCKET_NAME=${env.SUPABASE_BUCKET_NAME}",
                                "MONGODB_URI=${env.MONGODB_URI}",
                                "MONGODB_DATABASE=${env.MONGODB_DATABASE}"
                            ]) {
                                sh '''
                                    docker-compose up -d

                                    # Attendre que les services soient prêts
                                    echo "⏳ Attente du démarrage des services..."
                                    sleep 60

                                    # Vérifier que les services sont en bonne santé
                                    docker-compose ps
                                '''
                            }
                        } finally {
                            sh 'docker-compose logs --tail=50'
                            sh 'docker-compose down -v --remove-orphans'
                        }
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    echo '📤 Push des images vers Docker Hub...'

                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credential',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    }

                    def services = ['frontend', 'product-service', 'user-service', 'media-service', 'api-gateway', 'config-service', 'eureka-server']
                    def parallelPushes = [:]

                    services.each { service ->
                        parallelPushes[service] = {
                            def localImageName = "my_buy01_pipeline2-${service}"
                            def taggedImageName = "${DOCKER_HUB_USER}/${service}:${env.BUILD_NUMBER}"
                            def latestImageName = "${DOCKER_HUB_USER}/${service}:latest"

                            echo "📦 Push de ${service}..."
                            sh """
                                docker tag ${localImageName}:latest ${taggedImageName}
                                docker tag ${localImageName}:latest ${latestImageName}
                                docker push ${taggedImageName}
                                docker push ${latestImageName}
                            """
                        }
                    }

                    parallel parallelPushes

                    sh 'docker logout'
                }
            }
        }

        stage('Deploy Locally') {
            steps {
                script {
                    echo "🚀 Déploiement local, version ${env.BUILD_NUMBER}..."

                    timeout(time: 10, unit: 'MINUTES') {
                        withEnv([
                            "IMAGE_VERSION=${env.BUILD_NUMBER}",
                            "GITHUB_TOKEN=${env.GITHUB_TOKEN}",
                            "SUPABASE_PROJECT_URL=${env.SUPABASE_PROJECT_URL}",
                            "SUPABASE_API_KEY=${env.SUPABASE_API_KEY}",
                            "SUPABASE_BUCKET_NAME=${env.SUPABASE_BUCKET_NAME}",
                            "MONGODB_URI=${env.MONGODB_URI}",
                            "MONGODB_DATABASE=${env.MONGODB_DATABASE}"
                        ]) {
                            sh '''
                                docker-compose -f docker-compose-deploy.yml down
                                docker-compose -f docker-compose-deploy.yml pull
                                docker-compose -f docker-compose-deploy.yml up -d

                                # Vérifier l'état des conteneurs
                                sleep 30
                                docker-compose -f docker-compose-deploy.yml ps
                            '''
                        }
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    echo '🏥 Vérification de la santé des services...'
                    timeout(time: 5, unit: 'MINUTES') {
                        sh '''
                            # Attendre que tous les services soient en bonne santé
                            for i in {1..30}; do
                                if docker-compose -f docker-compose-deploy.yml ps | grep -q "unhealthy"; then
                                    echo "⏳ Attente de la santé des services... ($i/30)"
                                    sleep 10
                                else
                                    echo "✅ Tous les services sont en bonne santé"
                                    exit 0
                                fi
                            done

                            echo "❌ Timeout: certains services ne sont pas en bonne santé"
                            docker-compose -f docker-compose-deploy.yml ps
                            exit 1
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                // s'assurer que les commandes Docker du post s'exécutent sur le noeud contrôleur
                node {
                    // Nettoyer les anciennes images
                    sh '''
                        docker images | grep "${DOCKER_HUB_USER}" | grep -v "${IMAGE_VERSION}" | grep -v "latest" | awk '{print $3}' | xargs -r docker rmi -f || true
                    '''

                    mail(
                        to: 'bahmamadoubobosewa@gmail.com',
                        subject: "SUCCESS: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                        body: "Le pipeline a réussi.\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_NUMBER}\nVoir les détails: ${env.BUILD_URL}"
                    )
                }
            }
        }

        failure {
            script {
                echo "⚠️ Pipeline échouée, rollback en cours..."

                def lastSuccessfulBuild = currentBuild.previousSuccessfulBuild

                node {
                    if (lastSuccessfulBuild && lastSuccessfulBuild.number != env.BUILD_NUMBER) {
                        echo "🔄 Rollback vers la version ${lastSuccessfulBuild.number}..."
                        try {
                            withEnv(["IMAGE_VERSION=${lastSuccessfulBuild.number}"]) {
                                sh '''
                                    docker-compose -f docker-compose-deploy.yml down
                                    docker-compose -f docker-compose-deploy.yml pull
                                    docker-compose -f docker-compose-deploy.yml up -d
                                '''
                            }
                            echo "✅ Rollback réussi vers la version ${lastSuccessfulBuild.number}"
                        } catch (Exception e) {
                            echo "❌ Échec du rollback: ${e.message}"
                        }
                    } else {
                        echo "⚠️ Aucune version précédente disponible pour rollback."
                    }

                    mail(
                        to: 'bahmamadoubosewa@gmail.com',
                        subject: "FAILURE: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                        body: "Le pipeline a echoue.\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_NUMBER}\nVoir les détails: ${env.BUILD_URL}"
                    )
                }
            }
        }

        always {
            script {
                node {
                    echo '🧹 Nettoyage final...'
                    sh '''
                        # Nettoyer les conteneurs arrêtés et les images dangereuses
                        docker container prune -f || true
                        docker image prune -f || true
                    '''
                }
            }
        }
    }
}
