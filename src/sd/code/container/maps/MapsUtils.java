/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.maps;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author motaz
 */
public class MapsUtils {
    
        public static Map<String, String> parseHashmap(String data, String lineSeperator) {
        
        String []items = data.split(lineSeperator);
        Map<String, String> map = new HashMap<>();
        for (String item: items){
            String name = item.substring(0, item.indexOf("="));
            String value = item.substring(item.indexOf("=")+1, item.length());
            map.put(name, value);
        }
        return map;
    }
    
}
