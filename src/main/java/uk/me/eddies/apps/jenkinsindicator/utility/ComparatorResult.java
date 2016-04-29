/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntPredicate;

/**
 * {@link Enum} to convert the result from a {@link Comparator} into a readable format.
 */
public enum ComparatorResult {
	LESS_THAN(i -> (i < 0)),
	EQUALS(i -> (i == 0)),
	GREATER_THAN(i -> (i > 0));
	
	private IntPredicate test;
	
	private ComparatorResult(IntPredicate test) {
		this.test = test;
	}
	
	private boolean matches(int result) {
		return test.test(result);
	}
	
	private static ComparatorResult getForResult(int result) {
		return Arrays.stream(values())
				.filter(potentialResult -> potentialResult.matches(result))
				.findAny().get();
	}
	
	public static <T> ComparatorResult compare(T first, T second, Comparator<T> comparator) {
		return getForResult(comparator.compare(first, second));
	}
	
	public static <T extends Comparable<? super T>> ComparatorResult naturalCompare(T first, T second) {
		return compare(first, second, Comparator.naturalOrder());
	}
}
