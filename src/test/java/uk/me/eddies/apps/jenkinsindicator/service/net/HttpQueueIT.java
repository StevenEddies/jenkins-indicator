/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.net;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test to prove overall operation of the {@link HttpQueue}.
 */
public class HttpQueueIT {

	private static final URI HTTPS_API = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/api/xml");
	private static final URI HTTP_API = URI.create("http://jenkins.eddies.me.uk/job/jenkins-indicator/api/xml");
	private static final int STATUS_OK = 200;
	private static final String XML_CONTENT_TYPE = "application/xml";
	
	private HttpQueue queue;
	
	@Before
	public void setUp() {
		queue = new HttpQueue(HttpAsyncClients.createDefault());
		queue.start();
	}
	
	@Test
	public void shouldRequestApi() throws IOException, InterruptedException {
		queue.putRequest(new HttpGet(HTTPS_API));
		HttpResponse response = queue.takeResponse();
		assertThat(response.getStatusLine().getStatusCode(), is(STATUS_OK));
		assertContainsXmlContent(response.getEntity());
	}
	
	@Test
	public void shouldResolveRedirects() throws IOException, InterruptedException {
		queue.putRequest(new HttpGet(HTTP_API));
		HttpResponse response = queue.takeResponse();
		assertThat(response.getStatusLine().getStatusCode(), is(STATUS_OK));
		assertContainsXmlContent(response.getEntity());
	}
	
	@After
	public void cleanUp() throws IOException {
		queue.stop();
	}
	
	private void assertContainsXmlContent(HttpEntity entity) throws IOException {
		assertThat(entity.getContentType().getValue(), containsString(XML_CONTENT_TYPE));
		try (ByteArrayOutputStream content = new ByteArrayOutputStream()) {
			entity.writeTo(content);
			assertThat(content.toByteArray().length, greaterThan(0));
		}
	}
}
