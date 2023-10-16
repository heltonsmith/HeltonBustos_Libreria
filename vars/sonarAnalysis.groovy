def call(boolean abortPipeline = false) {
    try {
        withSonarQubeEnv(credentialsId: 'sonar-token') {
            sh "${scannerHome}/bin/sonar-scanner"
            timeout(time: 5, unit: 'MINUTES') {
                script {
                    def qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        error "Quality Gate failed: ${qualityGate.status}"
                        if (abortPipeline) {
                            error "QualityGate de SonarQube falló, se abortará el pipeline."
                        }
                    }
                }
            }
        }
    } catch (Exception e) {
        error("Error al ejecutar SonarQube: ${e.getMessage()}")
        if (abortPipeline) {
            error("QualityGate de SonarQube falló, se abortará el pipeline.")
        }
    }
}
