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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author motaz
 */
public class Web {
    
    public static String callURL(String methodURL, String urlParameters, int waitSeconds, String contentType) throws 
	    IOException, MalformedURLException {
	
	if (contentType == null){
	    contentType = "text/json";
	}
    
        URL url = new URL(methodURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", contentType);
        conn.setDoOutput(true);
        String outputText = "";
        BufferedReader reader;
	try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream())) {
	    writer.write(urlParameters);
	    
	    writer.flush();
	    String line;
	    
	    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = reader.readLine()) != null) {
		
		outputText = outputText + line;
		
	    }
	}
        
        reader.close();
        return outputText;
    }     
    
}
