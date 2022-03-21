package com.menezesdaniel.controlecontabil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	
	@Override
	public void addCorsMappings( CorsRegistry registry ) {
		//CORS ou Cross-Origin Resource Sharing 
		//o presente metodo habilita o recebimento de requisicoes de determinada origem
		registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
		//com a configuracao acima o api fica aberto para...
			//requisicoes de todas as origens e com os metodos listados
	}
}
