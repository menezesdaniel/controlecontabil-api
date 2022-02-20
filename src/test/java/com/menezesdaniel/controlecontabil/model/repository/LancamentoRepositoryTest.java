package com.menezesdaniel.controlecontabil.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		//PREPARATION
		Lancamento lancamento = criarLancamento();

		//ACTION
		lancamento = repository.save(lancamento);

		//ASSERTION
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento(){
		//PREPARATION
		Lancamento lancamento = criarEPersistirUmLancamento();

		//ACTION
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());			
		repository.delete(lancamento);

		//ASSERTION
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		//PREPARATION
		Lancamento lancamento = criarEPersistirUmLancamento();

		//ACTION
		lancamento.setAno(2022);
		lancamento.setDescricao("Teste de atualizacao");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		repository.save(lancamento);	

		//ASSERTION
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2022);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste de atualizacao");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	@Test
	public void deveBuscarUmLancamento() {
		//PREPARATION
		Lancamento lancamento = criarEPersistirUmLancamento();

		//ACTION
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());	

		//ASSERTION
		Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	//metodo para criar e persistir um lancamento
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

	//metodo para criar um lancamento
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2021)
				.mes(12)
				.descricao("lancamento teste")
				.valor(BigDecimal.valueOf(200))
				.tipo(TipoLancamento.DESPESA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}


}
