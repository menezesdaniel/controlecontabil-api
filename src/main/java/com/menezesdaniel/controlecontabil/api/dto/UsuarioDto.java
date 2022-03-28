package com.menezesdaniel.controlecontabil.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor //construtor default vazio da classe
@AllArgsConstructor //construtor default da classe com todos os argumentos (a ser utilizado pelo Builder)
public class UsuarioDto {

		//classe usuario Data Transfer Object, a qual armazena e envia para o banco de dados
	private String email;
	private String nome;
	private String senha;
}
