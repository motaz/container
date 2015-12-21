/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author code
 */
final public class General {
    
    
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
            if ((logname == null) || (logname.isEmpty())) {
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
         if ((aFile == null) || (aFile.isEmpty())){
             
           aFile = dir + "simpletrunk.ini";
         }
         else
         {
            if (!aFile.contains("/")){
                 aFile = dir + aFile;
            }
             
         }
         
         String text;
           try (FileInputStream stream = new FileInputStream(aFile)) {
               prop.load(stream);
               text = prop.getProperty(parameterName, defaultValue);
           }
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
         if ((aFile == null) || (aFile.isEmpty())){
             
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
         
           try (FileInputStream stream = new FileInputStream(aFile)) {
               prop.load(stream);
               prop.setProperty(parameterName, aValue);
             try (FileOutputStream output = new FileOutputStream(aFile)) {
                 prop.store(output, "STPanel");
             }
           }
         return(true);
         
       }
       catch (IOException ex) {
         General.writeEvent("Error in setConfiguration: " + ex.toString(), null);
         return(false);
       }
     
    }  
    
    public static String getPBXsDir() {
        // Create new file
        
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
        conn.setRequestProperty("Content-Type", "text/json");
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
    
    public static String restCallURL(String methodURL, String urlParameters, int waitSeconds) throws IOException, MalformedURLException {
    
        URL url = new URL(methodURL);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(waitSeconds * 1000);            
        conn.setReadTimeout(waitSeconds * 1000);        
        conn.setRequestProperty("Content-Type", "text/json");
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