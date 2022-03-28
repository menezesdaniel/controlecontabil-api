package com.menezesdaniel.controlecontabil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ControlecontabilApplication implements WebMvcConfigurer {	
	
	public static void main(String[] args) {		
		SpringApplication.run(ControlecontabilApplication.class, args);
	}

}
