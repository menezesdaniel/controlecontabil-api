package com.menezesdaniel.controlecontabil.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;
import com.menezesdaniel.controlecontabil.model.repository.LancamentoRepository;
import com.menezesdaniel.controlecontabil.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
		//declaracao para o construtor da classe
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
		lancamento.setDataCadastro(LocalDate.now());
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		
		Objects.requireNonNull(lancamento.getId());
			//verifica se o lancamento possui id, existindo no BD
		validar(lancamento);
		
		return repository.save(lancamento);
			//salva o lancamento com as alteracoes
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		
		Objects.requireNonNull(lancamento.getId());
			//verifica se o lancamento possui id, existindo no BD
		
		repository.delete(lancamento);		
			//deleta o lancamento
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		
		// pega um instacia do objeto Lancamento preenchido como exemplo e faz a consulta baseado nas propriedades que foram informadas
		Example example = Example.of( 
							lancamentoFiltro, 
							ExampleMatcher.matching()
								.withIgnoreCase()
								.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		
		lancamento.setStatus(status);
			//seta o lancamento com o novo status
		
		atualizar(lancamento);		
			//atualiza o lancamento para garantir que a operacao foi realizada
	}

	@Override
	public void validar(Lancamento lancamento) {

		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida!");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes()<1 || lancamento.getMes()>12) {
			throw new RegraNegocioException("Informe um Mês válido!");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido!");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário!");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido!");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe de Tipo de lançamento!");
		}
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
			//busca um id no BD
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
			//obtem os saldos dos usuario informado por 2 tipos principais de lancamentos através de uma query para o BD
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
			// caso tenha retorno null, sera atribuido o valor zero ao tipo de lancamento
		if (receitas == null) {
			receitas = BigDecimal.ZERO;			
		}		
		if (despesas == null) {
			despesas = BigDecimal.ZERO;			
		}
		
		return receitas.subtract(despesas);
			//faz o calculo da operacao subtraindo as receitas das despesas
	}	
}
