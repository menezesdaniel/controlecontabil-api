package com.menezesdaniel.controlecontabil.service;

import java.util.Optional;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
		//autentica um usuario com e-mail e senha
	
	Usuario salvarUsuario(Usuario usuario);
		//salva um usuario novo
	
	void validarEmail(String email);
		//valida o e-mail recebido no momento do cadastro, de forma a não cadastrar 2 usuarios com o mesmo e-mail
	
	Optional<Usuario> obterPorId(Long id);
		//objeto Usuario do tipo Optional pois, retorna o id, caso existe, ou retorna vazio, caso contrario
}
