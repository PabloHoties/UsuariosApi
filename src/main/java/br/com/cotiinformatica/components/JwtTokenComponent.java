package br.com.cotiinformatica.components;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.cotiinformatica.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenComponent {

	@Value("${jwt.secretkey}")
	private String secretKey;
	
	@Value("${jwt.expiration}")
	private Integer expiration;
	
	public String generateToken(Usuario usuario) throws Exception {
		
		Date dataAtual = new Date();
		
		return Jwts.builder()
				.setSubject(usuario.getEmail())
				.setNotBefore(dataAtual)
				.setExpiration(new Date(dataAtual.getTime() + expiration))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
	
	// Método para ler o email do usuário que está gravado no token
	public String getEmailFromToken(String token) {
		return getSubject(token, Claims::getSubject);
	}
	
	private <T> T getSubject(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = Jwts.parser().setSigningKey(secretKey)
				.parseClaimsJws(token).getBody();
		
		return claimsResolver.apply(claims);
	}
}
