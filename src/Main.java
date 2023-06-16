public class Main {
    public static void main(String[] args) {
        // One instance, encrypting and decrypting with its key and its nonce
        Salsa20 s = new Salsa20("lubiebdan", "bardzobardzo", 32);

        s.encryptFile("secretMessage.txt", "ciphertext.txt", false);
        s.decryptFile("ciphertext.txt", "plaintext.txt",false);


        // Now two different instances with different nonces, but the nonce is saved and retrieved from the ciphertext
        Salsa20 s2 = new Salsa20("eiti4life", "obysesjawyszla", 16);


        s2.encryptFile("secretMessage.txt", "nonceIncludedCiphertext.txt", true);

        Salsa20 s3 = new Salsa20("eiti4life", "", 16);
        s3.decryptFile("nonceIncludedCiphertext.txt", "nonceIncludedPlaintext.txt", true);


    }
}
