package com.dlw.devapps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.dlw.devapps.domain.OauthTokenRequest;
import com.dlw.devapps.domain.OauthTokenResponse;
import com.dlw.devapps.service.TokenService;

import lombok.extern.log4j.Log4j2;

import reactor.core.publisher.Mono;

/**
 * @author Dmitry Lekhtuz
 *
 */
@RestController
@RequestMapping("/oauth")
@Log4j2
public class OauthController extends AbstractController {
	@Autowired private TokenService tokenService;

	/**
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public Mono<OauthTokenResponse> token(final ServerWebExchange exchange) {
		final String _M = "token(HttpRequest):";
		log.info("{} started. exchange = {}", _M, exchange);
		log.info("{} attributes = {}", _M, exchange.getAttributes());

		logHttpRequest(exchange.getRequest());
		
		return exchange
				.getFormData()
				.log()
				.map(formData -> tokenService.getToken(OauthTokenRequest.builder()
						.clientId(formData.getFirst("client_id"))
						.clientSecret(formData.getFirst("client_secret"))
						.grantType(formData.getFirst("grant_type"))
						.scope(formData.getFirst("scope"))
						.build()));
	}
}
