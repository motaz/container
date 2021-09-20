/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.types.Operation;

/**
 *
 * @author code
 */
final public class General {
    
    public static final String VERSION = "1.0.34";
    
    public static void writeEvent(String eventText){
        
        writeEvent(eventText, null);
    }
        /**
     * write event text in /var/log/simpletrunk/simpletrunk- day number
     * it recycles day numbers
     * @param eventText 
     * @param logname 
     */
    public static void writeEvent(String eventText, String logname) {
        
        
       try {  
              
            // Get current day
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            
            // Get today file name
            String fileName;
 
            String logdir = "log";
            if (System.getProperty("os.name").toLowerCase().contains("linux")){
              logdir = "/var/log/simpletrunk";
            }
            if ((logname == null) || (logname.equals(""))) {
                fileName = logdir + "/simpletrunk-" + day + ".log";
            }
            else {
                fileName = logdir + "/" + logname + "-" + day + ".log";
            }
            
            // Check log directory existance
            File dir = new File(logdir);
            
            if (!dir.exists()){
                dir.mkdir();
            }            
            
            File file = new File(fileName);
            
            // Recycle file after 1 month
            if (file.exists()) {
                
                Date fileDate = new Date();
                Date yesterday = new Date();
                yesterday.setTime(today.getTime() - 1000 * 60 * 60 * 24);
                fileDate.setTime(file.lastModified());
                
                // Delete old file
                if (fileDate.before(yesterday)) {
                    file.delete();
                }
                
            }
            
            PrintWriter out;
              out = new PrintWriter(new BufferedWriter(
                      new FileWriter(fileName, true)));
            out.println(today.toString() + " : " + eventText);  
            out.close();
            
              
        } catch (IOException e) {  
            System.out.println("Unable to write: " + eventText + ", in log file: " + e.toString());
        }     
    }

    public static String getConfigurationParameter(String parameterName, String defaultValue, String aFile) {
        
       Properties prop  = new Properties();
       try

       {
         String dir = "";
         if (System.getProperty("os.name").toLowerCase().contains("linux")){
              dir = "/etc/simpletrunk/";
         } 
         if ((aFile == null) || (aFile.equals(""))){
             
           aFile = dir + "simpletrunk.ini";
         }
         else
         {
            if (!aFile.contains("/")){
                 aFile = dir + aFile;
            }
             
         }
         
         String text;
         FileInputStream stream = new FileInputStream(aFile);
         prop.load(stream);
         text = prop.getProperty(parameterName, defaultValue);
	 stream.close();
           
         return(text);
         
       }
       catch (IOException ex) {
         General.writeEvent("Error in getConfiguration: " + ex.toString(), null);
         return(defaultValue);
       }
     
    }  
    
 
    public static boolean setConfigurationParameter(String parameterName, String aValue, String aFile) {
        
       Properties prop  = new Properties();
       try
       {
           
         String dir = "";
         if (System.getProperty("os.name").toLowerCase().contains("linux")){
              dir = "/etc/simpletrunk/";
         } 
         File directory = new File(dir);
         if (!directory.exists()){
             directory.mkdir();
         }
         if ((aFile == null) || (aFile.equals(""))){
             
           aFile = dir + "simpletrunk.ini";
         }
         else
         {
             if (!aFile.contains("/")){
                 aFile = dir + aFile;
             }
         }
         
         File confFile = new File(aFile);
         if (! confFile.exists()){
             confFile.createNewFile();
         }
         
           FileInputStream stream = new FileInputStream(aFile);
	   
               prop.load(stream);
               prop.setProperty(parameterName, aValue);
	       stream.close();
               FileOutputStream output = new FileOutputStream(aFile);
               prop.store(output, "STPanel");
               output.close();
           
         return(true);
         
       }
       catch (IOException ex) {
         General.writeEvent("Error in setConfiguration: " + ex.toString(), null);
         return(false);
       }
     
    }  
    
