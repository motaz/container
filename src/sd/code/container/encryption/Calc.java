/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.encryption;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author motaz
 */
public class Calc {
    
    
    public static String getMD5(String pass) {
        try {
          MessageDigest m = MessageDigest.getInstance("MD5");
          byte[] data = pass.getBytes();
          m.update(data, 0, data.length);

          BigInteger i = new BigInteger(1,m.digest());
          return (String.format("%1$032X", i).toLowerCase());
        }
        catch (NoSuchAlgorithmException ex) {
            System.out.println("Error in GetMD5: "  + ex.toString());
            return(null);
        }
    }    
    
}
