/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.files;

import java.io.IOException;
import java.net.URLConnection;
import sd.code.container.url.CodeURL;

/**
 *
 * @author motaz
 */
public class Web {
    
    public static String URLAddSlash(String url){
        
       return CodeURL.URLAddSlash(url);
       
    }
    
    public static String callURL(String methodURL, String contents, int waitSeconds, String contentType) throws IOException { 
            
	  return CodeURL.callURL(methodURL, contents, waitSeconds, contentType);
    }     

    public static String callURLWithMethod(String methodURL, String contents, int waitSeconds, String contentType, 
            String method, String encoding) throws IOException {
	
	return CodeURL.callURLWithMethod(methodURL, contents, waitSeconds, contentType, method, encoding);
        
    }         
    
    public static String actualCall(URLConnection conn, String contents, String encode) throws IOException {
        
       return CodeURL.actualCall(conn, contents, encode);
       
    }     
    
    
}
