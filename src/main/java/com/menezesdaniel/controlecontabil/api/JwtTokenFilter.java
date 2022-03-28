package com.menezesdaniel.controlecontabil.api;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.menezesdaniel.controlecontabil.service.JwtService;
import com.menezesdaniel.controlecontabil.service.impl.SecurityUserDetailsServiceImpl;

public class JwtTokenFilter extends OncePerRequestFilter{
		//pode ser executado apenas uma unica vez por requisicao (OncePerRequestFilter)
	
		//declaracao para o construtor da classe
	private JwtService jwtService;
	private SecurityUserDetailsServiceImpl userDetailsService;

	//construtor da classe
	public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsServiceImpl userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;		
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
										HttpServletResponse response, 
										FilterChain filterChain) throws ServletException, IOException {
		//intercepta a requisicao, decodifica o JWT Token e possibilita a realizacao da autenticacao do usuario
		
		String authorization = request.getHeader("Authorization");
				
		if(authorization != null && authorization.startsWith("Bearer")) {
			// verifica se o token recebido não está nulo e se inicia com "Bearer", conforme padrao estabelecido
			
			String token = authorization.split(" ")[1];
			boolean isTokenValidNow = jwtService.isTokenValid(token);
			
			if(isTokenValidNow){				
				String login = jwtService.obterLoginUsuario(token);
				UserDetails authenticatedUser = userDetailsService.loadUserByUsername(login);
				
				UsernamePasswordAuthenticationToken user = 
						new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
					//transforma os detalhes de UserDetails e transforma no Token nativo do Spring
				
				user.setDetails( new WebAuthenticationDetailsSource().buildDetails(request) );
				SecurityContextHolder.getContext().setAuthentication(user);
					//informa o usuario para o AuthenticationManagerBuilder criado do SecurityConfiguration para realizar a autenticacao do usuario				
			}		
		}	
		filterChain.doFilter(request, response);
		//darah continuidade execucao da requisicao 
	}
}
