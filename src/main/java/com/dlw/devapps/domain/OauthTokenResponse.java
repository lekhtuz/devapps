package com.dlw.devapps.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Data
@Builder
@JsonInclude(Include.NON_EMPTY)
public class OauthTokenResponse {
	@JsonProperty("access_token") private String accessToken;
	@JsonProperty("token_type") private String tokenType;
	@JsonProperty("refresh_token") private String refreshToken;
	@JsonProperty("expires_in") private Long expiresIn;
}
