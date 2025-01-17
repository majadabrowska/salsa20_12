
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * All test vectors are from the algorithm's original documentation, by D. J. Bernstein.
 * The link:
 * https://cr.yp.to/snuffle/spec.pdf
 */

class Salsa20Test {

    Salsa20 s;

    byte[] k = new byte[32];
    byte[] n = new byte[16];
    void expansionTestVectorsSetup() {
        for (int i = 0; i < 16; i++) {
            k[i] = (byte) (i + 1);
            k[16 + i] = (byte) (201 + i);

            n[i] = (byte) (101 + i);
        }
    }
    @BeforeEach
    void setup() {
        s = new Salsa20();
    }
    @Test
    void quarterRoundTest1() {
        int[] result = s.quarterRound(0,0,0,0);
        int[] expected = {0,0,0,0};
        assertArrayEquals(expected, result);
    }
    @Test
    void quarterRoundTest4() {
        int[] result = s.quarterRound(0,0,1,0);
        int[] expected = {0x80040000, 0x00000000, 0x00000001, 0x00002000};
        assertArrayEquals(expected, result);
    }
    @Test
    void quarterRoundTest5() {
        int[] result = s.quarterRound(0xe7e8c006, 0xc4f9417d, 0x6479b4b2, 0x68c67137);
        int[] expected = {0xe876d72b, 0x9361dfd5, 0xf1460244, 0x948541a3};
        assertArrayEquals(expected, result);
    }
    
    
    @Test
    void rowRoundTest1() {
        int[] toEncrypt = {0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000};
        int[] result = s.rowRound(toEncrypt);
        int[] expected = {0x08008145, 0x00000080, 0x00010200, 0x20500000,
                0x20100001, 0x00048044, 0x00000080, 0x00010000,
                0x00000001, 0x00002000, 0x80040000, 0x00000000,
                0x00000001, 0x00000200, 0x00402000, 0x88000100};
        assertArrayEquals(expected, result);
    }
    @Test
    void rowRoundTest2() {
        int[] toEncrypt = {0x08521bd6, 0x1fe88837, 0xbb2aa576, 0x3aa26365,
                0xc54c6a5b, 0x2fc74c2f, 0x6dd39cc3, 0xda0a64f6,
                0x90a2f23d, 0x067f95a6, 0x06b35f61, 0x41e4732e,
                0xe859c100, 0xea4d84b7, 0x0f619bff, 0xbc6e965a};
        int[] result = s.rowRound(toEncrypt);
        int[] expected = {0xa890d39d, 0x65d71596, 0xe9487daa, 0xc8ca6a86,
                0x949d2192, 0x764b7754, 0xe408d9b9, 0x7a41b4d1,
                0x3402e183, 0x3c3af432, 0x50669f96, 0xd89ef0a8,
                0x0040ede5, 0xb545fbce, 0xd257ed4f, 0x1818882d};
        assertArrayEquals(expected, result);
    }

    @Test
    void columnRoundTest1() {
        int[] toEncrypt = {0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000001, 0x00000000, 0x00000000, 0x00000000};
        int[] result = s.columnRound(toEncrypt);
        int[] expected = {0x10090288, 0x00000000, 0x00000000, 0x00000000,
                0x00000101, 0x00000000, 0x00000000, 0x00000000,
                0x00020401, 0x00000000, 0x00000000, 0x00000000,
                0x40a04001, 0x00000000, 0x00000000, 0x00000000};
        assertArrayEquals(expected, result);
    }
    @Test
    void columnRoundTest2() {
        int[] toEncrypt = {0x08521bd6, 0x1fe88837, 0xbb2aa576, 0x3aa26365,
                0xc54c6a5b, 0x2fc74c2f, 0x6dd39cc3, 0xda0a64f6,
                0x90a2f23d, 0x067f95a6, 0x06b35f61, 0x41e4732e,
                0xe859c100, 0xea4d84b7, 0x0f619bff, 0xbc6e965a};
        int[] result = s.columnRound(toEncrypt);
        int[] expected = {0x8c9d190a, 0xce8e4c90, 0x1ef8e9d3, 0x1326a71a,
                0x90a20123, 0xead3c4f3, 0x63a091a0, 0xf0708d69,
                0x789b010c, 0xd195a681, 0xeb7d5504, 0xa774135c,
                0x481c2027, 0x53a8e4b5, 0x4c1f89c5, 0x3f78c9c8};
        assertArrayEquals(expected, result);
    }

