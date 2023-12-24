package com.dlw.devapps.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

import com.google.common.collect.Lists;

import reactor.core.publisher.Mono;

/**
 * @author dmitry
 *
 */
@Controller
@Log4j2
public class QifController extends AbstractController {
	/**
	 * @return
	 */
	@GetMapping("/qif")
	public String showUploadForm()
	{
		final String _M = "showUploadForm():";
		log.info("{} called", _M);
		return("qifuploadform");
	}

	/**
	 * @param model
	 * @param fileParts
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/qif")
	public Mono<Void> upload(final Model model, final @RequestPart("file") Mono<FilePart> fileParts) throws Exception
	{
		final String _M = "upload():";
		log.info("{} started.", _M);

		return fileParts
				.doOnNext(part -> log.info("{} part = {}", _M, part))
				.map(Part::content)
				.map(DataBufferUtils::join)
				.flatMap(content -> content)
				.map(content -> content.asInputStream(true))
				.map(InputStreamReader::new)
				.map(BufferedReader::new)
				.map(BufferedReader::lines)
				.flatMapIterable(lines -> lines
						.filter(StringUtils::isNotBlank)
						.filter(str -> !StringUtils.startsWithAny(str, "!Option:", "!Switch:"))
						.collect(
							() -> new LinkedList<List<String>>(List.of(Lists.newLinkedList())), 
							(acc, str) -> {
								log.info("{} str = {}, size = {}, last size = {}", _M, str, acc.size(), acc.getLast().size());
								if (StringUtils.equals(str, "^")) {
						 			acc.add(Lists.newLinkedList());
						 		} else {
						 			acc.getLast().add(str);
						 		}
						 	}, 
						 	(acc1, acc2) -> log.info("{} combiner called", _M)
						)
				)
				.filter(CollectionUtils::isNotEmpty)
				.collect(() -> new QifCollector(), QifCollector::collect)
				.doOnNext(coll -> model.addAttribute("numAccounts", coll.getAccountCount()))
				.then()
		;
	}

	/**
	 * @author dmitry
	 *
	 */
	@Data
	@AllArgsConstructor
	private static abstract class Account {
		private String name;

		public static Account createAccount(Map<String, String> detailsMap) {
			final String _M = "collect():";

			switch (detailsMap.get("T")) {
				case "Bank":
					return(new BankAccount(detailsMap));

				case "CCard":
					return(new CreditCardAccount(detailsMap));

				default:
					log.warn("{} unrecognized accountType {}", _M, detailsMap.get("T"));
					return(null);
			}
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper=true)
	private static class BankAccount extends Account {
		public BankAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper=true)
	private static class CreditCardAccount extends Account {
		private int limit;

		public CreditCardAccount(Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
			this.limit = Integer.valueOf(detailsMap.get("L").replace(",", ""));
		}
	}

	private static enum AccountType { Bank, CCard };

	/**
	 * @author dmitry
	 *
	 */
	@Log4j2
	private static class QifCollector {
		private Map<String, Account> accounts = new HashMap<>();
		private Account currentAccount = null;

		public void collect(final List<String> record) {
			final String _M = "collect():";
			log.info("{} {} lines to be collected", _M, record.size());

			String header = record.get(0);
			if(StringUtils.equalsIgnoreCase(header, "!Account")) {
				Map<String, String> detailsMap = record
					.stream()
					.skip(1)
					.collect(Collectors.toMap(s -> s.substring(0, 1), s -> s.substring(1)));

				currentAccount = Account.createAccount(detailsMap);
				accounts.put(currentAccount.getName(), currentAccount);

			} else if (header.startsWith("!Type:")) {

			} else if (currentAccount != null) {

			}
		}

		/**
		 * @return
		 */
		public int getAccountCount() {
			return(accounts.size());
		}
	}
}
