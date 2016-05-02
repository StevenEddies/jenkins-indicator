/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.net;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * Handler to receive {@link HttpResponse}s on behalf of a {@link HttpQueue}.
 */
class HttpQueueResponseHandler implements FutureCallback<HttpResponse> {
	
	private static final Logger LOG = Logger.getLogger(HttpQueueResponseHandler.class.getName());
	
	private HttpQueue queue;

	HttpQueueResponseHandler(HttpQueue queue) {
		this.queue = queue;
	}

	@Override
	public void completed(HttpResponse result) {
		try {
			queue.putResponse(result);
		} catch (InterruptedException ex) {
			LOG.log(Level.INFO, "Interrupted while attempting to handle web response.", ex);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void failed(Exception ex) {
		LOG.log(Level.WARNING, "Error executing web request.", ex);
	}

	@Override
	public void cancelled() {
		// No action necessary here
		// The user will know that the request has been cancelled
	}
}
