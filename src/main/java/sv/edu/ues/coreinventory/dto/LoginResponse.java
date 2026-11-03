package sv.edu.ues.coreinventory.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token; // Aquí podrás retornar tu JWT más adelante
    private String email;
}