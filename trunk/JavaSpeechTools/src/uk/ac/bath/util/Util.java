package uk.ac.bath.util;

public class Util {
	

	public static double[] convertFloatsToDoubles(float[] input,double output[])
	{
	    if (input == null)
	    {
	        return null; // Or throw an exception - your choice
	    }
	    
	    if (output == null){
	    	output= new double[input.length];
	    } 
	    
	    for (int i = 0; i < input.length; i++)
	    {
	        output[i] = input[i];
	    }
	    return output;
	}
	
	

	public static float[] convertDoublesToFloats(double  []input,float output[])
	{
	    if (input == null)
	    {
	        return null; // Or throw an exception - your choice
	    }
	    
	    
	    if (output == null){
	    	output= new float[input.length];
	    } 
	    
	    for (int i = 0; i < input.length; i++)
	    {
	        output[i] = (float)input[i];
	    }
	    return output;
	}

}
