/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.container.formats;

import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author motaz
 */
public class XML {
    
    public static Node getNodeByName(String nodeName, NodeList list) {
	
	Node aNode = null;
	nodeName = nodeName.toLowerCase();
	
       for (int i=0; i < list.getLength(); i++) {
	   if (list.item(i).getNodeName().toLowerCase().equals(nodeName)){
	       aNode = list.item(i);
	       break;
	   }
       
       }
       return aNode;
    }
    
    public static ArrayList<Node> getNodesListByName(String nodeName, NodeList list) {
	
	ArrayList<Node> nodes = new ArrayList<>();
	nodeName = nodeName.toLowerCase();
	
       for (int i=0; i < list.getLength(); i++) {
	   if (list.item(i).getNodeName().toLowerCase().equals(nodeName)){
	       nodes.add(list.item(i));
	   }
       
       }
       return nodes;
    }    
    
}