    @Test
    void doubleRoundTest1() {
        int[] toEncrypt = {0x00000001, 0x00000000, 0x00000000, 0x00000000,
                0x00000000, 0x00000000, 0x00000000, 0x00000000,
                0x00000000, 0x00000000, 0x00000000, 0x00000000,
                0x00000000, 0x00000000, 0x00000000, 0x00000000};
        int[] result = s.doubleRound(toEncrypt);
        int[] expected = {0x8186a22d, 0x0040a284, 0x82479210, 0x06929051,
                0x08000090, 0x02402200, 0x00004000, 0x00800000,
                0x00010200, 0x20400000, 0x08008104, 0x00000000,
                0x20500000, 0xa0000040, 0x0008180a, 0x612a8020};
        assertArrayEquals(expected, result);
    }
    @Test
    void doubleRoundTest2() {
        int[] toEncrypt = {0xde501066, 0x6f9eb8f7, 0xe4fbbd9b, 0x454e3f57,
                0xb75540d3, 0x43e93a4c, 0x3a6f2aa0, 0x726d6b36,
                0x9243f484, 0x9145d1e8, 0x4fa9d247, 0xdc8dee11,
                0x054bf545, 0x254dd653, 0xd9421b6d, 0x67b276c1};
        int[] result = s.doubleRound(toEncrypt);
        int[] expected = {0xccaaf672, 0x23d960f7, 0x9153e63a, 0xcd9a60d0,
                0x50440492, 0xf07cad19, 0xae344aa0, 0xdf4cfdfc,
                0xca531c29, 0x8e7943db, 0xac1680cd, 0xd503ca00,
                0xa74b2ad6, 0xbc331c5c, 0x1dda24c7, 0xee928277};
        assertArrayEquals(expected,result);
    }

    @Test
    void littleEndianTest1() {
        //int initial = ByteBuffer.wrap(array).getInt();
        int result = s.littleEndian((byte)86,(byte)75,(byte)30,(byte)9);
        int expected = 0x091E4B56;
        assertEquals(expected,result);
        byte[] array = {86,75,30,9};
        byte[] inverse = s.littleEndianInverse(0x091E4B56);
        assertArrayEquals(array, inverse);
    }
    @Test
    void littleEndianTest2() {
        int[] array = {};
        //int initial = ByteBuffer.wrap(array).getInt();
        int result = s.littleEndian((byte)0,(byte)0,(byte)0,(byte)0);
        int expected = 0;
        assertEquals(expected,result);
    }
    @Test
    void littleEndianTest3() {
        int[] array = {};
        int result = s.littleEndian((byte)255, (byte)255, (byte)255, (byte)250);
        int expected = 0xfaffffff;
        assertEquals(expected,result);
    }


