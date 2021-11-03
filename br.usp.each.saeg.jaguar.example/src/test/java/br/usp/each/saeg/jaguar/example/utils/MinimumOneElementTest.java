package br.usp.each.saeg.jaguar.example.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Minimum}.
 */
public class MinimumOneElementTest {

	@Test
	public void test5() {
		int expected = 4;
		int actual = Minimum.minimum(new int[] { 4 });
		Assert.assertEquals(expected, actual);
	}
	
}
