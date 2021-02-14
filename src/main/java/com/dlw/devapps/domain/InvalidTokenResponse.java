package com.dlw.devapps.domain;

import lombok.Getter;

/**
 * @author Dmitry Lekhtuz
 *
 */
public enum InvalidTokenResponse {
	invalid_token(ErrorType.invalid_token, "Invalid token"),
	invalid_client(ErrorType.invalid_client, "Invalid client"),
	invalid_grant(ErrorType.invalid_grant, "Invalid grant"),
	invalid_scope(ErrorType.invalid_scope, "Invalid scope"),
	unauthorized_client(ErrorType.unauthorized_client, "Unauthorized client"),
	unsupported_grant_type(ErrorType.unsupported_grant_type, "Unsupported grant type"),
	;
	
	@Getter private ErrorType error;
	@Getter private String errorDescription;
	@Getter private String errorUri;	// Field not used yet

	/**
	 * @param errorType
	 * @param errorDescription
	 */
	private InvalidTokenResponse(ErrorType errorType, String errorDescription) {
		this.error = errorType;
		this.errorDescription = errorDescription;
	}

	/**
	 * @author Dmitry Lekhtuz
	 * 
	 * See https://www.oauth.com/oauth2-servers/making-authenticated-requests/refreshing-an-access-token
	 *
	 */
	public static enum ErrorType {
		invalid_token, invalid_client, invalid_grant, invalid_scope, unauthorized_client, unsupported_grant_type
	}
}
