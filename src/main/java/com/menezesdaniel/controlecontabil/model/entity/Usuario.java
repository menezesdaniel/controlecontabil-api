package com.menezesdaniel.controlecontabil.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "usuario", schema = "contabilidade")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
		//modelos das entidades para as tabelas do BD
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome")
	private String nome;	

	@Column(name = "email")
	private String email;

	@Column(name = "senha")
	@JsonIgnore
	private String senha;
}
