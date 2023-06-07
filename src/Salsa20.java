import java.util.Arrays;
import java.lang.Math;

public class Salsa20 {
    byte[] key;
    byte[] nonce;

    public Salsa20(){}
    public Salsa20(String key, String nonce) {
        this.nonce = new byte[8];

        byte[] keyBytes = key.getBytes();
        byte[] nonceBytes = nonce.getBytes();

        int length = key.length();
        if( length < 16 ) {
            this.key = new byte[16];
            fillTo16(keyBytes);
        } else if ( length == 16 ) {
            this.key = keyBytes;
        } else if ( length < 32 ) {
            this.key = new byte[32];
            fillTo32(keyBytes);
        } else if ( length == 32 ) {
            this.key = keyBytes;
        } else {
            for (int i = 0; i < 32; i++) {
                this.key[i] = keyBytes[i];
            }
        }

        length = nonceBytes.length;
        if ( length < 8 ) {
            fillNonce(nonceBytes);
        } else if ( length == 8 ) {
            this.nonce = nonceBytes;
        } else {
            for (int i = 0; i < 8; i++) {
                this.nonce[i] = nonceBytes[i];
            }
        }
    }
    private void fillNonce(byte[] input) {
        int i = 0;
        for ( ; i < input.length; i++ ) {
            nonce[i] = input[i];
        }
        for ( ; i < 8; i++ ) {
            nonce[i] = 0;
        }
    }
    private void fillTo16(byte[] input) {
        int i = 0;
        for ( ; i < input.length; i++ ) {
            key[i] = input[i];
        }
        for ( ; i < 16; i++ ) {
            key[i] = 0;
        }
    }
    private void fillTo32(byte[] input) {
        int i = 0;
        for ( ; i < input.length; i++ ) {
            key[i] = input[i];
        }
        for ( ; i < 32; i++ ) {
            key[i] = 0;
        }
    }


    public int[] quarterRound(int x0, int x1, int x2, int x3) {
        int[] x = {x0, x1, x2, x3};
        x[1] = x[1] ^ (Integer.rotateLeft( (x[0] + x[3]), 7) );
        x[2] = x[2] ^ (Integer.rotateLeft( (x[1] + x[0]), 9) );
        x[3] = x[3] ^ (Integer.rotateLeft( (x[2] + x[1]), 13) );
        x[0] = x[0] ^ (Integer.rotateLeft( (x[3] + x[2]), 18) );
        return x;
    }
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
    public int littleEndian(byte x0, byte x1, byte x2, byte x3) {
        /*int intx0 = x0&0xFF;
        int intx1 = x1&0xFF;
        int intx2 = x2&0xFF;
        int intx3 = x3&0xFF;
        int result = intx0 + (intx1 << 8) + (intx2 << 16) + (intx3 << 24);*/
        int result = (((x3 & 0xFF) << 8 | x2 & 0xFF) << 8 | x1 & 0xFF) << 8 | x0 & 0xFF;;
        return result;
    }
    public byte[] littleEndianInverse(int x) {
        /*int x0 = (x & 0xFF) & 0xFF;
        int x1 =  (x >> 8) & 0xFF;
        int x2 =  (x >> 16) & 0xFF;
        int x3 =  (x >> 24) & 0xFF;*/
        byte[] result = {(byte)(x & 0xFF), (byte)((x >> 8) & 0xFF), (byte)((x >> 16) & 0xFF), (byte)((x >> 24) & 0xFF)};
        return result;
    }

   public byte[] hash(byte[] x) {
        if (x.length != 64) { throw new IllegalArgumentException("hash"); }
        byte[] result = new byte[64];

        int[] doubleRound10X;

        int[] xLittleEndian = new int[16];

        for (int i = 0; i < 16; i++) {
            xLittleEndian[i] = littleEndian( x[0 + (4 * i)], x[1 + ( 4 * i )], x[2 + ( 4 * i )], x[3 + ( 4 * i )] );
        }

        doubleRound10X = doubleRound10(xLittleEndian);


        byte[] tempFourBytes;

        for (int i = 0; i < 16; i++) {
            tempFourBytes = littleEndianInverse(xLittleEndian[i] + doubleRound10X[i]);
            for (int j = 0; j < 4; j++) {
                result[(4 * i) + j] = tempFourBytes[j];
            }
        }
        return result;
    }
    public byte[] expansion(byte[] key, byte[] nonce) {


        byte[] hashInput = new byte[64];

        // In case of 32 bytes
        byte[] a0 = {101, 120, 112, 97};
        byte[] a1 = {110, 100, 32, 51};
        byte[] a2 = {50, 45, 98, 121};
        byte[] a3 = {116, 101, 32, 107};

        // In case of 16 bytes
        byte[] b0 = {101, 120, 112, 97};
        byte[] b1 = {110, 100, 32, 49};
        byte[] b2 = {54, 45, 98, 121};
        byte[] b3 = {116, 101, 32, 107};

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

    public byte[] encrypt(byte[] key, byte[] nonce, byte[] message) {
        byte[] msgFilled = new byte[64];
        byte[] exp = expansion(key, nonce);
        if ( message.length < 64 ) {
            int i = 0;
            for ( ; i < message.length; i++ ) {
                msgFilled[i] = message[i];
            }
            for ( ; i < 64; i++ ) {
                msgFilled[i] = 0;
            }
        } else {
            msgFilled = message;
        }
        for (int i = 0; i < 64; i++) {
            msgFilled[i] = (byte) (msgFilled[i] ^ exp[i]);
        }
        return msgFilled;
    }

    public String decrypt (byte[] key, byte[] nonce, byte[] ciphertext) {
        byte[] exp = expansion(key, nonce);
        byte[] plainText = new byte[64];
        int mark = 0;
        for (int i = 0; i < 64; i++) {
            plainText[i] = (byte) (ciphertext[i] ^ exp[i]);
            if ( plainText[i] == 0 ) {
                mark = i;
                break;
            }
        }
        byte[] plainTextFiltered = Arrays.copyOfRange(plainText, 0, mark);
        String result = new String(plainTextFiltered);
        return result;
    }
}
