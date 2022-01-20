package com.menezesdaniel.controlecontabil.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.menezesdaniel.controlecontabil.api.dto.UsuarioDto;
import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

	private UsuarioService service;

	//construtor da classe
	public UsuarioResource(UsuarioService service) {
		this.service = service;
	}

	//url para autenticar, seguido do padrao do RequestMapping
	@PostMapping("/autenticar")
	public ResponseEntity autenticar( @RequestBody UsuarioDto dto ) {
		try {
			//compara usuario e a senha recebidos com o armazenado no BD  
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			//retorna o usuario autenticado
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			//mostra o erro que impossibilitou a autenticacao do usuario
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvar( @RequestBody UsuarioDto dto ) {

		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha()).build();

		try{
			//compara usuario e a senha recebidos com o armazenado no BD
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			//retorna o usuario cadastrado e a mensagem de sucesso
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch(RegraNegocioException e) {
			//mostra o erro que impossibilitou o cadastro do usuario
			return ResponseEntity.badRequest().body(e.getMessage()); 
		}

	}
}
