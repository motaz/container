/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author motaz
 */
public class Config {
    
    static private String lastError;
    
    public static boolean isUnixLike(){
	
	return File.separator.equals("/");
    }
    
    static public String getLastError(){
        return lastError;
    }
    
    public static String getConfigurationParameter(String parameterName, String defaultValue, String aFile) {
        
       Properties prop  = new Properties();
       try

       {
         if ((!aFile.contains(File.separator)) && isUnixLike()){
	       aFile = "/etc/code/" + aFile;
	 }

         
         String text;
	 try (FileInputStream stream = new FileInputStream(aFile)) {
	      prop.load(stream);
	      text = prop.getProperty(parameterName, defaultValue);
	 }
           
         return(text);
         
       }
       catch (IOException ex) {
         lastError = "Error in getConfiguration: " + ex.toString();
         return(defaultValue);
       }
     
    }  
    
 
    public static boolean setConfigurationParameter(String parameterName, String aValue, String aFile) {
        
       Properties prop  = new Properties();
       try
       {
           
         if (!aFile.contains(File.separator)) {
                 if ( isUnixLike()){
	       aFile = "/etc/code/" + aFile;
            }
            else { // Windows: c:\\users\\currentuser\\code
               aFile =  System.getProperty("user.home") + "\\code\\" + aFile;

                 }
         }
         
	 // Check file existence
         File confFile = new File(aFile);
         if (! confFile.exists()){
             confFile.createNewFile();
         }
         
	 try (FileInputStream stream = new FileInputStream(aFile)) {
	       prop.load(stream);
	       prop.setProperty(parameterName, aValue);
	 }
	 
	 try (FileOutputStream output = new FileOutputStream(aFile)) {
	       prop.store(output, "settings");
	 }
           
         return(true);
         
       }
       catch (IOException ex) {
          lastError = "Error in setConfiguration: " + ex.toString();
          return(false);
       }
     
    }  
    
}
