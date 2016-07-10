package org.klose.payment.util;

import java.util.Random;


public class ValidateCode {
	
	private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	
	public static String randomCode(int length){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<length; i++){
			sb.append(randomCode());
		}
		
		return sb.toString();
	}
	
	public static String randomCode(){

		Random random = new Random();
		
		return String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
	}
	
	
	public static void main(String[] args){
		
		System.out.println(ValidateCode.randomCode(10));
		
	}

}
