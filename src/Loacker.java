import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

/**
 * Classe per la criptazione e decriptazione dei dati.
 */
public class Loacker {
    private static final int DESKEYSIZE = 8; // 64 bit
    private static final int AES256KEYSIZE = 32; // 256 bit
    private static final int CBCIVSIZE = 16; // 128 bit
    private String TRANSFORMATION;

    public Loacker(){}

    /**
     * Funzione che cifra i dati, ritorna true se tutto ok, altrimenti false
     */
    public boolean encrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new Exception();

            TRANSFORMATION = getTransformation(algorithm);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm.split("-")[0]);

            if (algorithm.contains("EBC")) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            }

            File[] files = directory.listFiles();
            for (File file : files) {
                byte[] bytes = Files.readAllBytes(file.toPath());

                byte[] encryptedData = cipher.doFinal(bytes);
                Files.write(file.toPath(), encryptedData);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Funzione che decifra i dati, ritorna true se tutto ok, altrimenti false
     */
    public boolean decrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new Exception();

            TRANSFORMATION = getTransformation(algorithm);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm.split("-")[0]);

            if (algorithm.contains("EBC")) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            }

            File[] files = directory.listFiles();
            for (File file : files) {
                byte[] bytes = Files.readAllBytes(file.toPath());

                byte[] decryptedData = cipher.doFinal(bytes);
                Files.write(file.toPath(), decryptedData);
            }

            return true;
        } catch (Exception e) {
            // Rileva un errore durante la decifratura
            return false;
        }
    }

    /**
     * Funzione di validazione dei dati di input, restituisce false nel caso in cui non vadano bene
     */
    private boolean dataValidation(String algorithm, String key, String iv) {
        // Controlla la linghezza dell'IV se l'algoritmo utilizza la modalità CBC
        if (algorithm.contains("CBC") && iv.getBytes(StandardCharsets.UTF_8).length > CBCIVSIZE)
            return false;

        // Controlla la lunghezza della chiave in base all'algoritmo scelto con un operatore ternario
        int keySize = algorithm.contains("DES") ? DESKEYSIZE : algorithm.contains("AES") ? AES256KEYSIZE : 0;
        return key.getBytes(StandardCharsets.UTF_8).length > keySize;
    }

    /**
     * Funzione che ritorna la transformation in base all'algoritmo scelto
     */
    private String getTransformation(String algorithm) {
        return switch (algorithm) {
            case "DES-ECB" -> "DES/ECB/PKCS5Padding";
            case "DES-CBC" -> "DES/CBC/PKCS5Padding";
            case "AES-256-ECB" -> "AES/ECB/PKCS5Padding";
            case "AES-256-CBC" -> "AES/CBC/PKCS5Padding";
            default -> throw new IllegalArgumentException("");
        };
    }
}
