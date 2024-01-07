package com.dlw.devapps.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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

import com.google.common.collect.ImmutableList;

import reactor.core.publisher.Flux;
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
	public Mono<Void> upload(final Model model, final @RequestPart("file") Mono<FilePart> filePart) throws Exception
	{
		final String _M = "upload():";
		log.info("{} started.", _M);

		return filePart
				.doOnNext(part -> log.info("{} part = {}", _M, part))
				.map(Part::content)
				.map(DataBufferUtils::join)
				.flatMap(content -> content)
				.map(content -> content.asInputStream(true))
				.map(InputStreamReader::new)
				.map(BufferedReader::new)
				.map(BufferedReader::lines)
				.map(Stream::toList)
				.flatMapMany(Flux::fromIterable)
				.map(StringUtils::trim)
				.filter(StringUtils::isNotBlank)
				.filter(str -> !StringUtils.startsWithAny(str, "!Option:", "!Switch:", "!Clear:"))
				.collect(
					() -> new ArrayList<>(ImmutableList.of(new ArrayList<String>())), 
					(acc, str) -> {
						log.debug("{} str = {}, size = {}, last size = {}", _M, str, acc.size(), acc.getLast().size());
						if (StringUtils.equals(str, "^")) {
				 			acc.add(new ArrayList<>());
				 		} else {
				 			acc.getLast().add(str);
				 		}
				 	}
				)
				.flatMapMany(Flux::fromIterable)
				.filter(CollectionUtils::isNotEmpty)
				.collect(() -> new QifCollector(), QifCollector::collect)
				.doOnNext(coll -> model.addAttribute("accounts", coll.getAccounts()))
				.then()
		;
	}

	/**
	 * 
	 */
	@RequiredArgsConstructor
	private static enum AccountType {
		Bank("Bank"), CCard("CCard"), Cash("Cash"), OthA("Oth A"), OthL("Oth L"), 
		Port("Port"), Retirement("401(k)/403(b)"), Invst("Invst"), Unknown("Unknown");
		
		private final String qifTitle;

		/**
		 * @param qifTitle
		 * @return
		 */
		public static AccountType getByQifTitle(final String qifTitle) {
			return Arrays.stream(AccountType.values())
					.filter(at -> StringUtils.equalsIgnoreCase(at.qifTitle, qifTitle))
					.findAny()
					.orElse(Unknown)
			;
		}
	};

	/**
	 * 
	 */
	@Data
	private static abstract class Transaction {
		private LocalDate transactionDate;
	}

	/**
	 * @author dmitry
	 *
	 */
	@Data
	@RequiredArgsConstructor
	@Log4j2
	private static abstract class Account<T extends Transaction> {
		final private String name;
		private List<T> transactions;

		/**
		 * @param detailsMap
		 * @return
		 */
		public static Account<? extends Transaction> createAccount(final Map<String, String> detailsMap) {
			final String _M = "createAccount():";

			switch (AccountType.getByQifTitle(detailsMap.get("T"))) {
				case Bank:
					return(new BankAccount(detailsMap));

				case CCard:
					log.info("{} detailsMap = {}, T={}", _M, detailsMap, detailsMap.get("T"));
					return(new CreditCardAccount(detailsMap));

				case Cash:
					return(new CashAccount(detailsMap));

				case OthA:
				case OthL:
					return(new OthAccount(detailsMap));

				case Port:
					return(new PortAccount(detailsMap));

				case Retirement:
					return(new RetirementAccount(detailsMap));

				default:
					return null;
			}
		}
		
		/**
		 * @param transactionMap
		 */
		public void addTransaction(final Map<String, String> transactionMap) {
			
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class BankAccount extends Account<Transaction> {
		public BankAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	@Log4j2
	private static class CreditCardAccount extends Account<Transaction> {
		private int limit;

		public CreditCardAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
			final String _M = "CreditCardAccount():";
			log.info("{} detailsMap = {}", _M, detailsMap);

			limit = Integer.valueOf(detailsMap.getOrDefault("L", "0").replaceAll("\\.|,", ""))/100;
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class CashAccount extends Account<Transaction> {
		public CashAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class OthAccount extends Account<Transaction> {
		public OthAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class PortAccount extends Account<Transaction> {
		public PortAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Value
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	private static class RetirementAccount extends Account<Transaction> {
		public RetirementAccount(final Map<String, String> detailsMap) {
			super(detailsMap.get("N"));
		}
	}

	/**
	 * @author dmitry
	 *
	 */
	@Log4j2
	private static class QifCollector {
		@Getter private Map<String, Account> accounts = new HashMap<>();
		private Account currentAccount = null;
		private int accountSectionNum = -1;

		/**
		 * @param record
		 */
		public void collect(final List<String> record) {
			final String _M = "collect():";
			log.info("{} accountSectionNum = {}, currentAccount = {}, record = {}", _M, accountSectionNum, currentAccount, record);

			final boolean isAccountRecord = StringUtils.equalsIgnoreCase(record.get(0), "!Account");

			if (isAccountRecord) {
				accountSectionNum ++;
			}

			if (isAccountRecord || accountSectionNum == 0) {
				final Map<String, String> detailsMap = record.stream()
						.filter(s -> !StringUtils.startsWith(s, "!"))
						.collect(Collectors.toMap(s -> s.substring(0, 1), s -> s.substring(1)));
				final String accountName = detailsMap.get("N");

				log.info("{} accountSectionNum = {}, currentAccount = {}, detailsMap = {}", _M, accountSectionNum, currentAccount, detailsMap);
				if (accountSectionNum == 0) {
					log.info("{} adding {}", _M, accountName);
					accounts.computeIfAbsent(accountName, k -> Account.createAccount(detailsMap));
				} else {
					log.info("{} retreiving {}", _M, accountName);
					currentAccount = accounts.compute(accountName, (accName, account) -> account == null ? Account.createAccount(detailsMap) : account);
				}
				return;
			}

			// Process transaction data
		}
	}
}
