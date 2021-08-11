package com.dlw.devapps.controller;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

/**
 * @author dmitry.lekhtuz@ey.com
 *
 */
@Controller
@Log4j2
public class EmptyResponseController extends AbstractController {

//	@DeleteMapping("/empty/resea/pool/{claimantId}/{effectiveDate}")
	@ResponseBody public Mono<String> emptyResult(final ServerHttpResponse response)
	{
		final String _M = "empty():";
		
		log.info("{} started", _M);

		return(Mono.empty());
	}
	
	@DeleteMapping("/empty/resea/pool/{claimantId}/{effectiveDate}")
	@ResponseBody public Mono<Payload> jsonResult(final ServerHttpResponse response)
	{
		final String _M = "jsonResult():";
		
		log.info("{} started", _M);

		return(Mono.just(Payload.builder().message("hello").build()));
	}
	
	@Data
	@Builder
	static private class Payload {
		private String message;
	}
}
