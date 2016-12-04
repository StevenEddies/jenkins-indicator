/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.eddies.apps.jenkinsindicator.model.ActualBuild;
import uk.me.eddies.apps.jenkinsindicator.model.BuildStatus;
import uk.me.eddies.apps.jenkinsindicator.model.Job;
import uk.me.eddies.apps.jenkinsindicator.model.NoBuilds;

/**
 * Represents a job node received through the API.
 */
public class ApiJob {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiJob.class);

	private final RootApiModel apiModel;
	private final URI jobUri;
	private final String fullName;
	private final ApiJobContainer children;
	
	ApiJob(RootApiModel apiModel, URI jobUri, String fullName) {
		this(apiModel, jobUri, fullName, new ApiJobContainer(apiModel, fullName));
	}
	
	ApiJob(RootApiModel apiModel, URI jobUri, String fullName, ApiJobContainer jobContainer) {
		this.apiModel = apiModel;
		this.jobUri = jobUri;
		this.fullName = fullName;
		this.children = jobContainer;
		apiModel.notifyAdded(this);
	}
	
	public void updateLastBuild(long number, Instant startTime, BuildStatus status) {
		requireNonNull(startTime);
		requireNonNull(status);
		LOG.debug("Updating job '{}', build {}.", fullName, number);
		apiModel.getModel().updateForNewJobInformation(
				fullName,
				number,
				() -> new Job(fullName),
				j -> new ActualBuild(j, number, startTime, status));
	}
	
	public void updateNotBuilt() {
		LOG.debug("Updating job '{}', no builds.", fullName);
		apiModel.getModel().updateForNewJobInformation(
				fullName,
				null,
				() -> new Job(fullName),
				NoBuilds::new);
	}
	
	public void updateChildren(Map<URI, String> newChildren) {
		LOG.debug("Updating job '{}' children: {}.", fullName, newChildren.values());
		children.updateJobs(newChildren);
	}
	
	void destroy() {
		children.destroyAll();
		apiModel.getModel().updateForDeletedJob(fullName);
		apiModel.notifyRemoved(this);
	}
	
	URI getUri() {
		return jobUri;
	}
	
	String getFullName() {
		return fullName;
	}
}
