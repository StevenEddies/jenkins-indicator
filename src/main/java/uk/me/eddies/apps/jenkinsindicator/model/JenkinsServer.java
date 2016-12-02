/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static java.util.Collections.synchronizedSortedMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a server running Jenkins which may have multiple {@link Job}s.
 * 
 * <p>Accesses from different threads are supported. Returned collections reflect the
 * state at the time of the method call and are not subsequently updated.
 */
public class JenkinsServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(JenkinsServer.class);

	private final String serverName;
	private final SortedMap<String, Job> jobs;
	
	public JenkinsServer(String serverName) {
		this.serverName = requireNonNull(serverName);
		this.jobs = synchronizedSortedMap(new TreeMap<>());
		LOG.debug("New server '{}'.", serverName);
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public Collection<Job> getAllJobs() {
		synchronized (jobs) {
			return unmodifiableCollection(new LinkedHashSet<>(jobs.values()));
		}
	}
	
	public void updateForNewJobInformation(String jobName, Long buildNumber,
			Supplier<Job> jobCreator, Function<Job, Build> buildCreator) {
		requireNonNull(buildCreator);
		synchronized (jobs) {
			Job job = addOrGetJob(jobName, jobCreator);
			job.updateBuildIfLater(buildNumber, buildCreator);
		}
	}
	
	private Job addOrGetJob(String jobName, Supplier<Job> jobCreator) {
		if (jobs.containsKey(jobName)) {
			return jobs.get(jobName);
		} else {
			Job job = jobCreator.get();
			if (!job.getName().equals(jobName)) throw new IllegalStateException("Wrong Job supplied.");
			jobs.put(jobName, job);
			LOG.debug("New job '{}' on server '{}'.", jobName, serverName);
			return job;
		}
	}
	
	public void updateForDeletedJob(String jobName) {
		jobs.remove(jobName);
		LOG.debug("Job '{}' on server '{}' deleted.", jobName, serverName);
	}
}
