package br.com.cotiinformatica.configurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.cotiinformatica.filters.JwtBearerFilter;

@Configuration
public class JwtBearerConfig {

	/*
	 * Este método deverá configurar o filtro do JWT (JwtBearerFilter)
	 * e definir para quais endpoints da API este filtro será aplicado.
	 */
	@Bean
	FilterRegistrationBean<JwtBearerFilter> jwtFilter() {
		
		// Adicionando o filtro que irá validar os TOKENS
		FilterRegistrationBean<JwtBearerFilter> filter = new FilterRegistrationBean<JwtBearerFilter>();
		filter.setFilter(new JwtBearerFilter());
		
		// Mapeando os endpoints da API que irão exigir autenticação
		filter.addUrlPatterns("/api/usuarios/obter-dados");
		
		return filter;
	}
}
