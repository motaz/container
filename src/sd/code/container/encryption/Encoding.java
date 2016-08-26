/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.encryption;

import java.io.IOException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author motaz
 */
public class Encoding {
    
   public static String base64decode(String text){

         try {
	    String decoded = new String(DatatypeConverter.parseBase64Binary(text), "UTF-8");
	    return decoded;
         }
         catch ( IOException e ) {
           return null;
         }

      }    
   
   public static String base64encode(String text){

         try {
	    String encoded = DatatypeConverter.printBase64Binary(text.getBytes());
	    return encoded;
         }
         catch ( Exception e ) {
           return null;
         }

      }    
   
    
}
