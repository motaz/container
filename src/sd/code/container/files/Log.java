/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author motaz
 */
public class Log {
    
    
    public static void writeEvent(String eventText, String logname, String logdir) {
        
        
       try {  
              
            // Get current day
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            
	    // Default directory if it is null
            if (logdir == null) {
		if (Config.isUnixLike()) {
		    logdir = "/var/log/code";
		}
		else {
		    logdir = "log";
		}
	    }

	    // Remove last seperator from directory
	    if (logdir.substring(logdir.length()-1, logdir.length()).equals(File.separator)) {
			logdir = logdir.substring(0, logdir.length() -1);
			
	    }
	    
            // Get today file name
            String fileName;
            fileName = logdir + File.separator + logname + "-" + day + ".log";
            
            
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
            
	    // Write into log file
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(
                      new FileWriter(fileName, true)));
            out.println(today.toString() + " : " + eventText);  
            out.close();
            
              
        } catch (IOException e) {  
            System.out.println("Unable to write: " + eventText + ", in log file: " + e.toString());
        }     
    }

    
}
