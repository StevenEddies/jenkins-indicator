/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import uk.me.eddies.apps.jenkinsindicator.model.BuildStatus;
import uk.me.eddies.apps.jenkinsindicator.model.api.ApiResolutionException;

/**
 * Tests to ensure that JAXB can suitably deserialise XML from the API.
 */
public class XmlApiDeserialisationTest {

	private XmlApiDeserialiser deserialiser;
	
	@Before
	public void setUp() throws JAXBException {
		deserialiser = new XmlApiDeserialiser();
	}
	
	@Test
	public void shouldDeserialiseFromValidServerApi() throws IOException, JAXBException, ApiResolutionException {
		ServerRootElement root = deserialiseFile("server_example.xml", ServerRootElement.class);
		assertThat(root.getJobs(), hasSize(6));
		ChildJobElement firstJob = root.getJobs().iterator().next();
		assertThat(firstJob.getName(), equalTo("cloud-storage"));
		assertThat(firstJob.getUrl(), equalTo(URI.create("https://jenkins.eddies.me.uk/job/cloud-storage/")));
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromServerApiWithMissingJobUrl() throws IOException, JAXBException, ApiResolutionException {
		ServerRootElement root = deserialiseFile("bad_server_example_no_url.xml", ServerRootElement.class);
		root.getJobs().iterator().next().getUrl();
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromServerApiWithIncorrectJobUrl() throws IOException, JAXBException, ApiResolutionException {
		ServerRootElement root = deserialiseFile("bad_server_example_bad_url.xml", ServerRootElement.class);
		root.getJobs().iterator().next().getUrl();
	}
	
	@Test
	public void shouldDeserialiseFromValidParentJobApi() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("parent_job_example.xml", JobRootElement.class);
		assertThat(root.getName(), equalTo("jenkins-indicator"));
		assertThat(root.getUrl(), equalTo(URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/")));
		assertThat(root.getBuild(), nullValue());
		assertThat(root.getJobs(), hasSize(2));
		ChildJobElement firstChildJob = root.getJobs().iterator().next();
		assertThat(firstChildJob.getName(), equalTo("dev0.1"));
		assertThat(firstChildJob.getUrl(), equalTo(URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/job/dev0.1/")));
	}
	
	@Test
	public void shouldDeserialiseFromValidChildJobApi() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("child_job_example.xml", JobRootElement.class);
		assertThat(root.getName(), equalTo("dev0.1"));
		assertThat(root.getUrl(), equalTo(URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/job/dev0.1/")));
		assertThat(root.getBuild().getNumber(), equalTo(19L));
		assertThat(root.getBuild().getResult(), equalTo(BuildStatus.SUCCESS));
		assertThat(root.getBuild().getTimestamp(), equalTo(Instant.ofEpochMilli(1480701040698L)));
		assertThat(root.getJobs(), empty());
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromChildJobApiWithMissingBuildResult() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("bad_child_job_example_no_result.xml", JobRootElement.class);
		root.getBuild().getResult();
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromChildJobApiWithInvalidBuildResult() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("bad_child_job_example_bad_result.xml", JobRootElement.class);
		root.getBuild().getResult();
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromChildJobApiWithMissingBuildTimestamp() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("bad_child_job_example_no_timestamp.xml", JobRootElement.class);
		root.getBuild().getTimestamp();
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromChildJobApiWithMissingUrl() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("bad_child_job_example_no_own_url.xml", JobRootElement.class);
		root.getUrl();
	}
	
	@Test(expected=ApiResolutionException.class)
	public void shouldFailToDeserialiseFromChildJobApiWithInvalidUrl() throws IOException, JAXBException, ApiResolutionException {
		JobRootElement root = deserialiseFile("bad_child_job_example_bad_own_url.xml", JobRootElement.class);
		root.getUrl();
	}

	private <T extends XmlResponse> T deserialiseFile(String fileLocation, Class<T> type)
			throws JAXBException, IOException {
		try (
			InputStream is = getClass().getResourceAsStream(fileLocation);
		) {
			XmlResponse deserialised = deserialiser.deserialise(is);
			assertThat(deserialised, instanceOf(type));
			return type.cast(deserialised);
		}
	}
}