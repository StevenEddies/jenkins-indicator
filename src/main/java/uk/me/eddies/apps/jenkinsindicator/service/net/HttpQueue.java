/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.net;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

/**
 * Asynchronous executor for HTTP requests.
 */
public class HttpQueue {
	
	private CloseableHttpAsyncClient client;
	private BlockingQueue<HttpResponse> responseQueue;
	private HttpQueueResponseHandler responseHandler;
	
	public HttpQueue(CloseableHttpAsyncClient client) {
		this(client, new LinkedBlockingQueue<>(), null);
	}
	
	HttpQueue(CloseableHttpAsyncClient client, BlockingQueue<HttpResponse> responseQueue, HttpQueueResponseHandler responseHandler) {
		this.client = client;
		this.responseQueue = responseQueue;
		this.responseHandler = (responseHandler == null) ? new HttpQueueResponseHandler(this) : responseHandler;
	}

	public void start() {
		client.start();
	}
	
	public void stop() throws IOException {
		client.close();
	}
	
	public void putRequest(HttpUriRequest request) {
		client.execute(request, responseHandler);
	}
	
	void putResponse(HttpResponse response) throws InterruptedException {
		responseQueue.put(response);
	}
	
	public HttpResponse takeResponse() throws InterruptedException {
		return responseQueue.take();
	}
}
