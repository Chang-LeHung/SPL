package org.spl.exceptions;

public class SPLException extends Exception {

	public SPLException(String message) {
		super(message);
	}

	public static String buildErrorMessage(
			String filename,
			int lineNo,
			String code,
			String errorMessage) {
		return "File, " +
				filename +
				", line " +
				lineNo +
				'\n' +
				code +
				"SyntaxError: " +
				errorMessage;
	}
}
