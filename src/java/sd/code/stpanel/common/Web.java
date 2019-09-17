/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author motaz
 */
public class Web {
    
    public static void setHeader(boolean displayTabs, HttpServletRequest request, HttpServletResponse response, 
            PrintWriter out, String parent, String page){
        
        String user = getCookieValue(request, "user");
        if (page == null){
            page = "";
        }
        
        if (user == null){
            user = "";
        }
        String logoutText =  user + "&emsp; <a href=Logout>Logout</a>";
        
        String selectedPBX = "";
        String fileName = "";
        if (displayTabs) {
            fileName = getCookieValue(request, "file");
            if (fileName != null) {
                String title = General.getConfigurationParameter("title", "", General.getPBXsDir() + fileName);
                selectedPBX = "<font color=green><b>" + title + "</b></font>";
            }
            
        }

        
        request.setAttribute("version", General.VERSION);
        request.setAttribute("selectedpbx", selectedPBX);
        request.setAttribute("logouttext", logoutText);
        
        try {
            request.getRequestDispatcher("header.jsp").include(request, response);
        } catch (Exception ex){
            General.writeEvent("Error in header.jsp: " + ex.toString());
        }

        // Tabs
        if (displayTabs) {
            String font = "style='color:lime;font-weight: bold;'";
            out.print("<li><a href=Home><font ");
            if (parent.equals("home")){
                out.print(font);
            }
            out.println(">Home</font></a></li>");
              
            out.print("<li><a href=Advanced><font ");
            if (parent.equals("advanced")){
                out.print(font);
            }
            out.println(">Advanced</font></a></li>");
              
              
            out.print("<li><a href='Pbx?sub=" + parent + "'><font ");
            if (parent.equals("pbx")){
                out.print(font);
            }
            out.println(">PBX</font></a></li>");
              
            out.println("<li><a href=ChangePassword><font ");
            if (parent.equals("myadmin")){
                out.print(font);
            }
            out.println(">My Admin</font></a></li>");
              
            out.println("&emsp;");
            displayPbxsBox(out, fileName);
             
        }
         
        out.println("</ul>\n" +
            "	</div>\n" +
            "	</div>\n" +
            "	<div class=\"content\">\n" +
            "	<div class=\"container\">\n" +
            "	<div class=\"main\">");
        
        if (parent.equals("advanced")) {
 
            advancedTab(page, out);
          
        }
        else if (parent.equals("pbx")) {
            pbxTab(page, out);
        }      
        out.println("<br/>");
    }

    private static void advancedTab(String page, PrintWriter out) {
        
        out.println("<table><tr bgcolor='#eeeecc'>");
        
        selectTabPage(out, page, "status");
        out.println("<a href='Status'>Status</a></td>");
        
        selectTabPage(out, page, "files");
        out.println("<a href='Files'>Files</a></td>");
        
        selectTabPage(out, page, "sip");
        out.println("<a href='SIPNodes'>SIP</a></td>");
        
        selectTabPage(out, page, "dialplan");
        out.println("<a href='Dialplan'>Dial plans</a></td>");
        
        selectTabPage(out, page, "commands");
        out.println("<a href='Commands'>CLI commands</a></td>");
        
        selectTabPage(out, page, "ami");
        out.println("<a href='AMI'>AMI commands</a></td>");
        
	
        selectTabPage(out, page, "terminal");
        out.println("<a href='Terminal'>Terminal</a></td>");
	
        selectTabPage(out, page, "logs");
        out.println("<a href='Logs'>Logs</a></td>");
        
        selectTabPage(out, page, "config");
        out.println("<a href='Config'>Configuration</a></td>");
	
        out.println("</tr></table>");
    }

    private static void pbxTab(String page, PrintWriter out) {
        
        out.println("<table><tr bgcolor='#eeeecc'>");
        
        selectTabPage(out, page, "ext");
        out.println("<a href='Extensions'>Extensions</a></td>");
        
        selectTabPage(out, page, "trunk");
        out.println("<a href='Extensions?type=trunk'>Trunks</a></td>");

        selectTabPage(out, page, "dialplans");
        out.println("<a href='Dialplans'>Dialplans</a></td>");
	
        selectTabPage(out, page, "functions");
        out.println("<a href='Functions'>Queues</a></td>");
	
        selectTabPage(out, page, "monitor");
        out.println("<a href='Monitor'>Monitor</a></td>");

	out.println("</tr></table>");
    }
    
    private static void selectTabPage(PrintWriter out, String page, String compare) {
        
        String selected = " bgcolor='#FFFFcc'";
        out.print("<td ");
        if (page.equals(compare)){
            out.println(selected);
        }
        out.println(">");
    }
   
    public static void setFooter(HttpServletRequest request, HttpServletResponse response){

        try {
            request.getRequestDispatcher("footer.jsp").include(request, response);
        } catch (Exception ex){
            General.writeEvent("Error in setFoot: " + ex.toString());
            
        }
       
    }
   
    public static String getCookieValue(HttpServletRequest request, String cookieName) {

        Cookie coo[];
        coo = request.getCookies();
    
        if (coo != null){
          for (Cookie coo1 : coo) {
            if (coo1.getName().equals(cookieName)) {
                return coo1.getValue();
            }
          }
        }
        return(null);
    }  
    
