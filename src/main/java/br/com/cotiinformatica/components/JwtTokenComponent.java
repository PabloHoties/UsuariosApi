package br.com.cotiinformatica.components;

import org.springframework.stereotype.Component;

import br.com.cotiinformatica.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenComponent {

	public String generateToken(Usuario usuario) throws Exception {
		
		return Jwts.builder()
				.setSubject(usuario.getEmail())
				.signWith(SignatureAlgorithm.HS256, "apiusuarios-cotiinformatica")
				.compact();
	}
}
