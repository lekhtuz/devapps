package com.dlw.devapps.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Data
@Builder
public class ResourceResponse {
	private boolean success;
}