    public static boolean checkSession(HttpServletRequest request, String user) {
        
        boolean result;
        
        String spices = getCookieValue(request, "spices");
        String userAgent = request.getHeader("user-agent");
        String password = General.getConfigurationParameter("pass", "", null);
        String remoteAddress = request.getRemoteAddr();
        
        String currentSpices;

        currentSpices = General.getMD5(General.getMD5(userAgent + "7n1" + password +
                remoteAddress + "77") + "0066");
        result = currentSpices.equals(spices);
        
        return(result);
        
    }     
    
    public static void displayDialplans(HttpServletRequest request, String user, final PrintWriter out
	      ) throws IOException, ParseException {
	
        String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
        String url = General.getConfigurationParameter("url", "", pbxfile);
        JSONObject obj = new JSONObject();
        obj.put("filename", "extensions.conf");
        String requestText = obj.toJSONString();

        String resultText = General.restCallURL(url + "GetFile", requestText);
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
        if (Boolean.valueOf(resObj.get("success").toString())) {
            String content = resObj.get("content").toString();
            String[] arr = content.split("\n");

            ContextParser cparser = new ContextParser(arr, "extensions.conf");
            ArrayList<String> nodes = cparser.getNodes();

            String reverseStr = Web.getCookieValue(request, "reverse");
            boolean reverse = (reverseStr != null) && (reverseStr.equals("yes"));

            out.println("<table class=dtable>");
            //<tr><th>Node</th><th></th></tr>");


            int count = 0;
            out.println("<tr>");
            for (String node: nodes) {
                out.println("<td><a href='EditNode?filename=extensions.conf&nodename=" + node + "'>" +
                        node + "</a></td>");
                count++;
                if (count % 6 == 0) {
                    out.println("</tr><tr>");

                }
            }

            out.println("</tr>");

            out.println("</table>");
		
	}
    }    
      
    public static void addNewNode(String pbxfile, HttpServletRequest request, String fileName, String nodename, String content, final PrintWriter out) 
	    throws ParseException, IOException {
	
	    String url = General.getConfigurationParameter("url", "", pbxfile);	    
            JSONObject saveobj = new JSONObject();
            saveobj.put("filename", fileName);
            saveobj.put("nodename", nodename);
            saveobj.put("content", content);
            String requestText = saveobj.toJSONString();
            String resultText = General.restCallURL(url + "AddNode", requestText);
            JSONParser saveparser = new JSONParser();
            JSONObject saveresObj = (JSONObject) saveparser.parse(resultText);
            boolean res = (Boolean.valueOf(saveresObj.get("success").toString()));
            if (res) {
                out.println("<p class=infomessage>New node " + nodename + " has been added</p>");
            }
            else {
                out.println("<p class=errormessage>Error: " + saveresObj.get("message").toString()
			  + "</p>");
            }
    }      
    
    public static String callAMICommand(String pbxfile, String command){
	try {
              String url = General.getConfigurationParameter("url", "", pbxfile);
              JSONObject obj = new JSONObject();
              String username = General.getConfigurationParameter("amiuser", "admin", pbxfile);
              String secret = General.getConfigurationParameter("amipass", "", pbxfile);
              obj.put("username", username);
              obj.put("secret", secret);
              obj.put("command", "action:command\ncommand:" + command);	

              String requestText = obj.toJSONString();

              String resultText = General.restCallURL(url + "CallAMI", requestText);
              JSONParser parser = new JSONParser();
              JSONObject resObj = (JSONObject) parser.parse(resultText);

              String content = resObj.get("message").toString();
              return content;
	    }
	     catch (Exception ex){
	           return null;
	    }
	
    }
    
    public static void displayPbxsBox(final PrintWriter out, String selected){
              
        File folder = new File(General.getPBXsDir());
        File[] listOfFiles = folder.listFiles();
        
        out.println("<select name=pbx onchange='selectPBX(this)'>");
        out.println("<option >--Select PBX--</option>");
        for (File file: listOfFiles){
            if (file.isFile()) {
                String title = General.getConfigurationParameter("title", "", file.getAbsolutePath());            
                out.println("<option value='" + file.getName() + "' ");
                if (file.getName().equals(selected)){
                    out.println("selected");
                }
                out.println(">" + title);
                out.println("</option>");
            }
        }
        out.println("</select>");
        out.println("<script>");
        out.println("function selectPBX(sel) {\n" +
                    "   location='SelectPBX?pbx=' + sel.value;  \n" +
                    "}");
        out.println("</script>");
    }
    
    public static void displayIncludedFiles(String content, final PrintWriter out, String action) {

        String[] arr = content.split("\n");

        for (String line: arr){
            if ((line.contains("#include")) &&
                    ((!line.contains(";")) || (line.indexOf(";") > line.indexOf("#include")))) {
                line = line.substring(line.indexOf(" ", line.indexOf("#include")), line.length()).trim();
                out.println("<a href='" + action + line + "'>" + line + "</a><br/>");
            }
        }
    }
    
    public static void displayReloadLink(String fileName, final PrintWriter out) {
        
        if (fileName.equals("extensions.conf") || fileName.equals("sip.conf")) {
            String command = "dialplanreload";
            String caption = "Reload Dialplan";
            if (fileName.equals("sip.conf")){
                command = "sipreload";
                caption = "Reload SIP";
            }
            out.println("<p class=warnmessage>");
            out.println("<a href='Commands?command=" + command + "&ret=1'>" + caption + "</a></br></p>");
        }
    }
     
}
