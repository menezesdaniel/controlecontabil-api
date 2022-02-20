package com.menezesdaniel.controlecontabil.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.menezesdaniel.controlecontabil.exception.RegraNegocioException;
import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.entity.Usuario;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;
import com.menezesdaniel.controlecontabil.model.repository.LancamentoRepository;
import com.menezesdaniel.controlecontabil.model.repository.LancamentoRepositoryTest;
import com.menezesdaniel.controlecontabil.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;


	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		//PREPARATION
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// doNothing para impedir que o metodo validar sera chamado 
		Mockito.doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		//verifica se lancamento save foi chamado, efetivando para salvar o lancamento
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		//ACTION
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		//ASSERTION
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(lancamentoSalvo.getStatus());
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//PREPARATION
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

		// apresenta um erro quando o metodo validar eh chamado, que encontra-se dentro do metodo salvar
		Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);

		//ACTION AND ASSERTION AT ONCE
		//chama o metodo salvar e verifica se o tipo capturado que foi retornado eh o esperado
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);

		//verifica se ao chamar o metodo salvar realmente ocorreu erro e o objeto nao chegou a ser salvo
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		//PREPARATION
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);


		// doNothing para impedir que o metodo validar sera chamado 
		Mockito.doNothing().when(service).validar(lancamentoSalvo);


		//verifica se lancamento save foi chamado, efetivando para salvar o lancamento
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		//ACTION
		service.atualizar(lancamentoSalvo);

		//ASSERTION
		//verifica se ao chamar o metodo atualizar, o objeto realmente foi salvo
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}


	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//PREPARATION
		Lancamento lancamentoNaoSalvo = LancamentoRepositoryTest.criarLancamento();

		//ACTION AND ASSERTION AT ONCE
		//chama o metodo atualizar e verifica se o tipo capturado que foi retornado eh o esperado
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamentoNaoSalvo), NullPointerException.class);

		//verifica se ao chamar o metodo salvar realmente ocorreu erro e o objeto nao chegou a ser salvo
		Mockito.verify(repository, Mockito.never()).save(lancamentoNaoSalvo);
	}

	@Test
	public void deveDeletarUmLancamento() {
		//PREPARATION
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		//ACTION
		service.deletar(lancamento);

		//ASSERTION
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//PREPARATION
		Lancamento lancamentoNaoSalvo = LancamentoRepositoryTest.criarLancamento();

		//ACTION
		Assertions.catchThrowableOfType( () -> service.deletar(lancamentoNaoSalvo), NullPointerException.class);

		//ASSERTION
		Mockito.verify(repository, Mockito.never()).delete(lancamentoNaoSalvo);

	}

	@Test
	public void deveFiltrarLancamentos() {
		//PREPARATION
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when( repository.findAll(Mockito.any(Example.class)) ).thenReturn(lista);

		//ACTION
		List<Lancamento> resultado =  service.buscar(lancamento);

		//ASSERTION
		Assertions.assertThat(resultado)
		.isNotEmpty()
		.hasSize(1)
		.contains(lancamento);		
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//PREPARATION
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		//no momento que o service chamar atualizar, obterah como retorno o objeto lancamento criado acima
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		//ACTION
		service.atualizarStatus(lancamento, StatusLancamento.EFETIVADO);


		//ASSERTION
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		//PREPARATION
		Long id = 1l;   

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		//ao inves do service chamar findById, este retornarah o objeto lancamento criado acima
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		//ACTION
		Optional<Lancamento> resultado = service.obterPorId(id);

		//ASSERTION
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExiste() {
		//PREPARATION
		Long id = 1l;   

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		//ao inves do service chamar findById, este retornarah um objeto vazio
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		//ACTION
		Optional<Lancamento> resultado = service.obterPorId(id);

		//ASSERTION
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}


	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();

		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("");

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("Teste");

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Mês válido!");

		lancamento.setMes(13);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Mês válido!");

		lancamento.setMes(0);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Mês válido!");

		lancamento.setMes(1);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Ano válido!");

		lancamento.setAno(0);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Ano válido!");

		lancamento.setAno(999);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Ano válido!");

		lancamento.setAno(2022);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Usuário!");

		lancamento.setUsuario(new Usuario());

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Usuário!");

		lancamento.getUsuario().setId(1l);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.ZERO);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.valueOf(100));

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
		.hasMessage("Informe de Tipo de lançamento!");
	}
}
