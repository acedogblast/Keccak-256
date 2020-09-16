package com.acedogblast;

import com.acedogblast.crypto_algos.Ethash;
import com.acedogblast.crypto_algos.Keccak;
import com.acedogblast.crypto_algos.Utils;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;

public class Main {

    public static void main(String[] args) {
        test();
        //test2();
    }
    private static void test()
    {
        //StratumPoolConectionInfo spci = new StratumPoolConectionInfo("us1.ethermine.org", 4444, "0x0496E7a0d8fe56ad5c497B640eCDfdF1e9eEEA80", "");
        //connectToPool(spci);

        //String test = "testing";
        //byte[] inputBytes = stringToBytesASCII(test);
        byte[] inputBytes = new byte[0];
        Keccak k = new Keccak();
        byte[] outputHash = k.hash256(inputBytes);
        String outputString = bytesToHex(outputHash);
        System.out.println("My Hash: " + outputString);
        System.out.println("Correct: " + "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470".toUpperCase());

    }
    private static void connectToPool(StratumPoolConectionInfo spci) {
        try{
            Socket socket = new Socket(spci.getHostname(), spci.getHostport());

            OutputStream outputStream = socket.getOutputStream(); // Raw byte streams
            InputStream inputStream = socket.getInputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream); // Cannot use builtin writeUTF methods due to incorrect formatting.
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String subscribe_message_noSpace = "{\"id\":1,\"method\":\"mining.subscribe\",\"params\":[\"MinerName/1.0.0\",\"EthereumStratum/1.0.0\"]}";

            PrintWriter printWriter = new PrintWriter(dataOutputStream);
            BufferedReader in = new BufferedReader(new InputStreamReader(dataInputStream));

            printWriter.println(subscribe_message_noSpace);
            System.out.println("SELF:" + subscribe_message_noSpace);
            printWriter.flush();

            String authorize_message = "{\"id\":2,\"method\":\"mining.authorize\",\"params\":[\"" + spci.getUsername() + "\"," + spci.getPassword() + "\"\"]}";

            printWriter.println(authorize_message);
            System.out.println("SELF:" + authorize_message);
            printWriter.flush();

            while (true) {
                System.out.println("POOL:" + in.readLine());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static byte[] stringToBytesASCII(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] flipBytes(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[input.length - i - 1];
        }
        return output;
    }

    private static void test2() {  // 0b1010000101000101101000010100010110100001010001011010000101000101L
        Keccak k = new Keccak();
        Keccak.printBlock(Keccak.encodeLELong(0x8000000000000000l));

    }
    private static long bytesToLane(byte[] input) {
        long laneValue = 0L;
        for (int laneByteIndex = 7; laneByteIndex >= 0; --laneByteIndex) {
            laneValue <<= Byte.SIZE;
            laneValue += Byte.toUnsignedInt(input[laneByteIndex]);
        }
        return laneValue;
    }

    private static void test3(){
        //Create state
        long[][] A = new long[5][5];
        Keccak k = new Keccak();
        // Block
        long[] block = new long[17];
        block[0]  = 0b1000000000000000000000000000000000000000000000000000000000000000L;
        block[16] = 0b0000000000000000000000000000000000000000000000000000000000000001L;

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if (x + 5 * y < 17) {
                    A[x][y] = A[x][y] ^ block[x + 5 * y];
                    A = k.keccakF1600(A);
                }
            }
        }

        long[] output = new long[4]; // 256-bit

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if (x + 5 * y < 17) {

                    A = k.keccakF1600(A);
                }
            }
        }



    }


}
