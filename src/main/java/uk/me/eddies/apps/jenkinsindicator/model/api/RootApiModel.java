/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;

/**
 * Represents the "root" of the API-model which is referenced from other areas of the API model.
 */
interface RootApiModel extends ActionConnector {

	public void notifyAdded(ApiJob job);

	public void notifyRemoved(ApiJob job);

	public JenkinsServer getModel();
}