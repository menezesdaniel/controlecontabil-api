package com.menezesdaniel.controlecontabil.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menezesdaniel.controlecontabil.api.dto.UsuarioDto;
import com.menezesdaniel.controlecontabil.exception.ErroAutenticacao;
import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.service.LancamentoService;
import com.menezesdaniel.controlecontabil.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest( controllers = UsuarioResource.class)
@AutoConfigureMockMvc //injeta o MockMvc no contexto da aplicacao.
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	// criacao das constantes as quais serao utilizadas

	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;

	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		//PREPARATION
		String email = "usuario@email.com";
		String senha = "123456";
		UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		//ACTION AND ASSERTION AT ONCE
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/autenticar") )
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		//o .post("/autenticar") farah a chamada para o endpoint
		//o .contentType esta setado para JSON
		//o .content recebe o objeto DTO criado anteriormente, jah convertido para string
		
		mvc.perform(request)
			.andExpect( MockMvcResultMatchers.status().isOk() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
			.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
		//o mockMvc inicia o perform com todo o request anterior e as demais verificacoes necessarias
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception{
		//PREPARATION
		String email = "usuario@email.com";
		String senha = "123456";
		UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();

		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		//ACTION AND ASSERTION AT ONCE
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/autenticar") )
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest() );		
	}
	
	@Test
	public void deveCriarUmUsuario() throws Exception{
		//PREPARATION
		String email = "usuario@email.com";
		String senha = "123456";
		UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		//ACTION AND ASSERTION AT ONCE
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API )
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
			.andExpect( MockMvcResultMatchers.status().isCreated() )
			.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
			.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
			.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );		
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
		//PREPARATION
		String email = "usuario@email.com";
		String senha = "123456";
		UsuarioDto dto = UsuarioDto.builder().email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);

		//ACTION AND ASSERTION AT ONCE
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API )
													.accept(JSON)
													.contentType(JSON)
													.content(json);
		
		mvc.perform(request)
			.andExpect( MockMvcResultMatchers.status().isBadRequest() );	
	}
}
