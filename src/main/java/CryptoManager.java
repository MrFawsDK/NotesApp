import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKeyFactory;
import java.security.SecureRandom;
import java.util.Base64;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * CryptoManager håndterer AES-GCM kryptering og dekryptering af notes data
 * Bruger password-baseret nøglederivation (PBKDF2) for sikkerhed
 */
public class CryptoManager {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final int PBKDF2_ITERATIONS = 100000;
    
    private SecretKey secretKey;
    
    /**
     * Genererer en krypteringsnøgle baseret på brugerens password
     */
    public void generateKeyFromPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        spec.clearPassword();
    }
    
    /**
     * Genererer et tilfældigt salt til password hashing
     */
    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    
    /**
     * Krypterer tekst og returnerer Base64-encoded resultat
     */
    public String encrypt(String plainText) throws Exception {
        if (secretKey == null) {
            throw new IllegalStateException("Krypteringsnøgle er ikke genereret. Kald generateKeyFromPassword først.");
        }
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        
        // Generer tilfældig IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        
        byte[] encryptedData = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Kombiner IV og krypteret data
        byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
        
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }
    
    /**
     * Dekrypterer Base64-encoded krypteret tekst
     */
    public String decrypt(String encryptedText) throws Exception {
        if (secretKey == null) {
            throw new IllegalStateException("Krypteringsnøgle er ikke genereret. Kald generateKeyFromPassword først.");
        }
        
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
        
        // Udpak IV og krypteret data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
        
        System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, "UTF-8");
    }
    
    /**
     * Tjekker om krypteringsmanageren er initialiseret med en nøgle
     */
    public boolean isInitialized() {
        return secretKey != null;
    }
}