/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@WebServlet(name = "AMIConfig", urlPatterns = {"/AMIConfig"})
public class AMIConfig extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
          try{
          String user = Web.getCookieValue(request, "user");
          String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
           String url = General.getConfigurationParameter("url", "", pbxfile);
          if (Web.checkSession(request, user)) {
              Web.setHeader(true, request, response, out, "advanced", "config");
              out.println("<h2>AMI Configurations</h2>");
              if(paramStatus(request)){
                  displayAMIUsers(out, url,pbxfile);
                  displayAMIStatus(out,url);
              }
              if(request.getParameter("adf")!=null)
              {
                  addAMIUserForm(out);
              }
              if(request.getParameter("edf")!=null)
              {
                  editAMIUserForm(out, url, request.getParameter("edf"));
              }
              if(request.getParameter("aok")!=null)
              {
                  doAddAMIUser(request, response, out, url);
              }
              if(request.getParameter("mok")!=null)
              {
                  doModAMIUser(request, response, out, url);
              }
              if(request.getParameter("def")!=null)
              {
                  setDefault(out, url, pbxfile,response,request.getParameter("def"));
              }
          }
          else
              response.sendRedirect("Login");
          Web.setFooter(out);
          }catch(Exception e){
             out.println("<p class=errormessage >Error : "+e.toString()+"</p>");
          }
        }
    }
    private void displayAMIStatus(PrintWriter out,String url) throws Exception
    {
              String spl[];
              String ami,amiht,http;
              String Requestres = General.restCallURL(url + "GetAMIStatus", "");
              JSONParser parser = new JSONParser();
              JSONObject pres = (JSONObject) parser.parse(Requestres);
              boolean success=Boolean.valueOf(pres.get("success").toString());
              if(success){
                  String res=pres.get("result").toString();
                  spl=res.split(":");
                  ami=spl[0];
                  amiht=spl[1];
                  http=spl[2];
                    out.println("<h4>AMI Status</h4>");
                  if(ami.equals("ok")&&ami.equals("ok")){
                        out.println("<p style=margin-left:20px >AMI Status:<span class=infomessage> OK </span></p>");
                 }else{
                        out.println("<p style=margin-left:20px >AMI Status:<span class=errormessage> AMI or AMI over Http is disabled </span></p>");
                  }
                  if(http.equals("ok")){
                       out.println("<p style=margin-left:20px >Asterisk HTTP Server Status:<span class=infomessage> OK </span></p>");
                  }else{
                       out.println("<p style=margin-left:20px >Asterisk HTTP Server Status:<span class=errormessage> Disabled </span></p>");
                  }
              }
              else
                  out.println("<p class=errormessage >Error: "+pres.get("message").toString()+"</p>");
         }
     private void displayAMIUsers(PrintWriter out,String url,String pbxfile) throws Exception
     {
         String spl[],spl1[],user;
          String resultText = General.restCallURL(url + "GetAMIUsersInfo", "");
          JSONParser parser = new JSONParser();
          JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                    {
                        String result=res.get("result").toString();
                     
                        if(!result.equals("")){
                        spl=result.split(";");
                        out.println("<h4>AMI Users</h4>"
                                + "<a href=AMIConfig?adf=yes>Add New User</a>");
                        out.println("<table style=margin-top:10px class=dtable><tr><th>AMI User</th><th>AMI Secret</th><th>AMI Read Permission</th><th>AMI Write Permission</th><th>Edit</th><th>Default</th></tr>");
                        for(int i=0;i<spl.length;i++){
                            spl1=spl[i].split(":");
                            user=spl1[0].replace("[","");
                            user=user.replace("]", "");
                            out.println("<tr><td>"+user+"</td><td>"+spl1[1]+"</td><td>"+spl1[2]+"</td><td>"+spl1[3]+"</td><td><a href=AMIConfig?edf="+spl1[0]+"> Edit</a></td><td>"+ getDefault(user, spl1[1], pbxfile) +"</td></tr>");
                         }
                         out.println("</table><hr/>");
                        }else{
                            out.println("<p class=infomessage >There is no AMI User <a href=AMIConfig?adf=yes>Add AMI User</a></p>");
                        }
                    }
                    else
                        out.println("<p class=errormessage >Error : "+res.get("message").toString()+"</p>");
     }
     private void addAMIUserForm(PrintWriter out) {
         out.print("<h4>Add New AMI User</h4>");
        out.println("<form method=POST >");
        out.println("<table>");
        out.println("<tr><td>AMI Username </td><td><input type=text name=user size=30 required/></td></td>");
	
        out.println("<tr><td>AMI Secret</td>");
        out.println("<td><input type=passwoed name=sec size=30 required/></td></tr>");
	
        out.println("<tr><td>AMI Read Permission </td>");
        out.println("<td><input type=text name=read size=30 value=all required/></td></td>");
		
        out.println("<tr><td>AMI Write Permission</td>");
        out.println("<td><input type=text name=write size=30 value=all required/></td></tr>");
        out.println("<tr><td>AMI Aditional Configuration:</td>");
        out.println("<td><textarea rows = 5 cols=40 name=addi />");
	
	out.println("deny=0.0.0.0/0.0.0.0\npermit=127.0.0.1/255.255.255.0");
	out.println("</textarea></td></tr>");
        
        out.println("<tr><td><input type=submit name=aok value=OK required/></td></tr>");
        out.println("</table>");
        out.println("</form>");
        
    }
     private String getDefault(String user,String pass,String pbxfile)
     {
         String res="";
         String suser=General.getConfigurationParameter("amiuser", "",pbxfile);
         String spass=General.getConfigurationParameter("amipass", "",pbxfile);
         if(suser.equals(user)&&spass.equals(pass)){
             res="Default";
         }
         else
             res="<a href=AMIConfig?def=["+user+"]>Set Default</a>";
  
         return res;
     }
     private void setDefault(PrintWriter out,String url,String pbxfile,HttpServletResponse response,String uname) throws Exception
     {
        String spl[],user;
         JSONObject obj=new JSONObject();
                    obj.put("Username", uname);
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "GetAMIUserInfo", requestText);
          JSONParser parser = new JSONParser();
          JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                    {
                         String result=res.get("result").toString();
                        spl=result.split(":");
                        user=spl[0].replace("[", "");
                        user=user.replace("]", "");
                        General.setConfigurationParameter("amiuser", user, pbxfile);
                        General.setConfigurationParameter("amipass", spl[1], pbxfile);
                        response.sendRedirect("AMIConfig");
                    }else
                        out.println("<p class=errormessage >Error: "+res.get("message").toString()+"</p>");
     }
     private void editAMIUserForm(PrintWriter out,String url,String uname) throws Exception 
     {
         String spl[],user;
         JSONObject obj=new JSONObject();
                    obj.put("Username", uname);
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "GetAMIUserInfo", requestText);
          JSONParser parser = new JSONParser();
          JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                    {
                         String result=res.get("result").toString();
                        spl=result.split(":");
                        user=spl[0].replace("[", "");
                        user=user.replace("]", "");
         out.print("<h4>Edit "+user+" </h4>");
        out.println("<form method=POST >");
        out.println("<table>");
        out.println("<tr><td>AMI Username </td><td><input type=text name=user value="+user+"  size=30 required/></td></td>");
	
        out.println("<tr><td>AMI Secret</td>");
        out.println("<td><input type=passwoed name=sec value="+spl[1]+" size=30 required/></td></tr>");
	
        out.println("<tr><td>AMI Read Permission </td>");
        out.println("<td><input type=text name=read size=30 value="+spl[2]+" required/></td></td>");
		
        out.println("<tr><td>AMI Write Permission</td>");
        out.println("<td><input type=text name=write size=30 value="+spl[3]+" required/></td></tr>");
        out.println("<tr><td>AMI Aditional Configuration:</td>");
        out.println("<td><textarea rows = 5 cols=40 name=addi />");
	
	out.println(spl[4]);
	out.println("</textarea></td></tr>");
        
        out.println("<tr><td><input type=submit name=mok value=OK required/></td><td><input type=hidden name=cuser value="+spl[0]+" required/></td></tr>");
        out.println("</table>");
        out.println("</form>");
                    }else
                        out.println("<p class=errormessage >Error: "+res.get("message").toString()+"</p>");
    }
    private void doAddAMIUser(HttpServletRequest request,HttpServletResponse response,PrintWriter out,String url)throws Exception
    {               
                    JSONObject obj=new JSONObject();
                    obj.put("Username", request.getParameter("user"));
                    obj.put("Secret", request.getParameter("sec"));
                    obj.put("Read", request.getParameter("read"));
                    obj.put("Write", request.getParameter("write"));
                    obj.put("Addi", request.getParameter("addi"));
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "AddAMIUser", requestText);
                    JSONParser parser = new JSONParser();
                    JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                        response.sendRedirect("AMIConfig");
                    else
                    {
                        out.println("<p class=errormessage >Error in Adding AMI User: "+res.get("message").toString()+"</p>"); 
                    }
              
    }
     private void doModAMIUser(HttpServletRequest request,HttpServletResponse response,PrintWriter out,String url)throws Exception
    {               
                    JSONObject obj=new JSONObject();
                    obj.put("Username", request.getParameter("cuser"));
                    obj.put("NUsername", request.getParameter("user"));
                    obj.put("Secret", request.getParameter("sec"));
                    obj.put("Read", request.getParameter("read"));
                    obj.put("Write", request.getParameter("write"));
                    obj.put("Addi", request.getParameter("addi"));
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "ModifyAMIUser", requestText);
                    JSONParser parser = new JSONParser();
                    JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                        response.sendRedirect("AMIConfig");
                    else
                    {
                        out.println("<p class=errormessage >Error in Adding AMI User: "+res.get("message").toString()+"</p>"); 
                    }
              
    }
     
    private boolean paramStatus(HttpServletRequest request){
        return request.getParameter("adf")==null&& request.getParameter("edf")==null;
    }              
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
              
    }