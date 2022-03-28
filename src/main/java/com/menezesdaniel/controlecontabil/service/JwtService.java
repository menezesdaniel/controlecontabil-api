package com.menezesdaniel.controlecontabil.service;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {
	
	String gerarToken(Usuario usuario);
	
	Claims obterClaims(String token) throws ExpiredJwtException;
	
	boolean isTokenValid(String token);
	
	String obterLoginUsuario(String token);
}
