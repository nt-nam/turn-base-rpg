package emu.java.security;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

public class MessageDigest {

    private static final String SHA_256 = "SHA-256";

    private static final int[] ROUND_CONSTANTS = {
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private static final int[] INITIAL_STATE = {
        0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    private final String algorithm;
    private ByteArrayOutputStream pending = new ByteArrayOutputStream();

    protected MessageDigest(String algorithm) {
        this.algorithm = algorithm;
    }

    public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (!SHA_256.equalsIgnoreCase(algorithm)) {
            throw new NoSuchAlgorithmException(algorithm + " MessageDigest not available");
        }
        return new MessageDigest(SHA_256);
    }

    public final String getAlgorithm() {
        return algorithm;
    }

    public final int getDigestLength() {
        return 32;
    }

    public void update(byte input) {
        pending.write(input);
    }

    public void update(byte[] input) {
        pending.write(input, 0, input.length);
    }

    public void update(byte[] input, int offset, int length) {
        pending.write(input, offset, length);
    }

    public void reset() {
        pending = new ByteArrayOutputStream();
    }

    public byte[] digest(byte[] input) {
        update(input);
        return digest();
    }

    public byte[] digest() {
        byte[] message = pending.toByteArray();
        reset();
        return sha256(message);
    }

    private static byte[] sha256(byte[] message) {
        int paddedLength = ((message.length + 9 + 63) / 64) * 64;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        long bitLength = (long) message.length * 8;
        for (int index = 0; index < 8; index++) {
            padded[paddedLength - 1 - index] = (byte) (bitLength >>> (8 * index));
        }
        int[] state = INITIAL_STATE.clone();
        int[] schedule = new int[64];
        for (int block = 0; block < paddedLength; block += 64) {
            for (int word = 0; word < 16; word++) {
                int offset = block + word * 4;
                schedule[word] = ((padded[offset] & 0xff) << 24) | ((padded[offset + 1] & 0xff) << 16) | ((padded[offset + 2] & 0xff) << 8) | (padded[offset + 3] & 0xff);
            }
            for (int word = 16; word < 64; word++) {
                int previous = schedule[word - 2];
                int earlier = schedule[word - 15];
                int sigmaOne = Integer.rotateRight(previous, 17) ^ Integer.rotateRight(previous, 19) ^ (previous >>> 10);
                int sigmaZero = Integer.rotateRight(earlier, 7) ^ Integer.rotateRight(earlier, 18) ^ (earlier >>> 3);
                schedule[word] = sigmaOne + schedule[word - 7] + sigmaZero + schedule[word - 16];
            }
            int a = state[0];
            int b = state[1];
            int c = state[2];
            int d = state[3];
            int e = state[4];
            int f = state[5];
            int g = state[6];
            int h = state[7];
            for (int round = 0; round < 64; round++) {
                int sumOne = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
                int choice = (e & f) ^ (~e & g);
                int first = h + sumOne + choice + ROUND_CONSTANTS[round] + schedule[round];
                int sumZero = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
                int majority = (a & b) ^ (a & c) ^ (b & c);
                int second = sumZero + majority;
                h = g;
                g = f;
                f = e;
                e = d + first;
                d = c;
                c = b;
                b = a;
                a = first + second;
            }
            state[0] += a;
            state[1] += b;
            state[2] += c;
            state[3] += d;
            state[4] += e;
            state[5] += f;
            state[6] += g;
            state[7] += h;
        }
        byte[] digest = new byte[32];
        for (int word = 0; word < 8; word++) {
            digest[word * 4] = (byte) (state[word] >>> 24);
            digest[word * 4 + 1] = (byte) (state[word] >>> 16);
            digest[word * 4 + 2] = (byte) (state[word] >>> 8);
            digest[word * 4 + 3] = (byte) state[word];
        }
        return digest;
    }
}
