/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.files;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author motaz
 */
public class Web {
    
    public static String URLAddSlash(String url){
        
        if ((url != null) && (!url.endsWith("/"))){
              url = url + "/";
        }
        return url;
    }
    
    public static String callURL(String methodURL, String contents, int waitSeconds, String contentType) throws 
	    IOException, MalformedURLException {
	
	if ((contentType == null) || (contentType.isEmpty())){
	    contentType = "text/json";
	}
    
        URL url = new URL(methodURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", contentType);
        conn.setDoOutput(true);
        
        return actualCall(conn, contents, "UTF-8");
    }     

    public static String callURLWithMethod(String methodURL, String contents, int waitSeconds, String contentType, String method, String encoding) throws 
	    IOException, MalformedURLException {
	
	if ((contentType == null) || (contentType.isEmpty())){
	    contentType = "text/json";
	}
    
        URL url = new URL(methodURL);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
       conn.setRequestProperty("Content-Type", contentType);
       
//Accept: */*
//Accept-Language: null
//Accept-Encoding: gzip, deflate
       
        
        conn.setRequestProperty("method", method);
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        
        return actualCall(conn, contents, encoding);
    }         
    
    public static String actualCall(URLConnection conn, String contents, String encode) throws IOException {
        
        String outputText = "";
        BufferedReader reader;
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), encode)) {
            writer.write(contents);
            
            writer.flush();
            String line;
            
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), encode));
            while ((line = reader.readLine()) != null) {
                String text = new String(line.getBytes(encode));
                outputText = outputText + line;
                
            }
        }
        
        reader.close();
        return outputText;
    }     
    
    
    
}
