package com.menezesdaniel.controlecontabil.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.menezesdaniel.controlecontabil.api.JwtTokenFilter;
import com.menezesdaniel.controlecontabil.service.JwtService;
import com.menezesdaniel.controlecontabil.service.impl.SecurityUserDetailsServiceImpl;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private SecurityUserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private JwtService jwtService;
	
	@Bean //o objeto abaixo sera registrado no contexto do Spring, na presente classe de configuracao 
	public PasswordEncoder passwordEncoder() {
		
		PasswordEncoder encoder = new BCryptPasswordEncoder();
			// a senha sera codificada pelo metodo BCrypt
		
		return encoder;
	}
	
	@Bean
	public JwtTokenFilter jwtTokenFilter(){
		return new JwtTokenFilter(jwtService, userDetailsService);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				
		auth.
			userDetailsService(userDetailsService) //autenticacao recorrente atraves de um servico especifico, nao permanecendo em memoria
			.passwordEncoder(passwordEncoder());				
	}	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			.csrf().disable() //desabilita a autorizacao de token padrao, via formulario
			.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
					//permitira todas as requisicoes POST para a url acima
				.antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
					//permitira todas as requisicoes POST para a url acima
				.anyRequest().authenticated()
					//eh necessario autenticacao para todas as demais requisicoes
		.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)				
				//exige uma nova autenticacao a cada nova requisicao, se necessario
		.and()
			.addFilterBefore( jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class );
				//a chamada do jwtTokenFilter sera realizada antes do filtro que realiza a autenticacao
				// utiliza-se do JwtToken ao inves do Authentication Basic, tornando a validacao mais segura
	}	
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		
		List<String> all = Arrays.asList("*");
		
		CorsConfiguration config = new CorsConfiguration();
		
		config.setAllowedMethods(all);		
		config.setAllowedOriginPatterns(all);
		config.setAllowedHeaders(all);
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		CorsFilter corsFilter = new CorsFilter(source);
		FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corsFilter);
		
		filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		return filter;				
	}
}
