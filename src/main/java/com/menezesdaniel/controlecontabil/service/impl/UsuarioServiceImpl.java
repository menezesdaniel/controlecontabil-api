package com.menezesdaniel.controlecontabil.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.repository.UsuarioRepository;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;

	// construtor da classe
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override //sobrescreve o metodo da superclasse
	public Usuario autenticar(String email, String senha) {
		//procura o email do usuario informado no BD
		Optional<Usuario> usuario = repository.findByEmail(email);

		//mostra msg de erro se o email não existe no BD
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		
		//mostra msg de erro se a senha não confere com a existente no BD
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		//retorna usuario, se autenticar com sucesso
		return usuario.get();
	}

	@Override
	@Transactional //anotacao importante para a transacao do BD, confirma o sucesso ou volta ao estado anterior
	public Usuario salvarUsuario(Usuario usuario) {
		//valida o e-mail recebido no momento do cadastro, de forma a não cadastrar 2 usuarios
		// com o mesmo e-mail
		validarEmail(usuario.getEmail());
		//retorna usuario, se cadastrar com sucesso
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		//verifica se o email recebido já existe no BD
		boolean existe = repository.existsByEmail(email);
		//mostra msg de erro se o email já existir no BD
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
		}
	}


}
