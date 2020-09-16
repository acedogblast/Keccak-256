package com.acedogblast.crypto_algos;

import com.acedogblast.Main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.acedogblast.crypto_algos.Utils.concatBytes;

public class Keccak {
    private int w = 64; // depth of State Array. Also number of bits in a lane (8 bytes)
    private int l = 6;

    private long[][] A; // State Array

    public static final long[] RC = { // Round Constants
            0x0000000000000001L,
            0x0000000000008082L,
            0x800000000000808AL,
            0x8000000080008000L,
            0x000000000000808BL,
            0x0000000080000001L,
            0x8000000080008081L,
            0x8000000000008009L,
            0x000000000000008AL,
            0x0000000000000088L,
            0x0000000080008009L,
            0x000000008000000AL,
            0x000000008000808BL,
            0x800000000000008BL,
            0x8000000000008089L,
            0x8000000000008003L,
            0x8000000000008002L,
            0x8000000000000080L,
            0x000000000000800AL,
            0x800000008000000AL,
            0x8000000080008081L,
            0x8000000000008080L,
            0x0000000080000001L,
            0x8000000080008008L
    };

    public byte[] hash256(byte[] input) {
        System.out.println(Main.bytesToHex(input));
        return keccak(input, 1088, 512);
    }

    public byte[] keccak(byte[] Mbytes, int r, int c) { // r should be 1088 and c should be 512 for Keccack-256
        // Padding
        byte[] paddedMessage = concatBytes(Mbytes, pad(r, Mbytes.length));
        System.out.println("Padded Message size: " + paddedMessage.length);

        // Initialize State
        A = new long[5][5];

        // Break up message into blocks r bits or 136 bytes
        byte[] block = new byte[136];
        int n = paddedMessage.length / block.length; // number of blocks
        System.out.println("Blocks: " + n);

        for (int i = 0; i < n; i++) { // For each block
            //set block values
            for (int j = 0; j < block.length; j++) {
                block[j] = paddedMessage[i * block.length + j];
            }
            printBlock(block);

            // Fill state
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    int index = x + 5 * y;
                    if (index < r/w) {
                        long value = decodeLELong(block, index * 8);
                        System.out.println(value + " index: " + index);
                        A[x][y] = A[x][y] ^ value;
                        A = keccakF1600(A);
                    }
                }
            }
        }
        byte[] output = new byte[0]; // Size should be 256 bits/Z
        int bytesFilled = 0;
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                int index = x+5*y;
                if (index < r/w) {
                    if (bytesFilled < 136) {
                        // Take out a long from state and concat it to output.
                        output = concatBytes(output, encodeLELong(A[x][y]));
                        bytesFilled += 8;
                        A = keccakF1600(A);
                    }
                }
                if (bytesFilled == 32) {
                    //System.out.println("Finished Hash");
                    return output;
                }
            }
        }

        return output;
    }

    public long[][] keccakF1600(long[][] A) { // KECCAK-f where b = 1600
        for (int i = 0; i < 24; i++) { // 24 Rounds
            A = Round1600(A, RC[i]);
        }
        return A;
    }

    private long[][] Round1600(long[][] A, long rc) {
        // θ Step
        long[] C = new long[5];
        for (int x = 0; x < 5; x++) {
            C[x] = A[x][0] ^ A[x][1]^ A[x][2] ^ A[x][3] ^ A[x][4];
        }

        long[] D = new long[5];
        for (int x = 0; x < 5; x++) {
            D[x] = C[(x + 4) % 5] ^ Long.rotateLeft(C[(x + 1) % 5], 1);
        }
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                A[x][y] = A[x][y] ^ D[x];
            }
        }

        // ρ and π steps
        long[][] B = new long[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                B[y][(2 * x + 3 * y) % 5] = Long.rotateLeft(A[x][y], rot_offset(x,y));
            }
        }

        // χ step
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                A[x][y] = B[x][y] ^ ((~B[(x+1) % 5][y]) & B[(x+2) % 5][y]);
            }
        }

        // ι step
        A[0][0] = A[0][0] ^ rc;

        return A;
    }


    private int rot_offset(int x, int y) {
        switch (x){
            case 0:
                switch (y) {
                    case 0:
                        return 0;
                    case 1:
                        return 36;
                    case 2:
                        return 3;
                    case 3:
                        return 41;
                    case 4:
                        return 18;
                }
            case 1:
                switch (y) {
                    case 0:
                        return 1;
                    case 1:
                        return 44;
                    case 2:
                        return 10;
                    case 3:
                        return 45;
                    case 4:
                        return 2;
                }
            case 2:
                switch (y) {
                    case 0:
                        return 62;
                    case 1:
                        return 6;
                    case 2:
                        return 43;
                    case 3:
                        return 15;
                    case 4:
                        return 61;
                }
            case 3:
                switch (y) {
                    case 0:
                        return 28;
                    case 1:
                        return 55;
                    case 2:
                        return 25;
                    case 3:
                        return 21;
                    case 4:
                        return 56;
                }
            case 4:
                switch (y) {
                    case 0:
                        return 27;
                    case 1:
                        return 20;
                    case 2:
                        return 39;
                    case 3:
                        return 8;
                    case 4:
                        return 14;
                }
        }
        System.out.println("Should not Happen!");
        return -1; // Should not happen!
    }

    private byte[] pad(int x, int m) { // x/size should be 1088 or 136 bytes
        byte[] p = null;
        int q = 136 - (m % 136); // number of padding bytes
        //System.out.println("Pad q: " + q + "  m: " + m);
        if(q == 136) { // Whole block is padding
            p = new byte[136];
            p[7] = (byte) 0x01;
            p[135 - 7] = (byte) 0x80;
        }
        else {
            p = new byte[q];
            p[0] = 1;
        }
        return p;
    }
    public static void printBlock(byte[] block) {
        System.out.println("Print Block size: " + block.length);
        for (int i = 0; i < block.length; i++) {
            System.out.print(block[i]);
            if (i + 1 != block.length) {
                System.out.print(",");
            }

            if (i % 8 == 7) {
                System.out.print("\n");
            }
        }
        System.out.print("\n");
    }
    /**
     * Decode a 64-bit little-endian word from the array {@code buf}
     * at offset {@code off}.
     *
     * @param buf   the source buffer
     * @param off   the source offset
     * @return  the decoded value
     */
    public static long decodeLELong(byte[] buf, int off)
    {
        return (buf[off + 0] & 0xFFL)
                | ((buf[off + 1] & 0xFFL) << 8)
                | ((buf[off + 2] & 0xFFL) << 16)
                | ((buf[off + 3] & 0xFFL) << 24)
                | ((buf[off + 4] & 0xFFL) << 32)
                | ((buf[off + 5] & 0xFFL) << 40)
                | ((buf[off + 6] & 0xFFL) << 48)
                | ((buf[off + 7] & 0xFFL) << 56);
    }

    /**
     * Encode the 64-bit word {@code val} into the array
     * {@code buf} at offset {@code off}, in little-endian
     * convention (least significant byte first).
     *
     * @param val   the value to encode
     */
    public static byte[] encodeLELong(long val)
    {
        byte[] buf = new byte[8];
        buf[0] = (byte)val;
        buf[1] = (byte)(val >>> 8);
        buf[2] = (byte)(val >>> 16);
        buf[3] = (byte)(val >>> 24);
        buf[4] = (byte)(val >>> 32);
        buf[5] = (byte)(val >>> 40);
        buf[6] = (byte)(val >>> 48);
        buf[7] = (byte)(val >>> 56);
        return buf;
    }

}
