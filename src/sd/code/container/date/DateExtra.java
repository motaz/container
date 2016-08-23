/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.date;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author motaz
 */
public class DateExtra {
    
    private static int getCalendarValue(Date aday, int tag){
	
      Calendar cal = Calendar.getInstance();
      cal.setTime(aday);
      return cal.get(tag);	
    }
    
    public static int getDay(Date aday){
	
	return getCalendarValue(aday, Calendar.DAY_OF_MONTH);
    }
    
    public static int getMonth(Date aday){
	
      return getCalendarValue(aday, Calendar.MONTH) +1;
      
    }
    
    public static int getYear(Date aday){
	
      return getCalendarValue(aday, Calendar.YEAR);
      
    }
    
    
    
}
