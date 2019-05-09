/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.url;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import sd.code.container.types.HTTPResponse;

/**
 *
 * @author motaz
 */
public class CodeURL {
    
    public static String URLAddSlash(String url){
        
        if ((url != null) && (!url.endsWith("/"))){
              url = url + "/";
        }
        return url;
    }
    
    public static HTTPResponse callURL(String aURL, String contents, int waitSeconds, String contentType) throws 
	    MalformedURLException {
	
        HTTPResponse response = new HTTPResponse();
      
	if ((contentType == null) || (contentType.isEmpty())){
	    contentType = "text/json";
	}
    
        URL url = new URL(aURL);
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();

            conn.setConnectTimeout(waitSeconds * 1000);            
            conn.setReadTimeout(waitSeconds * 1000);        
            conn.setRequestProperty("Content-Type", contentType);
            conn.setDoOutput(true);
            response.responseText = actualCall(conn, contents, "UTF-8");

            response.responsCode = conn.getResponseCode();
            
          } catch (IOException ex) {
            response.responsCode = 500;
             //response.responseText = ex.toString();
            if (ex.toString().contains("401")){
                response.responsCode = 401;
               //  response.responseText = "Invalid Authentication: " + ex.toString();
            }
           
        }
        return response;
    }     

    public static String callURLWithMethod(String aURL, String contents, int waitSeconds, String contentType, 
            String method, String encoding) throws IOException {
	
	if ((contentType == null) || (contentType.isEmpty())){
	    contentType = "text/json";
	}
    
        URL url = new URL(aURL);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", contentType);
       
 
        
        conn.setRequestProperty("method", method);
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        
        return actualCall(conn, contents, encoding);
    }         
 
    public static String callHTTPSURLWithAuth(String aURL, String username, String password, String contents, int waitSeconds, String contentType,
            String method, String encoding) throws IOException {
        
        disableSslVerification();
        return callURLWithAuth(aURL, username, password, contents, waitSeconds, contentType, method, encoding);
    }
    
    public static String callURLWithAuth(String aURL , String username, String password, String contents, int waitSeconds, String contentType, 
            String method, String encoding) throws IOException {
        
        
        String userCredentials = username + ":" + password;
        String basicAuth = new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        
        if ((contentType == null) || (contentType.isEmpty())){
	    contentType = "text/json";
	}
        
        URL url = new URL(aURL);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Authorization", basicAuth);
 
        
        conn.setRequestProperty("method", method);
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        
        return actualCall(conn, contents, encoding);
    }
    
    
    public static String actualCall(HttpURLConnection conn, String contents, String encode) throws IOException {
        
        String outputText = "";
        BufferedReader reader;
        
        if ((contents != null) && (!contents.isEmpty())){
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), encode);
            writer.write(contents);

            writer.flush();
            writer.close();
        }
        
        String line;
        InputStream is = null;  
        int statusCode = conn.getResponseCode();        
        if (statusCode >= 200 && statusCode < 400) {
           // Create an InputStream in order to extract the response object
           is = conn.getInputStream();
        }
        else {
           is = conn.getErrorStream();
        }
        reader = new BufferedReader(new InputStreamReader(is, encode));
        while ((line = reader.readLine()) != null) {
            String text = new String(line.getBytes(encode));
            outputText = outputText + text;

        }
        
        reader.close();
        is.close();
        return outputText;
    }     
    
    
    
    public static String callHTTPSURLWithMethod(String aURL, String contents, int waitSeconds, String contentType,
            String method, String encoding) throws IOException {
        
        disableSslVerification();
        return callURLWithMethod(aURL, contents, waitSeconds, contentType, method, encoding);
        
    }
    
    public static boolean disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
               }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }

            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            return true;
            
        } catch (Exception  e) {
            
            return false;
        }
    } // end  disableSslVerification function


        
    
}
