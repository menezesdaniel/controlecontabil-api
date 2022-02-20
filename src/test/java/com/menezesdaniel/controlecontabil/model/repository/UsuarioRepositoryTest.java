package com.menezesdaniel.controlecontabil.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.repository.UsuarioRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;

	@Autowired
	TestEntityManager entityManager; 

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//PREPARATION
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		//ACTION
		boolean result = repository.existsByEmail("usuario@email.com");

		//ASSERTION
		Assertions.assertThat(result).isTrue();		
	}

	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		//PREPARATION

		//ACTION
		boolean result = repository.existsByEmail("usuario@email.com");

		//ASSERTION
		Assertions.assertThat(result).isFalse();		
	}

	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//PREPARATION
		Usuario usuario = criarUsuario();

		//ACTION
		Usuario usuarioSalvo = repository.save(usuario);
		
		//ASSERTION
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//PREPARATION
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//ASSERTION
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {		
		//ASSERTION
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
		
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder()
				.nome("usuario").email("usuario@email.com").senha("senha")
				.build();
	}


}
