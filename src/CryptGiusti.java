import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author lucagiusti
 */
public class CryptGiusti {

    // Serve dopo per generare la chiave dell'algoritmo 
    private static final String ALGORITHM = "DES";
    
    // Si usa la modalità CBC (Cipher Block Chaining) con PKCS5 Padding
    // per gestire stringhe di lunghezza arbitraria.
    
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";

    // Vettore di Inizializzazione (IV) di 8 byte richiesto dalla modalità CBC.
    // Per semplicità usiamo un IV fisso.
    private static final byte[] IV = new byte[] { 
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 
    };

    /**
     * Cifra il testo in chiaro utilizzando l'algoritmo DES.
     */
    public static byte[] encrypt(String plainText, SecretKey key) 
            throws Exception {
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        
        // Inizializza il Cipher in modalità Cifratura (ENCRYPT_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decifra il testo cifrato in chiaro.
     */
    public static String decrypt(byte[] cipherText, SecretKey key) 
            throws Exception {
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        
        // Inizializza il Cipher in modalità Decifratura (DECRYPT_MODE)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String secretKeyInput;
        String originalString;

        // 1. Acquisizione della Chiave
        while (true) {
            System.out.println("Inserisci la chiave segreta (DEVE essere esattamente 8 caratteri):");
            secretKeyInput = scanner.nextLine();
            if (secretKeyInput.length() == 8) {
                break;
            }
            System.err.println("Errore: La chiave DES deve essere lunga 8 caratteri (64 bit).");
        }
        
        // 2. Acquisizione della Stringa da Cifrare
        System.out.println("\nInserisci la stringa da cifrare:");
        originalString = scanner.nextLine();
        
        try {
            // Conversione della stringa chiave in un oggetto SecretKey
            byte[] keyBytes = secretKeyInput.getBytes(StandardCharsets.UTF_8);
            SecretKey desKey = new SecretKeySpec(keyBytes, ALGORITHM);

            // --- Cifratura ---
            byte[] encryptedBytes = encrypt(originalString, desKey);
            String encodedString = Base64.getEncoder().encodeToString(encryptedBytes);
            
            // --- Decifratura ---
            String decryptedString = decrypt(encryptedBytes, desKey);

            // --- Risultati ---
            System.out.println("\n------------------------------------------------");
            System.out.println("Stringa Originale: " + originalString);
            System.out.println("Chiave (8 byte): " + secretKeyInput);
            System.out.println("IV (8 byte): " + bytesToHex(IV));
            System.out.println("------------------------------------------------");
            System.out.println("Testo Cifrato (in Base64): " + encodedString);
            System.out.println("Testo Decifrato: " + decryptedString);
            System.out.println("------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    // Funzione di utilità per stampare i byte in formato esadecimale (opzionale)
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}