package com.dlw.devapps.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.util.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.dlw.devapps.domain.EchoResponse;

import lombok.extern.log4j.Log4j2;

import reactor.core.publisher.Mono;

/**
 * @author Dmitry Lekhtuz
 *
 */
@RestController
@RequestMapping("/echo")
@Log4j2
public class EchoController extends AbstractController {

	@RequestMapping
	public Mono<EchoResponse> echo(final ServerWebExchange exchange) throws Exception {
		final String _M = "echo(HttpRequest):";
		log.info("{} started. exchange = {}", _M, exchange);
		log.info("{} attributes = {}", _M, exchange.getAttributes());

		logHttpRequest(exchange.getRequest());

		return exchange.getRequest().getBody()
				.collectList()
				.map(ld -> EchoResponse.builder()
						.success(true)
						.lines(ld.stream()
								.map(db -> {
									try {
										return IOUtils.toString(new BufferedReader(new InputStreamReader(db.asInputStream())));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return("");
								})
								.collect(Collectors.toList()))
						.build());
	}
}
