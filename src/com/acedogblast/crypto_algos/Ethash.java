package com.acedogblast.crypto_algos;

public class Ethash {
    private static final int WORD_BYTES = 4;
    private static final int DATASET_BYTES_INIT = 1073741824; // 2^30
    private static final int DATESET_BYTES_GROWTH = 8388608;  // 2^23
    private static final int CACHE_BYTES_INIT = 16777216;     // 2^24
    private static final int CACHE_BYTES_GROWTH = 131072;     // 2^17
    private static final int CACHE_MULTIPLIER = 1024;
    private static final int EPOCH_LENGTH = 30000;
    private static final int MIX_BYTES = 128;
    private static final int HASH_BYTES = 64;
    private static final int DATASET_PARENTS = 256;
    private static final int CACHE_ROUNDS = 3;
    private static final int ACCESSES = 64;


    public static int get_cache_size(int block_number) {
        int size = 0;
        size = CACHE_BYTES_INIT + CACHE_BYTES_GROWTH * Math.floorDiv(block_number, EPOCH_LENGTH);
        size -= HASH_BYTES;

        while (!Utils.checkForPrime(size / HASH_BYTES)) {
            size -= 2 * HASH_BYTES;
        }
        return size;
    }

    public static long get_full_size(int block_number) {
        long size = 0;
        size = DATASET_BYTES_INIT + DATESET_BYTES_GROWTH * Math.floorDiv(block_number, EPOCH_LENGTH);
        size -= MIX_BYTES;

        while (!Utils.checkForPrime((size / MIX_BYTES))) {
            size -= 2 * MIX_BYTES;
        }
        return size;
    }

    //public static byte[] mkcache(int cache_size, byte[] seed) {

    //}



}
