package com.menezesdaniel.controlecontabil.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.menezesdaniel.controlecontabil.model.entity.Lancamento;
import com.menezesdaniel.controlecontabil.model.enums.StatusLancamento;
import com.menezesdaniel.controlecontabil.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
	//faz uma query ao banco de dados com o id informado e o tipo de lancamento informado,
		// o resultado a ser mostrado sera a soma dos valores de todos os resultados apresentados
	@Query( value  =
				" select sum(l.valor) from Lancamento l join l.usuario u " +
				" where u.id = :idUsuario and l.tipo = :tipo and l.status = :status group by u ")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
			@Param("idUsuario") Long idUsuario,
			@Param("tipo") TipoLancamento tipo,
			@Param("status") StatusLancamento status );
	
}
