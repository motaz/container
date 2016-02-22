/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.common;

import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author motaz
 */
public class Web {
    
    public static void setHeader(boolean displayTabs, HttpServletRequest request, HttpServletResponse response, PrintWriter out, 
            String parent, String page){
        
        String user = getCookieValue(request, "user");
        if (page == null){
              page = "";
            }

        String version  = "1.0.3";
        
        if (user == null){
            user = "";
        }
        String logoutText =  user + "&emsp; <a href=Logout>Logout</a>";
        
        String selectedPBX = "";
        if (displayTabs) {
              String fileName = getCookieValue(request, "file");
              if (fileName != null) {
                 String title = General.getConfigurationParameter("title", "", General.getPBXsDir() + fileName);
                 selectedPBX = "<font color=lime><b>" + title + "</b></font>";
              }
            
        }
        
        out.println("<html lang=\"en\">\n" +
            "	<head>\n" +
            "		<meta charset=\"utf-8\">\n" +
            "		<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "		<title>Simple Trunk Panel</title>\n" +
            "		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "		<meta name=\"Description\" lang=\"en\" content=\"ADD SITE DESCRIPTION\">\n" +
            "		<meta name=\"author\" content=\"ADD AUTHOR INFORMATION\">\n" +
            "		<meta name=\"robots\" content=\"index, follow\">\n" +
            "\n" +
            "		<!-- icons -->\n" +
            "		<link rel=\"apple-touch-icon\" href=\"img/apple-touch-icon.png\">\n" +
            "		<link rel='shortcut icon' href='img/icon.png'>\n" +
            "\n" +
            "		<!-- Override CSS file - add your own CSS rules -->\n" +
            "		<link rel=\"stylesheet\" href=\"css/styles.css\">\n" +
            "	</head>\n" +
            "	<body>\n" +
            "	<div class=header>\n" +
            "	<div class=container>\n" +
            "<table ><tr bgcolor=#888888><td class=titletd><img src='img/title.jpg' /> </td>" + 
            "<td class=titletd><table class=titletable><tr  bgcolor=#888888><td class=titletd>" +
            "&emsp;" + logoutText + "&emsp;</td></tr><tr  bgcolor=#888888><td>" + 
                selectedPBX + "</td></tr></table>" +
            "</td><td bgcolor=#777777 style=vertical-align:bottom>Version " +
                version + "</td></tr></table>	</div>\n" +
            "	</div>\n" +
            "	<div class=\"nav-bar\">\n" +
            "	<div class=\"container\">\n" +
            "	<ul class=\"nav\"> ");

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
              
              
              out.print("<li><a href=Pbx?sub=" + parent + "><font ");
              if (parent.equals("pbx")){
                  out.print(font);
              }
              out.println(">PBX</font></a></li>");
              
              out.println("<li><a href=ChangePassword><font ");
              if (parent.equals("myadmin")){
                  out.print(font);
              }
              out.println(">My Admin</font></a></li>");
             
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
        
        
    }

    private static void advancedTab(String page, PrintWriter out) {
        
        out.println("<table><tr bgcolor='#AADDCC'>");
        
        selectTabPage(out, page, "status");
        out.println("<a href='Status'>Status</a></td>");
        
        selectTabPage(out, page, "files");
        out.println("<a href='Files'>Files</a></td>");
        
        selectTabPage(out, page, "sip");
        out.println("<a href='SIPNodes'>SIP</a></td>");
        
        selectTabPage(out, page, "dialplan");
        out.println("<a href='Dialplan'>Dial plan</a></td>");
        
        selectTabPage(out, page, "commands");
        out.println("<a href='Commands'>CLI commands</a></td>");
        
        selectTabPage(out, page, "ami");
        out.println("<a href='AMI'>AMI commands</a></td>");
        
        selectTabPage(out, page, "logs");
        out.println("<a href='Logs'>Logs</a></td>");
        
        selectTabPage(out, page, "tools");
        out.println("<a href='Tools'>Tools</a></td>");

        out.println("</tr></table>");
    }

    private static void pbxTab(String page, PrintWriter out) {
        
        out.println("<table><tr bgcolor='#AADDCC'>");
        
        selectTabPage(out, page, "extensions");
        out.println("<a href='Extensions'>Extensions</a></td>");
        
        selectTabPage(out, page, "trunks");
        out.println("<a href='Extensions?type=trunk'>Trunks</a></td>");

        out.println("</tr></table>");
    }
    
    private static void selectTabPage(PrintWriter out, String page, String compare) {
        
        String selected = " bgcolor='#99ccBB'";
        out.print("<td ");
        if (page.equals(compare)){
            out.println(selected);
        }
        out.println(">");
    }
   
   public static void setFooter(PrintWriter out){

       out.println("	</div>\n" +
                   "	</div>\n" +
                   "	</div>\n" +
                   "	<div class=\"footer\">\n" +
                   "		<div class=\"container\">\n" +
                   "			&copy; Code for computer software 2015 <img src='img/small-code.png' />\n" +
                   "		</div>\n" +
                   "	</div>\n" +
                   "  </body>\n" +
                   "</html>");
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
}
