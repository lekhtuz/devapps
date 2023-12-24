package com.dlw.devapps.service;

import com.dlw.devapps.domain.OauthTokenRequest;
import com.dlw.devapps.domain.OauthTokenResponse;
import com.dlw.devapps.domain.InvalidTokenException;

/**
 * @author Dmitry Lekhtuz
 *
 */
public interface TokenService {
	/**
	 * @param request
	 * @return
	 */
	public OauthTokenResponse getToken(final OauthTokenRequest request);

	/**
	 * @param token
	 * @return
	 */
	public void validateToken(final String token) throws InvalidTokenException;
}
