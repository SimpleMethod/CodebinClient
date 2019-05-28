package pl.simplemethod.codebin.linkDeploy;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class LinkClient implements LinkClientInterface {
   private String SALT;
   private Key KEY;
    public LinkClient(String SALT) {
        this.SALT = SALT;
        System.err.println(encrypt("8083"));
    }

    /**
     * Encrypt text
     * @param encryptText  Text for encrypt
     * @return Returns encrypted text
     */
    public String encrypt(String encryptText)
    {
        byte[] encrypted;
        String encodedString=null;
        try
        {
            KEY= new SecretKeySpec(SALT.getBytes(),"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            encrypted = cipher.doFinal(encryptText.getBytes());
            encodedString = Base64.getEncoder().encodeToString(encrypted);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return encodedString;
    }

    /**
     * Decrypted text
     * @param decryptText Text for decryption
     * @return Returns decrypted text
     */
    public String decrypt(String decryptText)
    {
        String decrypted;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            KEY= new SecretKeySpec(SALT.getBytes(),"AES");
            cipher.init(Cipher.DECRYPT_MODE, KEY);
            byte[] decoded = Base64.getDecoder().decode(decryptText);
            decrypted = new String(cipher.doFinal(decoded));
            return decrypted;
        }
        catch(IllegalArgumentException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
