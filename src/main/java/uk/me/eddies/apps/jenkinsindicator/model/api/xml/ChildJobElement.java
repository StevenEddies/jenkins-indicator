/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import uk.me.eddies.apps.jenkinsindicator.model.api.ApiResolutionException;

/**
 * XML element present in the API results defining a Jenkins job as part of a
 * structure.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChildJobElement {

	private String name;
	private String url;
	
	public String getName() {
		return name.trim();
	}
	
	public URI getUrl() throws ApiResolutionException {
		try {
			return URI.create(url.trim());
		} catch (IllegalArgumentException ex) {
			throw new ApiResolutionException("\"" + url + "\" is not a valid URI for job \"" + name + "\".", ex);
		} catch (NullPointerException ex) {
			throw new ApiResolutionException("Job \"" + name + "\" has no URI.", ex);
		}
	}
}
