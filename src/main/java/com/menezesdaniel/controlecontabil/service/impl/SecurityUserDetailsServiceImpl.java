package com.menezesdaniel.controlecontabil.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.repository.UsuarioRepository;

@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {
	
		//declaracao para o construtor da classe
	private UsuarioRepository usuarioRepository;
	
		//construtor da classe
	public SecurityUserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Usuario foundUser = usuarioRepository
				.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("E-mail não encontrado!"));
		
		return User.builder()
						.username(foundUser.getEmail())
						.password(foundUser.getSenha())
						.roles("USER")		// perfil de usuario padrão do Spring
						.build();
			// autentica a requisicoes pelos proprios usuarios cadastrados no BD
	}		
}
