package sv.edu.ues.coreinventory.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import sv.edu.ues.coreinventory.entity.Acceso;
import sv.edu.ues.coreinventory.entity.Empresa;
import sv.edu.ues.coreinventory.entity.Rol;
import sv.edu.ues.coreinventory.entity.Usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_NOMBRE = "nombre";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_ACCESOS = "accesos";
    private static final String CLAIM_EMPRESA = "empresa";
    private static final String CLAIM_EMPRESAS = "empresas";

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 días por defecto
    private long refreshTokenExpiration;

    // ---------------------------------------------------------------------
    // Generación de tokens
    // ---------------------------------------------------------------------

    /**
     * Genera el access token. Incluye el nombre del usuario, sus roles,
     * sus accesos (permisos) y la(s) empresa(s) a la(s) que pertenece.
     */
    public String generateAccessToken(Usuario usuario) {
        return buildToken(usuario, jwtExpiration, TOKEN_TYPE_ACCESS, true);
    }

    /**
     * Genera el refresh token. Se mantiene ligero: solo lleva el email
     * (subject), el nombre y el tipo de token. Los roles y accesos se
     * vuelven a consultar en la base de datos al renovar el access token.
     */
    public String generateRefreshToken(Usuario usuario) {
        return buildToken(usuario, refreshTokenExpiration, TOKEN_TYPE_REFRESH, false);
    }

    /**
     * Alias de compatibilidad: por defecto genera un access token.
     */
    public String generateToken(Usuario usuario) {
        return generateAccessToken(usuario);
    }

    private String buildToken(Usuario usuario, long expiration, String tipo, boolean incluirPerfil) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(usuario.getEmail())
                .claim(CLAIM_TYPE, tipo)
                .claim(CLAIM_NOMBRE, usuario.getNombre())
                .issuedAt(issuedAt)
                .expiration(expiresAt);

        if (incluirPerfil) {

            builder.claim(CLAIM_ROLES, usuario.getRoles().stream().map(Rol::getNombre).toList())
                    .claim(CLAIM_ACCESOS, usuario.getRoles().stream()
                        .flatMap(rol -> rol.getAccesos().stream())
                        .collect(Collectors.toMap(
                            Acceso::getId,           
                            acceso -> Map.of(
                                "path", acceso.getPath(),
                                "descripcion", acceso.getDescripcion()
                            ),
                            (existente, nuevo) -> existente 
                        )))
                    .claim(CLAIM_EMPRESAS, usuario.getEmpresas().stream().map(Empresa::getNombre).toList());
        }

        return builder
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // ---------------------------------------------------------------------
    // Lectura de claims
    // ---------------------------------------------------------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_TYPE, String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ---------------------------------------------------------------------
    // Validación
    // ---------------------------------------------------------------------

    /**
     * Valida un access token. Un refresh token NO es aceptado aquí, por lo
     * que no puede usarse para consumir los endpoints protegidos.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return TOKEN_TYPE_ACCESS.equals(extractTokenType(token))
                && isTokenOwnedBy(token, userDetails);
    }

    /**
     * Valida un refresh token para el endpoint de renovación.
     */
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return TOKEN_TYPE_REFRESH.equals(extractTokenType(token))
                && isTokenOwnedBy(token, userDetails);
    }

    private boolean isTokenOwnedBy(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // ---------------------------------------------------------------------
    // Utilidades
    // ---------------------------------------------------------------------

    public long getAccessTokenExpirationSeconds() {
        return jwtExpiration / 1000;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration / 1000;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
