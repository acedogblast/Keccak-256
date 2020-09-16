package com.acedogblast.crypto_algos;

public class Utils {
    public static boolean checkForPrime(int input) {
        boolean isItPrime = true;
        if(input <= 1)
        {
            isItPrime = false;

            return isItPrime;
        }
        else
        {
            for (int i = 2; i<= input/2; i++)
            {
                if ((input % i) == 0)
                {
                    isItPrime = false;
                    break;
                }
            }
            return isItPrime;
        }
    }
    public static boolean checkForPrime(long input) {
        boolean isItPrime = true;
        if(input <= 1)
        {
            isItPrime = false;

            return isItPrime;
        }
        else
        {
            for (long i = 2; i<= input/2; i++)
            {
                if ((input % i) == 0)
                {
                    isItPrime = false;
                    break;
                }
            }
            return isItPrime;
        }
    }
    public static byte[] concatBytes(byte[] in1, byte[] in2) {
        byte[] output = new byte[in1.length + in2.length];
        for (int i = 0; i < in1.length; i++) {
            output[i] = in1[i];
        }
        for (int i = 0; i < in2.length; i++) {
            output[i + in1.length] = in2[i];
        }
        return output;
    }
}
