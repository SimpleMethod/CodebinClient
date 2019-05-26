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
public class linkClient implements linkClientInterface{
   private String SALT;
   private Key key;
    public linkClient(String SALT) {
        this.SALT = SALT;
        System.err.println(encrypt("8082"));
    }

    /**
     *
     * @param encryptText
     * @return
     */
    public String encrypt(String encryptText)
    {
        byte[] encrypted;
        String encodedString=null;
        try
        {
            key= new SecretKeySpec(SALT.getBytes(),"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
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
        String decrypted=null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            key= new SecretKeySpec(SALT.getBytes(),"AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(decryptText);
            decrypted = new String(cipher.doFinal(decoded));
            return decrypted;
        }
        catch(IllegalArgumentException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return decrypted;
    }
}
