package com.menezesdaniel.controlecontabil.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;

public interface LancamentoService {
	
	//salva o lancamento novo
	Lancamento salvar (Lancamento lancamento);
	
	//atualiza um lancamento ja existente
	Lancamento atualizar (Lancamento lancamento);
	
	//deleta um lancamento
	void deletar (Lancamento lancamento);
	
	//busca um lancamento com o determinado filtro
	List<Lancamento> buscar (Lancamento lancamentoFiltro);
	
	//atualiza o status de um lancamento, recebe o lancamento e o status novo como parametro
	void atualizarStatus (Lancamento lancamento, StatusLancamento status);

	//valida o lancamento novo a ser inserido ou atualizado
	void validar (Lancamento lancamento);
	
	//busca um id no BD
	Optional<Lancamento> obterPorId(Long id);
	
	//calcula o saldo do id informado
	BigDecimal obterSaldoPorUsuario(Long id);
}
