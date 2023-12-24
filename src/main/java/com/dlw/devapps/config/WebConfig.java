package com.dlw.devapps.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

import lombok.Setter;

/**
 * @author dmitry
 *
 */
@Configuration
class WebConfig implements ApplicationContextAware, WebFluxConfigurer {
	@Setter(onMethod = @__(@Override))
	private ApplicationContext applicationContext;

	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver() {
		final SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setCacheable(false);
		return resolver;
	}
}
