/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import java.net.URI;
import java.util.Map;

/**
 * Provides an interface for {@link Action}s to perform themselves with.
 */
public interface ActionConnector {

	public void updateRootJobs(Map<URI, String> newChildren);

	public ApiJob getJob(URI jobUri);

	public ApiTrigger getApiTrigger();
}