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
    // Dimensioni richieste per le chiavi/IV in byte
    private static final int DESKEYSIZE = 8; // 64 bit per DES
    private static final int AES256KEYSIZE = 32; // 256 bit per AES-256
    private static final int CBCIVSIZE = 16; // 128 bit IV per CBC

    // Trasformazione selezionata (es. "AES/CBC/PKCS5Padding")
    private String TRANSFORMATION;

    public Loacker(){}

    /**
     * Cripta tutti i file nella directory specificata usando l'algoritmo, la chiave e l'IV forniti.
     * - Valida input con dataValidation
     * - Normalizza la chiave/IV se sono più corte (padding con zeri)
     * - Sovrascrive i file con i dati criptati
     * Restituisce true se tutto OK, false in caso di eccezione.
     */
    public boolean encrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new IllegalArgumentException("Invalid inputs");

            // Seleziona la trasformazione corrispondente all'algoritmo.
            TRANSFORMATION = getTransformation(algorithm);

            // Ottiene un'istanza di Cipher per la trasformazione selezionata.
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Determina dimensione chiave in base all'algoritmo
            int keySize = algorithm.contains("DES") ? DESKEYSIZE : AES256KEYSIZE;
            // Normalizza (padd) la chiave se più corta
            byte[] keyBytes = normalizeKeyBytes(key, keySize);
            SecretKey secretKey = new SecretKeySpec(keyBytes, algorithm.split("-")[0]);

            // Se modalità ECB non serve IV, altrimenti crea IvParameterSpec dai bytes normalizzati
            if (algorithm.contains("ECB")) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                IvParameterSpec ivSpec = new IvParameterSpec(normalizeIVBytes(iv));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            }

            // Controlla che la directory sia valida
            if (directory == null || !directory.isDirectory())
                throw new IllegalArgumentException("Invalid directory");

            File[] files = directory.listFiles();
            if (files == null) return false;

            // Itera sui file presenti nella directory (non entra nelle sottodirectory)
            for (File file : files) {
                if (!file.isFile()) continue;

                // Legge contenuto, lo cifra e lo inserisce in nuovo file .loacker
                byte[] bytes = Files.readAllBytes(file.toPath());
                byte[] encryptedData = cipher.doFinal(bytes);

                File target = new File(file.getParentFile(), file.getName() + ".loacker");
                Files.write(target.toPath(), encryptedData);
                Files.delete(file.toPath());
            }

            return true;
        } catch (Exception e) {
            // In caso di errore qualsiasi viene restituito false
            return false;
        }
    }

    /**
     * Decifra tutti i file nella directory specificata.
     * Stessa logica di encrypt ma con Cipher.DECRYPT_MODE.
     */
    public boolean decrypt(File directory, String algorithm, String key, String iv){
        try {
            if (!dataValidation(algorithm, key, iv))
                throw new IllegalArgumentException("Invalid inputs");

            // Seleziona la trasformazione corrispondente all'algoritmo.
            TRANSFORMATION = getTransformation(algorithm);

            // Ottiene un'istanza di Cipher per la trasformazione selezionata.
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
                if (!file.getName().endsWith(".loacker")) continue;

                // Nome originale del file
                String name = file.getName();
                // Sottrae al nome del file l'estensione .loacker
                String originalName = name.substring(0, name.length() - ".loacker".length());

                byte[] bytes = Files.readAllBytes(file.toPath());
                byte[] decryptedData = cipher.doFinal(bytes);
                File target = new File(file.getParentFile(), originalName);
                Files.write(target.toPath(), decryptedData);
                Files.delete(file.toPath());
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida gli input:
     * - algorithm e key non nulli
     * - la chiave non deve essere vuota né più lunga della dimensione richiesta
     * - se CBC: l'IV non deve essere vuoto né più lungo del CBCIVSIZE
     * Ritorna true se gli input sono accettabili per la normalizzazione successiva.
     */
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

    /**
     * Normalizza la chiave: crea un array di dimensione richiesta,
     * copia i byte forniti e lascia zeri per il padding se necessari.
     */
    private byte[] normalizeKeyBytes(String key, int requiredSize) {
        byte[] provided = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[requiredSize];
        int copyLen = Math.min(provided.length, requiredSize);
        System.arraycopy(provided, 0, result, 0, copyLen);

        return result;
    }

    /**
     * Normalizza l'IV in modo simile alla chiave (padding con zeri fino a CBCIVSIZE).
     */
    private byte[] normalizeIVBytes(String iv) {
        byte[] provided = iv.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[CBCIVSIZE];
        int copyLen = Math.min(provided.length, CBCIVSIZE);
        System.arraycopy(provided, 0, result, 0, copyLen);

        return result;
    }

    /**
     * Mappa la stringa dell'algoritmo selezionato alla trasformazione completa richiesta da Cipher.
     * Lancia IllegalArgumentException se l'algoritmo non è supportato.
     */
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
