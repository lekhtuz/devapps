package com.dlw.devapps.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlw.devapps.config.AppProperties;
import com.dlw.devapps.domain.OauthTokenRequest;
import com.dlw.devapps.domain.OauthTokenResponse;
import com.dlw.devapps.domain.InvalidTokenException;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Service
@Log4j2
public class TokenServiceImpl implements TokenService {
	@Autowired private AppProperties props;
	private Map<String, TokenInfo> tokenStore = new HashMap<>();

	/* (non-Javadoc)
	 * @see com.dlw.devapps.service.TokenService#getToken(com.dlw.devapps.domain.OauthTokenRequest)
	 */
	@Override
	public OauthTokenResponse getToken(OauthTokenRequest request) {
		final String _M = "getToken(OauthTokenRequest):";
		log.info("{} started. request = {}", _M, request);
		
		String newToken = UUID.randomUUID().toString();

		OauthTokenResponse response = OauthTokenResponse.builder()
				.accessToken(newToken)
				.tokenType(props.getTokenType())
				.expiresIn(props.getTokenTtl().toSeconds())
				.build();

		LocalDateTime now = LocalDateTime.now();
		tokenStore.put(newToken, TokenInfo.builder()
				.generated(now)
				.expiration(now.plus(props.getTokenTtl()))
				.build());

		log.info("{} Token store contains {} tokens", _M, tokenStore.size());
		log.info("{} ended. response = {}", _M, response);
		return(response);
	}

	/* (non-Javadoc)
	 * @see com.dlw.devapps.service.TokenService#validateToken(java.lang.String)
	 */
	@Override
	public void validateToken(String token) throws InvalidTokenException {
		final String _M = "validateToken(String):";
		log.info("{} started. token = {}", _M, token);

		TokenInfo tokenInfo = tokenStore.get(token);
		if (tokenInfo == null || tokenInfo.getExpiration().isBefore(LocalDateTime.now())) {
			throw new InvalidTokenException(token);
		}
	}

	@Data
	@Builder
	private static class TokenInfo {
		private LocalDateTime generated;
		private LocalDateTime expiration;
	}
}
