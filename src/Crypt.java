import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Classe per la crittografia e decrittografia dei dati.
 */
public class Crypt {
    private final int DESKEYSIZE = 8; // 64 bit
    private final int AES256KEYSIZE = 32; // 256 bit
    private final int CBCIVSIZE = 16; // 128 bit
    public Crypt(){}

    // Funzione che cifra i dati, ritorna true se tutto ok, atrimenti false
    public boolean encrypt(String algorithm, String key, String iv){
        try {
            // Cifratura avvenuta con successo
            return true;
        } catch (Exception e) {
            // Rileva un errore durante la cifratura
            return false;
        }
    }

    // Funzione che decifra i dati, ritorna true se tutto ok, altrimenti false
    public boolean decrypt(){
        try {
            // Decifratura avvenuta con successo
            return true;
        } catch (Exception e) {
            // Rileva un errore durante la decifratura
            return false;
        }
    }
}
