package com.dlw.devapps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

/**
 * @author dmitry.lekhtuz@ey.com
 *
 */
@Controller
@Log4j2
public class EmptyResponseController extends AbstractController {

	@GetMapping("/empty")
	public void emptyResult()
	{
		final String _M = "empty():";
		
		log.info("{} started", _M);
	}
}
