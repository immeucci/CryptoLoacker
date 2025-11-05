import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

/**
 * Classe per la criptazione e decriptazione dei dati.
 * Accetta chiavi/IV più corte della dimensione richiesta (vengono arrotondate con zeri),
 * rifiuta chiavi/IV troppo lunghi o vuoti.
 */
public class Loacker {
    private static final int DESKEYSIZE = 8; // 64 bit
    private static final int AES256KEYSIZE = 32; // 256 bit
    private static final int CBCIVSIZE = 16; // 128 bit
    private String TRANSFORMATION;

    public Loacker(){}

    public boolean encrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new IllegalArgumentException("Invalid inputs");

            TRANSFORMATION = getTransformation(algorithm);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            int keySize = algorithm.contains("DES") ? DESKEYSIZE : AES256KEYSIZE;
            byte[] keyBytes = normalizeKeyBytes(key, keySize);
            SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("-")[0]);

            if (algorithm.contains("ECB")) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(normalizeIVBytes(iv));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            }

            if (directory == null || !directory.isDirectory())
                throw new IllegalArgumentException("Invalid directory");

            File[] files = directory.listFiles();
            if (files == null) return false;

            for (File file : files) {
                if (!file.isFile()) continue;
                byte[] bytes = Files.readAllBytes(file.toPath());
                byte[] encryptedData = cipher.doFinal(bytes);
                Files.write(file.toPath(), encryptedData);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean decrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new IllegalArgumentException("Invalid inputs");

            TRANSFORMATION = getTransformation(algorithm);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            int keySize = algorithm.contains("DES") ? DESKEYSIZE : AES256KEYSIZE;
            byte[] keyBytes = normalizeKeyBytes(key, keySize);
            SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("-")[0]);

            if (algorithm.contains("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(normalizeIVBytes(iv));
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            }

            if (directory == null || !directory.isDirectory())
                throw new IllegalArgumentException("Invalid directory");

            File[] files = directory.listFiles();
            if (files == null) return false;

            for (File file : files) {
                if (!file.isFile()) continue;
                byte[] bytes = Files.readAllBytes(file.toPath());
                byte[] decryptedData = cipher.doFinal(bytes);
                Files.write(file.toPath(), decryptedData);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean dataValidation(String algorithm, String key, String iv) {
        if (algorithm == null || key == null) return false;

        int keySize;
        if (algorithm.contains("DES")) keySize = DESKEYSIZE;
        else keySize = AES256KEYSIZE;

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == 0 || keyBytes.length > keySize) return false; // non vuota, non troppo lunga

        if (algorithm.contains("CBC")) {
            if (iv == null) return false;
            byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
            if (ivBytes.length == 0 || ivBytes.length > CBCIVSIZE) return false; // non vuoto, non troppo lungo
        }

        return true;
    }

    private byte[] normalizeKeyBytes(String key, int requiredSize) {
        byte[] provided = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[requiredSize];
        int copyLen = Math.min(provided.length, requiredSize);
        System.arraycopy(provided, 0, result, 0, copyLen);

        return result;
    }

    private byte[] normalizeIVBytes(String iv) {
        byte[] provided = iv.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[CBCIVSIZE];
        int copyLen = Math.min(provided.length, CBCIVSIZE);
        System.arraycopy(provided, 0, result, 0, copyLen);

        return result;
    }

    private String getTransformation(String algorithm) {
        return switch (algorithm) {
            case "DES-ECB" -> "DES/ECB/PKCS5Padding";
            case "DES-CBC" -> "DES/CBC/PKCS5Padding";
            case "AES-256-ECB" -> "AES/ECB/PKCS5Padding";
            case "AES-256-CBC" -> "AES/CBC/PKCS5Padding";
            default -> throw new IllegalArgumentException("Unsupported algorithm");
        };
    }
}
