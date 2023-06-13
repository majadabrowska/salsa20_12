public class Main {
    public static void main(String[] args) {
        Salsa20 s = new Salsa20("lubiebdan", "hahahaaha", 32);
        s.encryptFile("secretMessage.txt", "ciphertext.txt", false);
        s.decryptFile("ciphertext.txt", "plaintext.txt",false);


    }
}
