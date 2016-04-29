/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.utility;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

/**
 * Superclass {@link Matcher} to perform type checking.
 */
public abstract class TypedMatcher<T> extends BaseMatcher<T> {
	
	private final Class<T> type;
	
	protected TypedMatcher(Class<T> type) {
		this.type = type;
	}
	
	@Override
	public boolean matches(Object item) {
		return (item != null)
				&& type.isInstance(item)
				&& internalMatches(type.cast(item));
	}
	
	protected abstract boolean internalMatches(T item);
}