    @Test
    void hashTest1() {
        byte[] array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] result = s.hash(array);
        byte[] expected = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        assertArrayEquals(expected, result);
    }
    @Test
    void hashTest2() {
        int[] array = {211,159, 13,115, 76, 55, 82,183, 3,117,222, 37,191,187,234,136,
                49,237,179, 48, 1,106,178,219,175,199,166, 48, 86, 16,179,207,
                31,240, 32, 63, 15, 83, 93,161,116,147, 48,113,238, 55,204, 36,
                79,201,235, 79, 3, 81,156, 47,203, 26,244,243, 88,118,104, 54};
        byte[] byteArray = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteArray[i] = (byte) array[i];
        }
        byte[] result = s.hash(byteArray);
        int[] expected = {109, 42,178,168,156,240,248,238,168,196,190,203, 26,110,170,154,
                29, 29,150, 26,150, 30,235,249,190,163,251, 48, 69,144, 51, 57,
                118, 40,152,157,180, 57, 27, 94,107, 42,236, 35, 27,111,114,114,
                219,236,232,135,111,155,110, 18, 24,232, 95,158,179, 19, 48,202};
        byte[] byteExpected = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteExpected[i] = (byte) expected[i];
        }
        assertArrayEquals(byteExpected,result);
    }
    @Test
    void hashTest3() {
        int[] array = {88,118,104, 54, 79,201,235, 79, 3, 81,156, 47,203, 26,244,243,
                191,187,234,136,211,159, 13,115, 76, 55, 82,183, 3,117,222, 37,
                86, 16,179,207, 49,237,179, 48, 1,106,178,219,175,199,166, 48,
                238, 55,204, 36, 31,240, 32, 63, 15, 83, 93,161,116,147, 48,113};
        byte[] byteArray = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteArray[i] = (byte) array[i];
        }
        byte[] result = s.hash(byteArray);
        int[] expected = {179, 19, 48,202,219,236,232,135,111,155,110, 18, 24,232, 95,158,
                26,110,170,154,109, 42,178,168,156,240,248,238,168,196,190,203,
                69,144, 51, 57, 29, 29,150, 26,150, 30,235,249,190,163,251, 48,
                27,111,114,114,118, 40,152,157,180, 57, 27, 94,107, 42,236, 35};
        byte[] byteExpected = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteExpected[i] = (byte) expected[i];
        }
        assertArrayEquals(byteExpected,result);
    }
    @Test
    void hashTest4() {
        int[] array = {6,124, 83,146, 38,191, 9, 50, 4,161, 47,222,122,182,223,185,
                75, 27, 0,216, 16,122, 7, 89,162,104,101,147,213, 21, 54, 95,
                225,253,139,176,105,132, 23,116, 76, 41,176,207,221, 34,157,108,
                94, 94, 99, 52, 90,117, 91,220,146,190,239,143,196,176,130,186};
        byte[] byteArray = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteArray[i] = (byte) array[i];
        }
        byte[] result = s.hash(byteArray);
        for (int i = 0; i < 999999; i++) {
            result = s.hash(result);
        }
        int[] expected = {8, 18, 38,199,119, 76,215, 67,173,127,144,162,103,212,176,217,
                192, 19,233, 33,159,197,154,160,128,243,219, 65,171,136,135,225,
                123, 11, 68, 86,237, 82, 20,155,133,189, 9, 83,167,116,194, 78,
                122,127,195,185,185,204,188, 90,245, 9,183,248,226, 85,245,104};
        byte[] byteExpected = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteExpected[i] = (byte) expected[i];
        }
        assertArrayEquals(byteExpected,result);
    }

    @Test
    void expansionTest1() {
        expansionTestVectorsSetup();
        byte[] result = s.expansion(k, n);
        int[] expected = {69, 37, 68, 39, 41, 15,107,193,255,139,122, 6,170,233,217, 98,
                89,144,182,106, 21, 51,200, 65,239, 49,222, 34,215,114, 40,126,
                104,197, 7,225,197,153, 31, 2,102, 78, 76,176, 84,245,246,184,
                177,160,133,130, 6, 72,149,119,192,195,132,236,234,103,246, 74};
        byte[] byteExpected = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteExpected[i] = (byte) expected[i];
        }
        assertArrayEquals(byteExpected,result);
    }
    @Test
    void expansionTest2() {
        expansionTestVectorsSetup();
        byte[] result = s.expansion(Arrays.copyOfRange(k,0,16), n);
        int[] expected = {39,173, 46,248, 30,200, 82, 17, 48, 67,254,239, 37, 18, 13,247,
                241,200, 61,144, 10, 55, 50,185, 6, 47,246,253,143, 86,187,225,
                134, 85,110,246,161,163, 43,235,231, 94,171, 51,145,214,112, 29,
                14,232, 5, 16,151,140,183,141,171, 9,122,181,104,182,177,193};
        byte[] byteExpected = new byte[64];
        for (int i = 0; i < 64; i++) {
            byteExpected[i] = (byte) expected[i];
        }
        assertArrayEquals(byteExpected,result);
    }
}
