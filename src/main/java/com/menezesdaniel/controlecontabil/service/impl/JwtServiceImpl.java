package com.menezesdaniel.controlecontabil.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService{

		// definicao do tempo para a expiracao do token
	@Value("${jwt.expiration}")
	private String expiration;
	
		// definicao da chave de assinatura para a geracao (criptografia) do token
	@Value("${jwt.key-signature}")
	private String keySignature;
	
	@Override
	public String gerarToken(Usuario usuario) {
			// metodo para gerar o token e definir em quanto tempo ele vai expirar, requerendo uma nova autenticacao
		
		long exp = Long.valueOf(expiration);
		LocalDateTime dateTimeExpiration = LocalDateTime.now().plusMinutes(exp);
		Instant instant = dateTimeExpiration.atZone( ZoneId.systemDefault() ).toInstant();
		Date date = Date.from(instant);
		
		String timeExpirationToken = dateTimeExpiration
										.toLocalTime()
										.format(DateTimeFormatter.ofPattern("HH:mm"));
		
		String token = Jwts
						.builder()
						.setExpiration(date)		// hora da expiracao
						.setSubject(usuario.getEmail())		// identificador do usuario
						.claim("userid", usuario.getId())	// claim opcional do token com o id do usuario
						.claim("nome", usuario.getNome())	// claim opcional do token com o nome do usuario
						.claim("timeExpiration", timeExpirationToken)		// claim opcional do token com a hora de expiracao
						.signWith( SignatureAlgorithm.HS512, keySignature )		// chave se assinatura a ser utilizada na criptografia
						.compact();
		
		return token;
	}

	@Override
	public Claims obterClaims(String token) throws ExpiredJwtException {
			// obtem os claims atraves do token

		return Jwts
				.parser()
				.setSigningKey(keySignature)
				.parseClaimsJws(token)
				.getBody();
	}

	@Override
	public boolean isTokenValid(String token) {
			// verifica se o token ainda eh valido
		
		try {
			Claims claims = obterClaims(token);
			Date dateExp = claims.getExpiration();
			
			LocalDateTime dateExpiration = dateExp.toInstant()
													.atZone( ZoneId.systemDefault() ).toLocalDateTime();
			
			boolean dateTimeIsAfter  = LocalDateTime.now().isAfter(dateExpiration);
			
			return !dateTimeIsAfter;
			
		} catch (ExpiredJwtException e) {
			
			return false;
		}
	}

	@Override
	public String obterLoginUsuario(String token) {
			// obtem o login do usuario enviado atraves do token 

		Claims claims = obterClaims(token);		
		return claims.getSubject();
	}

}
