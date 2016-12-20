/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents an XML element which can contain {@link ChildJobElement}s.
 */
public abstract class JobContainer {

	@XmlElement(name = "job")
	private Collection<ChildJobElement> jobs;

	public Collection<ChildJobElement> getJobs() {
		if (jobs == null) jobs = emptySet();
		return Collections.unmodifiableCollection(jobs);
	}
}
