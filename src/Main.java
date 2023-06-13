import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try {
            FileWriter fw1 = new FileWriter(new File("ciphertext.txt"));
            FileWriter fw2 = new FileWriter(new File("plaintext.txt"));
            fw1.write("");
            fw2.write("");
            fw1.close();
            fw2.close();
        } catch (IOException ioe) {}
        Salsa20 s = new Salsa20("1", "123");
        s.encryptFile("secretMessage.txt");
        s.decryptFile("ciphertext.txt");


    }
}
