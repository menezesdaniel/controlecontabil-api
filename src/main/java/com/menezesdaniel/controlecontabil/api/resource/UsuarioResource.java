package com.menezesdaniel.controlecontabil.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.menezesdaniel.controlecontabil.api.dto.TokenDto;
import com.menezesdaniel.controlecontabil.api.dto.UsuarioDto;
import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.service.JwtService;
import com.menezesdaniel.controlecontabil.service.LancamentoService;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

		//declaracao para o construtor da classe
	private UsuarioService service;
	private LancamentoService lancamentoService;
	private JwtService jwtService;

		//construtor da classe
	public UsuarioResource(UsuarioService service,
							LancamentoService lancamentoService,
							JwtService jwtService) {
		this.service = service;
		this.lancamentoService = lancamentoService;
		this.jwtService = jwtService;
	}

		//url para autenticar, seguido do padrao do RequestMapping
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar( @RequestBody UsuarioDto dto ) {
			// <?> -> pode retornar mais de um objeto em seu retorno
		
		try {			
			Usuario authenticatedUser = service.autenticar( dto.getEmail(), dto.getSenha() );
				//compara usuario e a senha recebidos com o armazenado no BD
						
			String token = jwtService.gerarToken( authenticatedUser );
			
			TokenDto tokenDto = new TokenDto( authenticatedUser.getNome(), token);
				// armazena o nome e email do usuario, os quais foram armazenados no token
			
			return ResponseEntity.ok(tokenDto);
				//retorna o usuario autenticado
			
		} catch (ErroAutenticacao e) {
				//mostra o erro que impossibilitou a autenticacao do usuario
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

		//url para salvar, sera na propria raiz da API
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

	@GetMapping("{id}/saldo") //o put recebera o nr de id na url o qual sera atualizado,
		//seguido de saldo, de modo a diferencia-lo das demais funcoes
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
			//PathVariable traz para o ambiente o valor da variavel do metodo get

		Optional<Usuario> usuario = service.obterPorId(id);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity( HttpStatus.NOT_FOUND );
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
}
