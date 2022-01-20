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
		//scene
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1l).nome("nome")
				.email("email@email.com").senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//action
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//check
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//scene
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		//action
		Usuario result = service.autenticar(email, senha);
		
		//check
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		//scene
		Mockito.when(repository.findByEmail(Mockito
				.anyString())).thenReturn(Optional.empty());
		
		//action
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "senha"));
		
		//check
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class)
		.hasMessage("Usuário não encontrado para o email informado.");
				
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//scene
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//action
		org.junit.jupiter.api.Assertions
		.assertThrows(RegraNegocioException.class, () -> service
				.salvarUsuario(usuario));
		
		//check
		Mockito.verify( repository, Mockito.never() ).save(usuario);
	}

	@Test
	public void deveLancarErroQuandoASenhaNaoBater() {
		//scene
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).
		thenReturn(Optional.of(usuario));
		
		//action+
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
	}
	
	@Test
	public void deveValidarEmail() {
		//scene
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		//action
		service.validarEmail("email@email.com");
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//scene
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//action
		org.junit.jupiter.api.Assertions
		.assertThrows(RegraNegocioException.class, () -> service
				.validarEmail("email@email.com"));
	}
}
