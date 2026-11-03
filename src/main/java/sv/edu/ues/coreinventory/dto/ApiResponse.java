package sv.edu.ues.coreinventory.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Oculta campos nulos en el JSON si no aplican
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    // Métodos estáticos de ayuda rápida para éxito
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // Métodos estáticos de ayuda rápida para errores
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}