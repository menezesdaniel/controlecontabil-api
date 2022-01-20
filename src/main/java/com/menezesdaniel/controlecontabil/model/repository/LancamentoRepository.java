package com.menezesdaniel.controlecontabil.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.menezesdaniel.controlecontabil.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
}
