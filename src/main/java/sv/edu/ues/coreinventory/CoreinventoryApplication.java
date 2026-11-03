package sv.edu.ues.coreinventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import sv.edu.ues.coreinventory.entity.Usuario;
import sv.edu.ues.coreinventory.repository.UsuarioRepository;

@SpringBootApplication
@RequiredArgsConstructor 
public class CoreinventoryApplication implements CommandLineRunner {

	private final UsuarioRepository userRepository; // Inyecta el servicio de usuario
	private final PasswordEncoder passwordEncoder; // Inyecta el codificador de contraseñas

	public static void main(String[] args) {
		SpringApplication.run(CoreinventoryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String testEmail = "admin@coreinventory.com";

        // Verificamos si ya existe para no duplicarlo cada vez que reinicies la app
        if (userRepository.findByEmailWithRolesAndAccesos(testEmail).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setEmail(testEmail);
            // Se encripta automáticamente con Argon2 usando el bean de seguridad
            usuario.setPassword(passwordEncoder.encode("Password123!"));
            usuario.setActivo(true);
			usuario.setNombre("Administrador");
            
            // ⚠️ Ajusta aquí si tu entidad Usuario requiere otros campos obligatorios (como nombre, apellido, etc.)
            // usuario.setNombre("Administrador");
            // usuario.setApellido("Sistema");

            userRepository.save(usuario);
            
            System.out.println("==================================================");
            System.out.println("🚀 ¡USUARIO DE PRUEBA CREADO EXITOSAMENTE!");
            System.out.println("📧 Email:    " + testEmail);
            System.out.println("🔑 Password: Password123!");
            System.out.println("==================================================");
        }
	}

}
