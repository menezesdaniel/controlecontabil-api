package com.menezesdaniel.controlecontabil.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.repository.LancamentoRepository;
import com.menezesdaniel.controlecontabil.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	private LancamentoRepository repository;
	
	// nao necessita autowired, pois ja eh um bean gerenciado
	// construtor da classe
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}

	@Override //sobrescreve o metodo da superclasse
	@Transactional //anotacao importante para a transacao do BD, confirma o sucesso ou volta ao estado anterior
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		
		//verifica se realmente existe um lancamento com id indicado
		Objects.nonNull(lancamento.getId());
		validar(lancamento);
		
		//salva o lancamento com as alteracoes
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		
		//verifica se realmente existe um lancamento com id indicado
		Objects.nonNull(lancamento.getId());
		
		//deleta o lancamento
		repository.delete(lancamento);		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		
		// pega um instacia do objeto Lancamento preenchido como exemplo e
		// faz a consulta baseado nas propriedades que foram informadas
		Example example = Example.of( lancamentoFiltro, 
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		
		//seta o lancamento com o novo status
		lancamento.setStatus(status);
		
		//atualiza o lancamento para garantir que a operacao foi realizada
		atualizar(lancamento);		
	}

	@Override
	public void validar(Lancamento lancamento) {
		// TODO Auto-generated method stub
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida!");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes()<1 || lancamento.getMes()>12) {
			throw new RegraNegocioException("Informe um Mês válido!");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido!");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getId() == null) {
			throw new RegraNegocioException("Informe um Usuário!");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido!");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe de Tipo de lançamento!");
		}
	}
	
	

}