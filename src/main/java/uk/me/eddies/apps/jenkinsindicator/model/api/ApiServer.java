/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;

/**
 * Wrapper for the {@link JenkinsServer} model to be acted upon by responses from the
 * Jenkins API.
 */
public class ApiServer implements RootApiModel {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiServer.class);

	private final JenkinsServer model;
	private final ApiTrigger apiTrigger;
	private final ApiJobContainer rootJobs;
	private final Map<URI, ApiJob> allJobs;
	
	public ApiServer(JenkinsServer model, ApiTrigger api) {
		this(model, api, m -> new ApiJobContainer(m, null));
	}
	
	ApiServer(JenkinsServer model, ApiTrigger api, Function<RootApiModel, ApiJobContainer> containerFactory) {
		this.model = requireNonNull(model);
		this.apiTrigger = requireNonNull(api);
		this.rootJobs = containerFactory.apply(this);
		this.allJobs = new HashMap<>();
	}
	
	@Override
	public void updateRootJobs(Map<URI, String> newJobs) {
		LOG.debug("Updating root jobs: {}.", newJobs.values());
		rootJobs.updateJobs(newJobs);
	}
	
	@Override
	public ApiJob getJob(URI jobUri) {
		return allJobs.get(requireNonNull(jobUri));
	}
	
	@Override
	public ApiTrigger getApiTrigger() {
		return apiTrigger;
	}
	
	@Override
	public void notifyAdded(ApiJob job) {
		allJobs.put(job.getUri(), job);
	}
	
	@Override
	public void notifyRemoved(ApiJob job) {
		allJobs.remove(job.getUri());
	}
	
	@Override
	public JenkinsServer getModel() {
		return model;
	}
}
