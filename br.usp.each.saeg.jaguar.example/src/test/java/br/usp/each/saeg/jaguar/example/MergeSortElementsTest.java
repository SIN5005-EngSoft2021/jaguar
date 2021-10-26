package br.usp.each.saeg.jaguar.example;

import org.junit.Assert;
import org.junit.Test;
import br.usp.each.saeg.jaguar.example.MergeSort;

public class MergeSortElementsTest {

    @Test
    public void test1() {
        int[] expected = new int[] {5, 6, 7, 11, 12, 13};
        int actual[] = {12, 11, 13, 5, 6, 7};
        MergeSort ob = new MergeSort();
        ob.sort(actual, 0, actual.length-1);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String expected = "5 6 7 11 12 13";
        int actual[] = {12, 11, 13, 5, 6, 7};
        MergeSort ob = new MergeSort();
        ob.sort(actual, 0, actual.length-1);
        Assert.assertEquals(expected, ob.printArray(actual));
    }

}
