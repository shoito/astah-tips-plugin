package com.github.astah.tips;


public class APIException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public APIException(Exception e) {
		super(e);
	}

}
