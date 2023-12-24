package com.dlw.devapps.controller;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.http.server.reactive.ServerHttpRequest;

import lombok.extern.log4j.Log4j2;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Log4j2
public abstract class AbstractController {

	/**
	 * @param request
	 */
	protected void logHttpRequest(ServerHttpRequest request) {
		final String _M = "logHttpRequest(HttpRequest):";
		
		log.info("{} id             : {}", _M, request.getId());
		log.info("{} method         : {}", _M, request.getMethod());
		log.info("{} local address  : {}", _M, request.getLocalAddress());
		log.info("{} remote address : {}", _M, request.getRemoteAddress());
		log.info("{} URI            : {}", _M, request.getURI());
		log.info("{} path           : {}", _M, request.getPath());

		consumeMultiValueMap(request.getQueryParams(), (k, v) -> {
			log.info("{} query param    : {} = {}", _M, k, v);
		});

		consumeMultiValueMap(request.getHeaders(), (k, v) -> {
			log.info("{} header         : {} = {}", _M, k, v);
		});

		consumeMultiValueMap(request.getCookies(), (k, v) -> {
			log.info("{} cookie         : {} = {}", _M, k, v.getValue());
		});
	}
	
	/**
	 * @param <T>
	 * @param map
	 * @param consumer
	 */
	private <T>void consumeMultiValueMap(final Map<String, List<T>> map, BiConsumer<String, T> consumer) {
		map.forEach((key, list) -> list.forEach(value -> consumer.accept(key, value)));
	}
}
