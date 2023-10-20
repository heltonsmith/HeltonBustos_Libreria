def call(def scannerHome, boolean abortPipeline = false, String gitBranch = '') {
    try {
        withSonarQubeEnv(credentialsId: 'sonar-token') {
            sh "${scannerHome}/bin/sonar-scanner"
            timeout(time: 5, unit: 'MINUTES') {
                script {
                    def qualityGate = waitForQualityGate()
                    if (qualityGate.status != 'OK') {
                        // Puedes usar la variable gitBranch aquí según tus condiciones
                        if (abortPipeline) {
                            error "Quality Gate failed: ${qualityGate.status}"
                            error "Se abortará el pipeline debido a la configuración."
                        } else if (gitBranch == 'main' || gitBranch.startsWith('hotfix')) {
                            error "Quality Gate failed: ${qualityGate.status}"
                            error "Se abortará el pipeline según la rama: ${gitBranch}"
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
