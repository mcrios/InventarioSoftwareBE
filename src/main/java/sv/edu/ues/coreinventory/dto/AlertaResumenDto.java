package sv.edu.ues.coreinventory.dto;

/**
 * Resumen de las alertas creadas durante una ejecución del procesamiento.
 */
public record AlertaResumenDto(
        int total,
        int licenciasVencidas,
        int licenciasPorVencer,
        int equiposVulnerables,
        int softwareDesactualizado) {
}
