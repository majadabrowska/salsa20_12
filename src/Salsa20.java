import java.io.*;

public class Salsa20 {
    byte[] key;
    byte[] nonce;

    /**
     * constructor used only for testing purposes
     */
    public Salsa20() {}

    /**
     * This constructor takes both key and nonce as strings and converts them to bytes. If a nonce should be zero,
     * an empty String should be provided. The constructor lets the user choose whether they want to operate on
     * a 16-bit or a 32-bit key. If given key or nonce Strings are longer than their byte sizes, they are truncated
     * @param key the 16 or 32-bit key
     * @param nonce the
     * @param keyMode
     */
    public Salsa20(String key, String nonce, int keyMode) {
        if ( keyMode != 32 && keyMode != 16 ) {
            IllegalArgumentException e = new IllegalArgumentException("Illegal argument in the constructor!");
            throw e;
        }
        this.nonce = new byte[8];

        byte[] keyBytes = key.getBytes();
        byte[] nonceBytes = nonce.getBytes();

        // Filling the key array to chosen length
        int length = key.length();
        if (length <= keyMode) {
            this.key = new byte[keyMode];
            fillToKeySize(keyBytes, length, keyMode);
        } else {
            this.key = new byte[keyMode];
            for (int i = 0; i < keyMode; i++) {
                this.key[i] = keyBytes[i];
            }
        }
        // Filling the nonce to proper length
        length = nonceBytes.length;
        if ( length < 8 ) {
            fillNonce(nonceBytes, length);
        } else if ( length == 8 ) {
            this.nonce = nonceBytes;
        } else {
            for (int i = 0; i < 8; i++) {
                this.nonce[i] = nonceBytes[i];
            }
        }
    }

    /**
     * Helper method. Used in the constructor to fill a nonce, which was given a value shorter than 8 bytes
     * @param input
     * @param currentLength
     */
    private void fillNonce(byte[] input, int currentLength) {
        int i = 0;
        for ( ; i < currentLength; i++ ) {
            nonce[i] = input[i];
        }
        for ( ; i < 8; i++ ) {
            nonce[i] = 0;
        }
    }

