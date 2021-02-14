package com.dlw.devapps;

import java.time.ZoneId;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ConfigurableApplicationContext;

import com.dlw.devapps.config.AppProperties;

import lombok.extern.log4j.Log4j2;

/**
 * @author Dmitry Lekhtuz
 *
 */
@SpringBootApplication
@Log4j2
public class DevappsApplication implements ApplicationRunner {
	@Autowired private BuildProperties buildProperties;
	@Autowired private AppProperties appProperties;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String _M = "main():";
		log.info("{} started. args = {}", _M, Arrays.toString(args));

		ConfigurableApplicationContext context = SpringApplication.run(DevappsApplication.class, args);

//		log.trace("{} available bean names = {}", () -> _M, () -> Arrays.toString(context.getBeanDefinitionNames()));
		log.info("{} ended.", _M);
	}


	/**
	 * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		final String _M = "build properties:";

		buildProperties.forEach(action -> {
			String value = action.getKey().equalsIgnoreCase("time") 
					? buildProperties.getTime().atZone(ZoneId.systemDefault()).toString()
							: action.getValue();
			log.info("{} {} = {}", _M, action.getKey(), value);
		});
		
		log.info("app   properties: {}", appProperties);
	}
}
