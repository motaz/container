/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.files;

import java.io.File;

/**
 *
 * @author motaz
 */
public class Config {
    
    public static boolean isUnixLinx(){
	
	return File.separator.equals("/");
    }
    
}
