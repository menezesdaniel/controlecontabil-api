package com.menezesdaniel.controlecontabil.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.repository.UsuarioRepository;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

		//declaracao para o construtor da classe
	private UsuarioRepository repository;
	private PasswordEncoder encoder;

		//construtor da classe
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override //sobrescreve o metodo da superclasse
	public Usuario autenticar(String email, String senha) {
				//procura o email do usuario informado no BD
		Optional<Usuario> usuario = repository.findByEmail(email);

			//mostra msg de erro se o email não existe no BD
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		
		boolean passwordsMatches = encoder.matches(senha, usuario.get().getSenha());
			//verifica se a senha informada, eh igual a senha criptografada presente no BD
		
			//mostra msg de erro se a senha não confere com a existente no BD
		if(!passwordsMatches) {
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
		criptografarSenha(usuario);
		return repository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
			//recebe a senha do usuario a ser cadastrado
		String senhaCripto = encoder.encode(senha);
			//criptografa a senha recebida
		usuario.setSenha(senhaCripto);
			//seta a senha cadastrada do usuario para a criptografada
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
	
		//objeto Usuario do tipo Optional pois, retorna o id, caso existe, ou retorna vazio, caso contrario
	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}
}
