package com.menezesdaniel.controlecontabil.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioDto {

	//classe usuario Data Transfer Object, a qual armazena e envia para o banco de dados
	private String email;
	private String nome;
	private String senha;
}
