package com.thomsonreuters.oa.filing.util;

public class RandomGenerator {
	public long randomGenerator(int noOfDigit){
		 return Math.round(Math.random() * Long.parseLong(String.format("%-"+noOfDigit+"s", "8").replace(' ', '9'))) + Long.parseLong(String.format("%-"+noOfDigit+"s", "1").replace(' ', '0'));
	}

	  public static void main(String args[]) throws Exception {
	   /* System.out.println(String.format("%10s", "howto").replace(' ', '*'));
	    System.out.println(String.format("%-10s", "howto").replace(' ', '*'));*/
	  }
}
