/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

/**
 * Represents an exception thrown while converting data received through the API
 * into our representation.
 */
public class ApiResolutionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ApiResolutionException() {
		super();
	}

	public ApiResolutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiResolutionException(String message) {
		super(message);
	}

	public ApiResolutionException(Throwable cause) {
		super(cause);
	}
}
