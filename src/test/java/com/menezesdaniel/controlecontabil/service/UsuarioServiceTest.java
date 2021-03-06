package com.menezesdaniel.controlecontabil.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.repository.UsuarioRepository;
import com.menezesdaniel.controlecontabil.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUsuario() {
		//PREPARATION
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome")
				.email("email@email.com").senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ACTION
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//ASSERTION
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//PREPARATION
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		//ACTION
		Usuario result = service.autenticar(email, senha);
		
		//ASSERTION
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//PREPARATION
		Mockito.when(repository.findByEmail(Mockito
				.anyString())).thenReturn(Optional.empty());
		
		//ACTION
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "senha"));
		
		//ASSERTION
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
		.hasMessage("Usu??rio n??o encontrado para o email informado.");
				
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//PREPARATION
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//ACTION
		org.junit.jupiter.api.Assertions
		.assertThrows(RegraNegocioException.class, () -> service
				.salvarUsuario(usuario));
		
		//ASSERTION
		Mockito.verify( repository, Mockito.never() ).save(usuario);
	}

	@Test
	public void deveLancarErroQuandoASenhaNaoBater() {
		//PREPARATION
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).
		thenReturn(Optional.of(usuario));
		
		//ACTION
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inv??lida.");
	}
	
	@Test
	public void deveValidarEmail() {
		//PREPARATION
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		//ACTION
		service.validarEmail("email@email.com");
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//PREPARATION
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ACTION
		org.junit.jupiter.api.Assertions
		.assertThrows(RegraNegocioException.class, () -> service
				.validarEmail("email@email.com"));
	}
}
