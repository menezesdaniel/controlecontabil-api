package com.menezesdaniel.controlecontabil.service;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;

public interface UsuarioService {
	
	//autentica um usuario com e-mail e senha
	Usuario autenticar(String email, String senha);
	
	//salva um usuario novo
	Usuario salvarUsuario(Usuario usuario);
	
	//valida o e-mail recebido no momento do cadastro, de forma a não cadastrar 2 usuarios
	// com o mesmo e-mail
	void validarEmail(String email);
}
