import java.util.Arrays;
import java.lang.Math;

/**
 * Salsa20 class contains methods allowing encryption and decryption of data in Salsa20 stream cipher.
 *
 */
public class Salsa20 {

    /**
     * The QuarterRound Function is used to convert four input values using XOR and rotation operations
     * @param x0, x1, x2, x3 - 4 input words
     * @return table containing 4 words
     */
    public int[] quarterRound(int x0, int x1, int x2, int x3) {
        int[] x = {x0, x1, x2, x3};
        x[1] = x[1] ^ (Integer.rotateLeft( (x[0] + x[3]), 7) );
        x[2] = x[2] ^ (Integer.rotateLeft( (x[1] + x[0]), 9) );
        x[3] = x[3] ^ (Integer.rotateLeft( (x[2] + x[1]), 13) );
        x[0] = x[0] ^ (Integer.rotateLeft( (x[3] + x[2]), 18) );
        return x;
    }

    /**
     * The RowRound Function is used to convert the input table containing 16 words using the QuarterRound Function.
     * Each use of QuarterRound function concern each row of 4x4 matrix that represents the input table.
     * @param x table of 16 words
     * @return 16-element table
     */
    public int[] rowRound(int[] x) {
        if (x.length != 16) {throw new IllegalArgumentException("rowRound");}

        int[] result = new int[16];
        // 1st four values
        int[] temp = quarterRound(x[0], x[1], x[2], x[3]);
        for (int i = 0; i < 4; i++) {
            result[i] = temp[i];
        }

        // 2nd four values
        temp = quarterRound(x[5], x[6], x[7], x[4]);
        result[5] = temp[0];
        result[6] = temp[1];
        result[7] = temp[2];
        result[4] = temp[3];

        // 3rd four values
        temp = quarterRound(x[10], x[11], x[8], x[9]);
        result[10] = temp[0];
        result[11] = temp[1];
        result[8] = temp[2];
        result[9] = temp[3];

        // 4th four values
        temp = quarterRound(x[15], x[12], x[13], x[14]);
        result[15] = temp[0];
        result[12] = temp[1];
        result[13] = temp[2];
        result[14] =temp[3];
        return result;
    }
    /**
     * The ColumnRound Function is used to convert the input table containing 16 words using the QuarterRound Function
     * Each use of QuarterRound function concern each column of 4x4 matrix that represents the input table.
     * @param x the 16-word input table
     * @return the 16-word output table
     */
    public int[] columnRound(int[] x) {
        if (x.length != 16) { throw new IllegalArgumentException("ColumnRound"); }

        int[] result = new int[16];
        int[] temp = new int[4];

        // 1st four values
        temp = quarterRound(x[0], x[4], x[8], x[12]);
        for (int i = 0; i < 4; i++) {
            result[(4 * i) % 16] = temp[i];
        }

        // 2nd four values
        temp = quarterRound(x[5], x[9], x[13], x[1]);
        for (int i = 0; i < 4; i++) {
            result[((4 * i) + 5) % 16] = temp[i];
        }

        // 3rd four values
        temp = quarterRound(x[10], x[14], x[2], x[6]);
        for (int i = 0; i < 4; i++) {
            result[((4 * i) + 10) % 16] = temp[i];
        }

        // 4th four values
        temp = quarterRound(x[15], x[3], x[7], x[11]);
        for (int i = 0; i < 4; i++) {
            result[((4 * i) + 15) % 16] = temp[i];
        }

        return result;
    }


    public int[] doubleRound(int[] x) {
        if (x.length != 16) { throw new IllegalArgumentException("doubleRound"); }
        int[] result = columnRound(x);
        result = rowRound(result);

        return result;
    }
    /**
     * The DoubleRound10 Function is used to convert a 16-word input table using the composition of RowRound and ColumnRounf functions.
     * The operation is repeated 10 times.
     * @param x the 16-word input table
     * @return the 16-word output table
     */
    public int[] doubleRound10(int[] x) {
        if (x.length != 16) { throw new IllegalArgumentException("doubleRound10"); }
        int[] result;
        result = columnRound(x);
        result = rowRound(result);
        for (int i = 0; i < 9; i++ ) {
            result = columnRound(result);
            result = rowRound(result);
        }
        return result;
    }

