package com.menezesdaniel.controlecontabil.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar (Lancamento lancamento);
		//salva o lancamento novo
	
	Lancamento atualizar (Lancamento lancamento);
		//atualiza um lancamento ja existente
	
	void deletar (Lancamento lancamento);
		//deleta um lancamento
	
	List<Lancamento> buscar (Lancamento lancamentoFiltro);
		//busca um lancamento com o determinado filtro
	
	void atualizarStatus (Lancamento lancamento, StatusLancamento status);
		//atualiza o status de um lancamento, recebe o lancamento e o status novo como parametro

	void validar (Lancamento lancamento);
		//valida o lancamento novo a ser inserido ou atualizado
	
	Optional<Lancamento> obterPorId(Long id);
		//busca um id no BD
	
	BigDecimal obterSaldoPorUsuario(Long id);
		//calcula o saldo do id informado
}
