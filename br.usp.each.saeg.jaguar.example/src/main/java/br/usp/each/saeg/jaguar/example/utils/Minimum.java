package br.usp.each.saeg.jaguar.example.utils;

public class Minimum {
	
	public static int minimum(int[] array)
	{
		int i = 0;
		int min = array[++i]; //array[i++];
		while(i < array.length)
		{
			if(array[i] < min)
				min = array[i];
			i++;
		}
		return min;
	}
	
}
