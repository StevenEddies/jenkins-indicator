/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uk.me.eddies.apps.jenkinsindicator.model.api.Action;
import uk.me.eddies.apps.jenkinsindicator.model.api.ApiResolutionException;

/**
 * {@link Action} from calling the API of a Jenkins job page.
 */
@XmlRootElement(name="jobRoot")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobRootElement extends JobContainer implements XmlResponse {
	
	public static final String QUERY_PARAMS =
			"tree=name,url,lastCompletedBuild[result,number,timestamp],jobs[name,url]&wrapper=jobRoot&xpath=/*/*";
	
	private String name;
	private String url;
	@XmlElement(name="lastCompletedBuild")
	private BuildElement build;
	
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
	
	public BuildElement getBuild() {
		return build;
	}

	@Override
	public Action constructAction(URI currentUri) {
		return null; // TODO
	}
}
