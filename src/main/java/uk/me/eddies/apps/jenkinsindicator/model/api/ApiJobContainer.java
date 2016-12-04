/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static java.util.stream.Collectors.toSet;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a container of {@link ApiJob}s.
 */
class ApiJobContainer {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApiJobContainer.class);

	private final RootApiModel apiModel;
	private final String nameBase;
	private final Map<URI, ApiJob> jobs;
	
	ApiJobContainer(RootApiModel apiModel, String parentName) {
		this.apiModel = apiModel;
		this.nameBase = (parentName == null) ? "" : parentName + " » ";
		this.jobs = new HashMap<>();
	}
	
	void updateJobs(Map<URI, String> newJobs) {
		Collection<URI> removed = jobs.keySet().stream()
				.filter(j-> !newJobs.containsKey(j))
				.collect(toSet());
		destroyJobs(removed);
		
		Collection<URI> added = newJobs.keySet().stream()
				.filter(j-> !jobs.containsKey(j))
				.collect(toSet());
		createJobs(added, newJobs::get);
		
		triggerJobRefresh(jobs.keySet());
	}
	
	void destroyAll() {
		destroyJobs(new HashSet<>(jobs.keySet()));
	}
	
	private void createJobs(Collection<URI> toCreate, Function<URI, String> nameFunction) {
		toCreate.forEach(u -> {
			String name = nameBase + nameFunction.apply(u);
			LOG.debug("New job '{}' identified.", name);
			jobs.put(u, new ApiJob(apiModel, u, name));
		});
	}
	
	private void triggerJobRefresh(Collection<URI> toRefresh) {
		toRefresh.forEach(apiModel.getApiTrigger()::triggerCall);
	}

	private void destroyJobs(Collection<URI> toDestroy) {
		toDestroy.stream()
				.map(jobs::get)
				.forEach(j -> {
					LOG.debug("Destroyed job '{}' identified.", j.getFullName());
					j.destroy();
				});
		jobs.keySet().removeAll(toDestroy);
	}
}
