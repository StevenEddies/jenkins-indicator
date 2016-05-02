/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.net;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class HttpQueueResponseHandlerTest {
	
	@Mock private HttpQueue queue;
	@Mock private HttpResponse response;

	private HttpQueueResponseHandler systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new HttpQueueResponseHandler(queue);
	}
	
	@Test
	public void shouldAddReceivedResponsesToQueue() throws InterruptedException {
		systemUnderTest.completed(response);
		verify(queue).putResponse(response);
	}
	
	@Test
	public void shouldReInterruptThreadIfInterruptedWhileQueueingResponse() throws InterruptedException {
		doThrow(InterruptedException.class).when(queue).putResponse(response);
		systemUnderTest.completed(response);
		assertThat(Thread.interrupted(), is(true));
	}
}
