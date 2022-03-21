package com.menezesdaniel.controlecontabil.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //mapeamento lombok
@Builder //mapeamento lombok
@NoArgsConstructor //construtor default vazio da classe
@AllArgsConstructor //construtor default da classe com todos os argumentos (a ser utilizado pelo Builder)
public class LancamentoDto {
	
	private Long id;
	private String descricao;
	private Integer mes;
	private Integer ano;
	private BigDecimal valor;
	private Long usuario; //sera recebido o ID do usuario
	private String tipo; //sera recebida uma String com o tipo
	private String status; //nao sera recebido o status, porem vai 
	// ser retornado na pesquisa no corpo da resposta
}
