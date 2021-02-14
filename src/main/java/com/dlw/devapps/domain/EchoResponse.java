package com.dlw.devapps.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author Dmitry Lekhtuz
 *
 */
@Data
@Builder
public class EchoResponse {
	private boolean success;
	private List<String> lines;
}
