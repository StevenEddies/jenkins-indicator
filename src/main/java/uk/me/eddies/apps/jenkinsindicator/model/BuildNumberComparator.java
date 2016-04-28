/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import java.util.Comparator;

/**
 * {@link Comparator} to compare {@link Long} build numbers.
 * 
 * <p>A build number is a positive number or null, with null being considered to be
 * before all numeric build numbers.
 */
public class BuildNumberComparator implements Comparator<Long> {
	
	private static final long MIN_BUILD_NUMBER = 1L;
	private static final long NO_BUILD_NUMBER = MIN_BUILD_NUMBER - 1L;
	
	@Override
	public int compare(Long number1, Long number2) {
		if (((number1 != null) && (number1 < MIN_BUILD_NUMBER))
				|| ((number2 != null) && (number2 < MIN_BUILD_NUMBER)))
			throw new IllegalArgumentException("Build numbers must be less than " + MIN_BUILD_NUMBER);
		
		long numericEquivalent1 = (number1 == null) ? NO_BUILD_NUMBER : number1;
		long numericEquivalent2 = (number2 == null) ? NO_BUILD_NUMBER : number2;
		return Long.compare(numericEquivalent1, numericEquivalent2);
	}
}
