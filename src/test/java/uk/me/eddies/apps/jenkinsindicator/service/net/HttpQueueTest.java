/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.net;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class HttpQueueTest {

	@Mock private CloseableHttpAsyncClient client;
	@Mock private BlockingQueue<HttpResponse> queue;
	@Mock private HttpQueueResponseHandler handler;
	
	private HttpQueue systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new HttpQueue(client, queue, handler);
	}
	
	@Test
	public void shouldStartClient() {
		systemUnderTest.start();
		verify(client).start();
	}
	
	@Test
	public void shouldStopClient() throws IOException {
		systemUnderTest.stop();
		verify(client).close();
	}
	
	@Test
	public void shouldExecuteRequest() {
		HttpUriRequest request = mock(HttpUriRequest.class);
		systemUnderTest.putRequest(request);
		verify(client).execute(request, handler);
	}
	
	@Test
	public void shouldQueueResponse() throws InterruptedException {
		HttpResponse response = mock(HttpResponse.class);
		systemUnderTest.putResponse(response);
		verify(queue).put(response);
	}
	
	@Test(expected=InterruptedException.class)
	public void shouldQueueResponseThrowingException() throws InterruptedException {
		HttpResponse response = mock(HttpResponse.class);
		doThrow(InterruptedException.class).when(queue).put(response);
		systemUnderTest.putResponse(response);
	}
	
	@Test
	public void shouldTakeResponse() throws InterruptedException {
		HttpResponse response = mock(HttpResponse.class);
		when(queue.take()).thenReturn(response);
		assertThat(systemUnderTest.takeResponse(), sameInstance(response));
	}
	
	@Test(expected=InterruptedException.class)
	public void shouldTakeResponseThrowingException() throws InterruptedException {
		doThrow(InterruptedException.class).when(queue).take();
		systemUnderTest.takeResponse();
	}
}
