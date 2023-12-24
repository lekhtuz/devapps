package com.dlw.devapps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.dlw.devapps.domain.ResourceResponse;
import com.dlw.devapps.config.AppProperties;
import com.dlw.devapps.domain.InvalidTokenException;
import com.dlw.devapps.domain.InvalidTokenResponse;
import com.dlw.devapps.service.TokenService;

import lombok.extern.log4j.Log4j2;

import reactor.core.publisher.Mono;

/**
 * @author Dmitry Lekhtuz
 *
 */
@RestController
@RequestMapping("/resource")
@Log4j2
public class ResourceController extends AbstractController {
	@Autowired private TokenService tokenService;
	@Autowired private AppProperties props;

	/**
	 * @param exchange
	 * @return
	 */
	@GetMapping
	public Mono<ResourceResponse> resource(final ServerWebExchange exchange) {
		final String _M = "resource(ServerWebExchange):";
		log.info("{} started. exchange = {}", _M, exchange);
		log.info("{} attributes = {}", _M, exchange.getAttributes());

		logHttpRequest(exchange.getRequest());

		String authHeader = exchange
				.getRequest()
				.getHeaders()
				.getFirst(HttpHeaders.AUTHORIZATION);
		log.info("{} authHeader = {}", _M, authHeader);
		
		String bearerToken = "";
		if (StringUtils.startsWithIgnoreCase(authHeader, props.getTokenType() + " ")) {
			bearerToken = StringUtils.split(authHeader, " ")[1];
		}
		log.info("{} bearerToken = {}", _M, bearerToken);

		tokenService.validateToken(bearerToken);

		log.info("{} ended", _M);
		return(Mono.just(ResourceResponse.builder()
				.success(true)
				.build()));
	}
	
	/**
	 * @param e
	 * @return
	 */
	@ExceptionHandler(InvalidTokenException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public InvalidTokenResponse handleTokenExpiredException(final InvalidTokenException e, final ServerWebExchange exchange) {
		final String _M = "handleTokenExpiredException(): ";
		log.info("{} called.", _M, e);

		StringBuffer errorMessage = new StringBuffer();
		errorMessage.append(props.getTokenType());
		errorMessage.append(" error=\"invalid_token\" error_description=\"The access token expired\"");
		
		exchange.getResponse().getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, errorMessage.toString());
		return InvalidTokenResponse.invalid_token;
	}

}
