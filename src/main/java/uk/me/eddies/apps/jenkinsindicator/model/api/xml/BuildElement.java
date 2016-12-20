/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import java.time.DateTimeException;
import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import uk.me.eddies.apps.jenkinsindicator.model.BuildStatus;
import uk.me.eddies.apps.jenkinsindicator.model.api.ApiResolutionException;

/**
 * XML element present in the API results defining a Jenkins build as part of a
 * structure.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildElement {

	private String result;
	private Long number;
	private Long timestamp;
	
	public BuildStatus getResult() throws ApiResolutionException {
		try {
			return BuildStatus.valueOf(result.trim());
		} catch (IllegalArgumentException ex) {
			throw new ApiResolutionException("\"" + result + "\" is not a valid build status.", ex);
		} catch (NullPointerException ex) {
			throw new ApiResolutionException("Build has no status.", ex);
		}
	}
	
	public Long getNumber() {
		return number;
	}
	
	public Instant getTimestamp() throws ApiResolutionException {
		try {
			return Instant.ofEpochMilli(timestamp);
		} catch (DateTimeException ex) {
			throw new ApiResolutionException("\"" + timestamp + "\" is not a valid timestamp.", ex);
		} catch (NullPointerException ex) {
			throw new ApiResolutionException("Build has no timestamp.", ex);
		}
	}
}
