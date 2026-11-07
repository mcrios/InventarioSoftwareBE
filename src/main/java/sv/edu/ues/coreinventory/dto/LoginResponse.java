package sv.edu.ues.coreinventory.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sv.edu.ues.coreinventory.entity.Empresa;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private String email;
    private String nombre;
    private List<String> roles;
    private List<Map<String, String>> accesos;
    private List<Empresa> empresas;
}
