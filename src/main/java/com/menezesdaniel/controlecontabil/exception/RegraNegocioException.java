package com.menezesdaniel.controlecontabil.exception;

public class RegraNegocioException extends RuntimeException {
	
	//mostra a mensagem de erro quando ha erro no cadastro
	public RegraNegocioException(String msg) {
		super(msg);		
	}
}
