package com.banking.util;

import java.util.Random;
 

public class AccountNumberGenerator {
	private static final Random RANDOM = new Random();
	 
    private AccountNumberGenerator() {}
 
    /**
     * Generates a random 12-digit account number prefixed with "ACC".
     * Format: ACC-XXXXXXXXXXXX
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder("ACC");
        for (int i = 0; i < 12; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

}
