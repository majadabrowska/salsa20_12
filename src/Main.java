import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Salsa20 s = new Salsa20("", "XD");
        /*System.out.println(Arrays.toString(s.key));
        System.out.println(Arrays.toString(s.nonce));*/

        String msg = "I wonder if it works...";
        System.out.println("Plaintext is: \"" + msg + "\"");
        byte[] message = msg.getBytes();
        byte[] ciphertext = s.encrypt("".getBytes(),"XD".getBytes(), message);
        System.out.println("Encrypted: " + Arrays.toString(ciphertext));
        System.out.println("Decrypted text: \"" + s.decrypt("".getBytes(),"XD".getBytes(), ciphertext) + "\"'");
    }
}
