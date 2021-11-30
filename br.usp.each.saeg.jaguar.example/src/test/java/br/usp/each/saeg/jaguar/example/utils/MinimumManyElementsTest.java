package br.usp.each.saeg.jaguar.example.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link Minimum}.
 */
public class MinimumManyElementsTest {
	
	@Test
	public void test1() {
		int expected = 2;
		int actual = Minimum.minimum(new int[] { 4, 3, 2 });
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test2() {
		int expected = 5;
		int actual = Minimum.minimum(new int[] { 5, 5, 6 });
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test3() {
		int expected = 1;
		int actual = Minimum.minimum(new int[] { 2, 1, 10 });
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void test4() {
		int expected = 1;
		int actual = Minimum.minimum(new int[] { 1, 2, 3 });
		Assert.assertEquals(expected, actual);
	}
	
}
