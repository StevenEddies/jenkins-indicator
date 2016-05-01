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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Proof-of-concept integration test to demonstrate retrieval of data from a Jenkins
 * API over HTTPS.
 */
public class HttpsRequestProofOfConceptIT {

	private static final URI HTTPS_API = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/api/xml");
	private static final URI HTTP_API = URI.create("http://jenkins.eddies.me.uk/job/jenkins-indicator/api/xml");
	private static final int STATUS_OK = 200;
	private static final String XML_CONTENT_TYPE = "application/xml";
	
	private CloseableHttpClient client;
	
	@Before
	public void setUp() {
		client = HttpClients.createDefault();
	}
	
	@Test
	public void shouldRequestHttpsApi() throws ClientProtocolException, IOException {
		try (CloseableHttpResponse response = client.execute(new HttpGet(HTTPS_API))) {
			assertThat(response.getStatusLine().getStatusCode(), is(STATUS_OK));
			assertContainsXmlContent(response.getEntity());
		}
	}
	
	@Test
	public void shouldResolveRedirects() throws ClientProtocolException, IOException {
		try (CloseableHttpResponse response = client.execute(new HttpGet(HTTP_API))) {
			assertThat(response.getStatusLine().getStatusCode(), is(STATUS_OK));
			assertContainsXmlContent(response.getEntity());
		}
	}
	
	@After
	public void cleanUp() throws IOException {
		client.close();
	}
	
	private void assertContainsXmlContent(HttpEntity entity) throws IOException {
		assertThat(entity.getContentType().getValue(), containsString(XML_CONTENT_TYPE));
		try (ByteArrayOutputStream content = new ByteArrayOutputStream()) {
			entity.writeTo(content);
			assertThat(content.toByteArray().length, greaterThan(0));
		}
	}
}
