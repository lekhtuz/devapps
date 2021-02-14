package com.dlw.devapps.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Data
@Builder
public class OauthTokenRequest {
	private String scope;
	private String grantType;
	private String clientId;
	private String clientSecret;
}
