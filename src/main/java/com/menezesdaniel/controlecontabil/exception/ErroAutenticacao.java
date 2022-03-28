package com.menezesdaniel.controlecontabil.exception;

public class ErroAutenticacao extends RuntimeException {
	
		//mostra a mensagem de erro na autenticacao
	public ErroAutenticacao(String msg) {
		super(msg);
	}
}
