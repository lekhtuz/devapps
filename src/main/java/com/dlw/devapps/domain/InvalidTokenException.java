package com.dlw.devapps.domain;

/**
 * @author Dmitry Lekhtuz
 *
 */
public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = -5237290826374807832L;

	/**
	 * @param message
	 */
	public InvalidTokenException(String token) {
		super(token);
	}
}