    public static String getPBXsDir() {
        
        String dir = "pbxs/";
        if (System.getProperty("os.name").toLowerCase().contains("linux")){
            dir = "/etc/simpletrunk/pbxs/";
        }
        File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdirs();
        }
        return dir;
    }  
    
   public static String getMD5(String pass) {
       
    try {
          MessageDigest m = MessageDigest.getInstance("MD5");
          byte[] data = pass.getBytes();
          m.update(data, 0, data.length);

          BigInteger i = new BigInteger(1,m.digest());
          return (String.format("%1$032X", i).toLowerCase());
        }
        catch (NoSuchAlgorithmException ex) {
            writeEvent("Error in GetMD5: "  + ex.toString(), "");
            return(null);
        }
    }
   
    public static String restCallURL(String methodURL, String urlParameters) throws IOException, MalformedURLException {
    
        URL url = new URL(methodURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(12 * 1000);            
        conn.setReadTimeout(12 * 1000);  
        conn.setRequestProperty("Content-Type", "text/json");
        conn.setDoOutput(true);
        String outputText = "";
        BufferedReader reader;
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	writer.write(urlParameters);

	writer.flush();
	String line;

	reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	while ((line = reader.readLine()) != null) {

	    outputText = outputText + line;

	}
	writer.close();
        
        reader.close();
        return outputText;
    } 
    
    public static String restCallURL(String methodURL, String urlParameters, int waitSeconds) throws IOException, MalformedURLException {
    
        URL url = new URL(methodURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", "text/json");
        conn.setDoOutput(true);
        String outputText = "";
        BufferedReader reader;
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	writer.write(urlParameters);

	writer.flush();
	String line;

	reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	while ((line = reader.readLine()) != null) {

	    outputText = outputText + line;

	}
	writer.close();
        
        reader.close();
        return outputText;
    }     

   public static Operation downloadFile(String fileURL, String urlParameters, String contentType, OutputStream outputStream)
            throws IOException, ParseException {
        
        Operation op = new Operation();
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Content-Type", contentType);
        httpConn.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write(urlParameters);
        General.writeEvent("URL " + fileURL);
        General.writeEvent("Parameters: " + urlParameters);
        writer.flush();        
        writer.close();
        int responseCode = httpConn.getResponseCode();

        String result = "";
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
          
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            //String saveFilePath = filePath;
             
            // opens an output stream to save into file
           // outputStream = new FileOutputStream(saveFilePath);
 
            long size = 0;
            int bytesRead;
            byte[] buffer = new byte[1024];
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                size = size + bytesRead;
            }
            if (size < 2048) {
                byte[] text = new byte[(int)size];
                System.arraycopy(buffer, 0, text, 0, (int)size);
                String str = new String(text);
                result = result + str;

            }
            
 
            outputStream.close();
            inputStream.close();
 
            op.success = size > 2048;
            op.size = size;
            
            if (!op.success) {
                try {
                  JSONParser parser = new JSONParser();
                  JSONObject obj = (JSONObject)parser.parse(result);
                  op.message = obj.get("message").toString();
                }
                catch (Exception ex){
                    op.success = false;
                    op.errorCode = 5;
                    op.message = "Error while parsing result: " + ex.toString() ;
                    General.writeEvent("Error : " + ex.toString());
                }
            }
        } else {
            op.success = false;
            op.errorCode = 5;
            op.message = "HTTP download Error";
            General.writeEvent("HTTP error: " + responseCode);
        }
        httpConn.disconnect();
        return op;
    } 
   
    public static String executeShell(String command, String url) {
	try {
	    
	    JSONObject obj = new JSONObject();

	    obj.put("command", command);

	    String requestText = obj.toJSONString();

	    String resultText = General.restCallURL(url + "Shell", requestText);
	    JSONParser parser = new JSONParser();
	    JSONObject resObj = (JSONObject) parser.parse(resultText);

	    String content = resObj.get("result").toString();
	    
	    if (content != null){
	    }
  	    return content;
	} catch (Exception ex){
	    return "Error: " + ex.toString();
	}
    }
    
     public static String[] getCallInfo(String pbxfile, String callid) throws IOException, ParseException{
	

	String text = Web.callAMICommand(pbxfile, "core show channel " + callid);
	String lines[];
	    
	lines = text.split("\n");
    
	return lines;
    }  
     
    public static String getValue(String text){
     
	return text.substring(text.indexOf(":") + 1, text.length()).trim();
    }
 
    public static String getRemoteFile(String url, String filename) throws IOException, ParseException {
	
	JSONObject obj = new JSONObject();
	obj.put("filename", filename);
	String requestText = obj.toJSONString();

	String resultText = General.restCallURL(url + "GetFile", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    String text = resObj.get("content").toString();
	    return text;
	}
	else {
	    return "Error: " + resObj.get("message").toString();
	}
    }
    
    public static String listFiles(String url, String folderName) throws IOException, ParseException {
	
	JSONObject obj = new JSONObject();
	folderName = addSlash(folderName);
	obj.put("foldername", folderName);
	String requestText = obj.toJSONString();

	String resultText = General.restCallURL(url + "ListFiles", requestText);
	JSONParser parser = new JSONParser();
        if (resultText != null && resultText.contains("{")){
            JSONObject resObj = (JSONObject) parser.parse(resultText);
            if (Boolean.valueOf(resObj.get("success").toString())) {
                String files = "";
                if (resObj.get("files") != null) {
                    files = resObj.get("files").toString();
                }
                return files;
            }
            else {
                return "Error: " + resObj.get("message").toString();
            }
        } else {
            return "";
        }
    }

    public static String addSlash(String folderName) {
	if (folderName.charAt(folderName.length()-1) != '/') {
	    folderName = folderName + "/";
	}
	return folderName;
    }
    
    public static String removeSlash(String folderName) {
	if (folderName.charAt(folderName.length()-1) == '/') {
	    folderName = folderName.substring(0, folderName.length() -1);
	}
	return folderName;
    }
    
    public static Operation saveRemoteFile(String url, String filename, String content) throws IOException, ParseException {
	
	Operation result = new Operation();
	JSONObject obj = new JSONObject();
	obj.put("filename", filename);
	obj.put("content", content);
	String requestText = obj.toJSONString();

	String resultText = General.restCallURL(url + "ModifyFile", requestText);
	JSONParser parser = new JSONParser();
	JSONObject resObj = (JSONObject) parser.parse(resultText);
	if (Boolean.valueOf(resObj.get("success").toString())) {
	    result.success = true;
	}
	else {
	    result.success = false;
	    result.errorCode = 5;
	    result.message = resObj.get("message").toString();
	}
	return result;
    }
    
    
}