    /**
     * The LittleEndian Function is used to reverse the order of the given 4 bytes
     * @param x0, x1, x2, x3 - 4 input words of a byte length
     * @return an int value representing the reversed bytes
     */
    public int littleEndian(int x0, int x1, int x2, int x3) {
        /*int x0 =  (x >> 24) & 0xFF;
        int x1 =  (x >> 16) & 0xFF;
        int x2 =  (x >> 8) & 0xFF;
        int x3 = (x & 0xFF) & 0xFF;*/
        int result = x0 + (x1 << 8) + (x2 << 16) + (x3 << 24);
        return result;
    }
    /**
     * The LittleEndianInverse Function is used to inverse the result of The LittleEndian Function.
     * @param x an input word of 4 bytes length
     * @return a table of four bytes given in a reversed order
     */
    public int[] littleEndianInverse(int x) {
        /*int x0 = (x & 0xFF) & 0xFF;
        int x1 =  (x >> 8) & 0xFF;
        int x2 =  (x >> 16) & 0xFF;
        int x3 =  (x >> 24) & 0xFF;*/
        int[] result = {(x & 0xFF), ((x >> 8) & 0xFF), ((x >> 16) & 0xFF), ((x >> 24) & 0xFF)};
        return result;
    }

    /**
     * The Hash Function is used to convert 64 input bytes using previously defined functions.
     * It creates 16 words by calling the LittleEndian function on every 4 of 64 bytes.
     * Then it modifies each word using The DoubleRound10 function.
     * At the end, it creates 64 bytes of output by calling The LittleEndianInverse function on the sum of each input word and the modified one.
     * @param x 64-byte long input table
     * @return 64 byte long output table
     */
   public int[] hash(int[] x) {
        if (x.length != 64) { throw new IllegalArgumentException("hash"); }
        int[] result = new int[64];
        int[] doubleRound10X = new int[16];
        int[] xLittleEndian = new int[16];
        for (int i = 0; i < 16; i++) {
            xLittleEndian[i] = littleEndian( x[0 + (4 * i)], x[1 + ( 4 * i )], x[2 + ( 4 * i )], x[3 + ( 4 * i )] );
        }
        doubleRound10X = doubleRound10(xLittleEndian);
        int[] tempFourBytes;
       for (int i = 0; i < 16; i++) {
           tempFourBytes = littleEndianInverse(xLittleEndian[i] + doubleRound10X[i]);
           for (int j = 0; j < 4; j++) {
               result[(4 * i) + j] = tempFourBytes[j];
           }
       }
        return result;
    }

    /**
     * The Expansion Function is used to convert 2 input byte arrays into a 64-byte long array using The Hash Function.\
     * The first array may be either 16 or 32 byte long. The second array is necessarily 16 byte long.
     * @param key
     * @param nonce
     * @return
     */
    public int[] expansion(int[] key, int[] nonce) {


        int[] hashInput = new int[64];

        // In case of 32 bytes
        int[] a0 = {101, 120, 112, 97};
        int[] a1 = {110, 100, 32, 51};
        int[] a2 = {50, 45, 98, 121};
        int[] a3 = {116, 101, 32, 107};

        // In case of 16 bytes
        int[] b0 = {101, 120, 112, 97};
        int[] b1 = {110, 100, 32, 49};
        int[] b2 = {54, 45, 98, 121};
        int[] b3 = {116, 101, 32, 107};

        if (key.length == 32) {
            for (int i = 0; i < 4; i++) {
                hashInput[i] = a0[i];
                hashInput[20 + i] = a1[i];
                hashInput[40 + i] = a2[i];
                hashInput[60 + i] = a3[i];
            }
            for (int i = 0; i < 16; i++) {
                hashInput[4 + i] = key[i];
                hashInput[44 + i] = key[16 + i];
                hashInput[24 + i] = nonce[i];
            }
        }

        if (key.length == 16) {
            for (int i = 0; i < 4; i++) {
                hashInput[i] = b0[i];
                hashInput[20 + i] = b1[i];
                hashInput[40 + i] = b2[i];
                hashInput[60 + i] = b3[i];
            }

            for (int i = 0; i < 16; i++) {
                hashInput[4 + i] = key[i];
                hashInput[44 + i] = key[i];
                hashInput[24 + i] = nonce[i];
            }
        }
        return hash(hashInput);
    }
}