    /**
     * Helper method for the constructor. Used for filling the key, which was given a value shorter than the specified one
     * @param givenKey the key bytes taken from String
     * @param currentLength the length of the key byte array
     * @param desiredLength the key length chosen by the user
     */
    private void fillToKeySize(byte[] givenKey, int currentLength, int desiredLength) {
        for (int i = 0; i < currentLength; i++) {
            key[i] = givenKey[i];
        }
        for (int i = currentLength; i < desiredLength; i++) {
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
        return rowRound(columnRound(x));
    }
    public int[] doubleRound6(int[] x) {
        if (x.length != 16) { throw new IllegalArgumentException("doubleRound10"); }
        int[] result;
        result = columnRound(x);
        result = rowRound(result);
        /**
         * TODO CHANGE!!
         * To pass tests, you must change to i < 9 - to pass the original vector tests
         * To make is a Salsa20/12 variant, change to i < 5
         */
        for (int i = 0; i < 5; i++ )
           result = doubleRound(result);
        return result;
    }
    public int littleEndian(byte x0, byte x1, byte x2, byte x3) {
        return (((x3 & 0xFF) << 8 | x2 & 0xFF) << 8 | x1 & 0xFF) << 8 | x0 & 0xFF;
    }
    public byte[] littleEndianInverse(int x) {
        return new byte[]{(byte)(x & 0xFF), (byte)((x >> 8) & 0xFF), (byte)((x >> 16) & 0xFF), (byte)((x >> 24) & 0xFF)};
    }

   public byte[] hash(byte[] x) {
        if (x.length != 64) { throw new IllegalArgumentException("hash"); }

        byte[] result = new byte[64];

        int[] doubleRound10X;

        int[] xLittleEndian = new int[16];

        for (int i = 0; i < 16; i++) {
            xLittleEndian[i] = littleEndian( x[4 * i], x[1 + ( 4 * i )], x[2 + ( 4 * i )], x[3 + ( 4 * i )] );
        }

        doubleRound10X = doubleRound6(xLittleEndian);


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

        // In case of 32 bytes - expand 32-byte k
        byte[] a0 = {101, 120, 112, 97};
        byte[] a1 = {110, 100, 32, 51};
        byte[] a2 = {50, 45, 98, 121};
        byte[] a3 = {116, 101, 32, 107};

        // In case of 16 bytes - expand 16-byte k
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


    /**
     * This method has two modes. It can either include a nonce in the first 8 bits of the ciphertext, or not.
     * The method reads input from the specified file and writes it to another.
     * @param Inputfilename file from which to read
     * @param outputFilename target file for the ciphertext
     * @param provideNonce determines whether to include the nonce or not
     */
    public void encryptFile (String Inputfilename, String outputFilename, boolean provideNonce) {
        try {
            FileInputStream fis = new FileInputStream(Inputfilename);
            File ciphertext = new File(outputFilename);
            FileOutputStream fos = new FileOutputStream(ciphertext.getAbsolutePath(), true);

            if ( provideNonce ) {
                fos.write(nonce);
            }

            byte[] buffer = new byte[64];
            int noBytesRead = fis.read(buffer);
            byte[] keyStream;
            byte[] nonceFilled = new byte[16];
            long blockNumber = 0;
            if ( noBytesRead == -1 ) {
                throw new IllegalArgumentException("Provided file is empty!");
            }
            while ( noBytesRead == 64 ) {
                fillNonce(nonce, nonceFilled, blockNumber);
                keyStream = expansion(key, nonceFilled);
                for (int i = 0; i < 64; i++) {
                    buffer[i] = (byte) (buffer[i] ^ keyStream[i]);
                }
                fos.write(buffer);
                blockNumber++;
                noBytesRead = fis.read(buffer);
            }
            if ( noBytesRead != 0 ) {
                fillNonce(nonce, nonceFilled,blockNumber);
                padding(buffer, noBytesRead);
                keyStream = expansion(key, nonceFilled);
                for (int i = 0; i < 64; i++) {
                    buffer[i] = (byte) (buffer[i] ^ keyStream[i]);
                }
                fos.write(buffer);
            }

            fis.close();
            fos.close();
        } catch ( FileNotFoundException fnf) {
            fnf.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }

    /**
     * This method has two modes as well. It can either decrypt the file using the nonce provided,
     * or read the nonce from a file (if, of course, one is present).
     * @param Inputfilename file from which to read
     * @param outputFilename target file for the plaintext
     * @param nonceProvided determines whether to include the method acquires the nonce from ciphertext, or not
     */
    void decryptFile (String Inputfilename, String outputFilename, boolean nonceProvided) {
        try {
            FileInputStream fis = new FileInputStream(Inputfilename);
            File plaintext = new File(outputFilename);
            FileOutputStream fos = new FileOutputStream(plaintext.getAbsolutePath(), true);

            if ( nonceProvided ){
                if (fis.readNBytes(nonce, 0, 8) == 0) {
                    throw new IllegalArgumentException("Provided file is empty!");
                }
            }


            byte[] buffer = new byte[64];
            int noBytesRead = fis.read(buffer);
            byte[] keyStream;
            byte[] nonceFilled = new byte[16];
            long blockNumber = 0;
            while ( noBytesRead == 64 ) {
                fillNonce(nonce, nonceFilled, blockNumber);
                keyStream = expansion(key, nonceFilled);
                int index = 0;
                for ( ; index < 64; index++) {
                    buffer[index] = (byte) (buffer[index] ^ keyStream[index]);
                }
                if ( buffer[index - 1] != 0 ) {
                    fos.write(buffer);
                    blockNumber++;
                    noBytesRead = fis.read(buffer);
                } else {
                    index = 0;
                    // Deleting the value, so the while loop doesn't go off one last time
                    noBytesRead = 0;
                    while ( buffer[index] != 0 ) {
                        fos.write(buffer[index]);
                        index++;
                    }
                }
            }
            fis.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }

    /**
     * As the number v, which stands for the number of blocks that have been processed is 8 bytes by design, it is a long,
     * this method divides the long into an array of 8 bytes and concatenates it with the nonce.
     * @param nonce the nonce used for encryption / decryption
     * @param nonceFilled the array representing a nonce concatenated with block number
     * @param blockNumber block number to be converted and parsed
     */
    private void fillNonce(byte[] nonce, byte[] nonceFilled, long blockNumber) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeLong(blockNumber);
            dos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        byte[] longAsBytes = bos.toByteArray();
        for (int i = 0; i < 8; i++) {
            nonceFilled[i] = nonce[i];
            nonceFilled[8+i] = longAsBytes[i];
        }
        try {
            dos.close();
            bos.close();
        } catch (IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    /**
     * Helper method used for filling the last block of data processed during encryption with zeros, in case it is not 64 bytes.
     * @param toPad block to be written to a ciphertext, which requires padding.
     * @param presentLength its current length
     */
    private void padding (byte[] toPad, int presentLength) {
        for (int i = presentLength; i < 64; i++) {
            toPad[i] = 0;
        }
    }

}
