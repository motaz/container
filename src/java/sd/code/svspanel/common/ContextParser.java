/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.common;

import java.util.ArrayList;

/**
 *
 * @author motaz
 */
public class ContextParser {
    
    String contents[];
    String file;
    
    public ContextParser(String lines[], String fileName) {
        
        file = fileName;
        contents = lines;
        
    }
    
    public ArrayList<String> getNodes(){
        
        ArrayList<String> nodes = new ArrayList<>();
        
        for (String line: contents){
            line = line.trim();
            if ((line.indexOf("[") == 0) &&  (line.indexOf("]")> 2)){
                line = line.substring(0, line.indexOf("]") + 1);
                nodes.add(line);
                
            }
        }
        
        return nodes;
        
    }
    
}
