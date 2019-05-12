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
 * @author MOS
 */
@WebServlet(name = "CDRConfig", urlPatterns = {"/CDRConfig"})
public class CDRConfig extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
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
          if (Web.checkSession(request, user)) {
              Web.setHeader(true, request, response, out, "advanced", "config");
              out.println("<h2>CDR Configurations</h2>");
              String url = General.getConfigurationParameter("url", "", pbxfile);
              String checkRes = General.restCallURL(url + "IsCDRConf", "");
              JSONParser checkPar = new JSONParser();
              JSONObject chres = (JSONObject) checkPar.parse(checkRes);
              boolean chsuccess = (Boolean.valueOf(chres.get("success").toString()));
              JSONObject csres=checkCDRStatus(url);
              boolean cssuccess= (Boolean.valueOf(csres.get("success").toString()));
              if( (!chsuccess && !cssuccess) && paramStatus(request))
              {
                  out.println("<p class=errormessage > CDR Not Configure yet <a href=CDRConfig?cf=yes> click to configure</a></p>");
              }
              if ( (chsuccess || cssuccess) && paramStatus(request)){
                  displayConf(out,url);
                  displayCDRStatus(out, url);
              }
              if(request.getParameter("cf")!=null)
              {
                  cdrConfigForm(out);
              }
              if(request.getParameter("edit")!=null)
              {
                  editCDRConfigForm(out, url);
              }
              if(request.getParameter("cok")!=null)
              {     
                    
                    String ser=request.getParameter("ser");
                    String dbuname=request.getParameter("duname");
                    String dbpass=request.getParameter("dpass");
                    String dname=request.getParameter("dname");
                    String cdrtab=request.getParameter("cdrtab");
                    String keyf=request.getParameter("key");
                    JSONObject obj=new JSONObject();
                    obj.put("Server", ser);
                    obj.put("Duname", dbuname);
                    obj.put("Dpass", dbpass);
                    obj.put("Dname", dname);
                    obj.put("Ctab", cdrtab);
                    obj.put("Ckey", keyf);
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "SetCDRConf", requestText);
                    JSONParser parser = new JSONParser();
                    JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                        response.sendRedirect("CDRConfig");
                    else
                    {
                        out.println("<p class=errormessage >Error in CDR configuration: "+res.get("message").toString()+"</p>");
                    }
              }
              if(request.getParameter("mcok")!=null)
              {     
                    String ser=request.getParameter("ser");
                    String dbuname=request.getParameter("duname");
                    String dbpass=request.getParameter("dpass");
                    String dname=request.getParameter("dname");
                    String cdrtab=request.getParameter("cdrtab");
                    String keyf=request.getParameter("key");
                    JSONObject obj=new JSONObject();
                    obj.put("Server", ser);
                    obj.put("Duname", dbuname);
                    obj.put("Dpass", dbpass);
                    obj.put("Dname", dname);
                    obj.put("Ctab", cdrtab);
                    obj.put("Ckey", keyf);
                    String requestText = obj.toJSONString();
                    String resultText = General.restCallURL(url + "ModifyCDRConf", requestText);
                    JSONParser parser = new JSONParser();
                    JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                        response.sendRedirect("CDRConfig");
                    else
                    {
                        out.println("<p class=errormessage >Error in CDR configuration: "+res.get("message").toString()+"</p>");
                    }
                }
              
            }
            else {
              response.sendRedirect("Login");
            }
           
            Web.setFooter(request, response);
            
        }catch(Exception e){
            out.println("<p class=errormessage>" + e.toString()+"</p>");
        }
        }
    }
     private void editCDRConfigForm(PrintWriter out,String url) throws Exception 
     {
        String spl[];
        String resultText = General.restCallURL(url + "GetCDRConf", "");
        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(resultText);
        boolean success = (Boolean.valueOf(res.get("success").toString()));
        if(success)
        { 
         String result=res.get("result").toString();
         spl=result.split(":");
        out.println("<form method=POST >");
        out.println("<table>");
        out.println("<tr><td>Server  </td><td><input type=text name=ser size=30 value=\""+spl[0]+"\" required/></td></td>");
	
        out.println("<tr><td>Database User</td>");
        out.println("<td><input type=text name=duname size=30 value=\""+spl[1]+"\" required/></td></tr>");
	
        out.println("<tr><td>Database Password</td>");
        out.println("<td><input type=password name=dpass size=30 value=\""+spl[2]+"\" required/></td></tr>");
        
        out.println("<tr><td>Database Name </td>");
        out.println("<td><input type=text name=dname size=30 value=\""+spl[3]+"\"  required/></td></td>");
		
        out.println("<tr><td>CDR Table</td>");
        out.println("<td><input type=text name=cdrtab size=30 value=\""+spl[4]+"\" required/></td></tr>");
	
        out.println("<tr><td>Key Field</td>");
        out.println("<td><input type=text name=key size=30 value=\""+spl[5]+"\" required/></td></tr>");
        
        out.println("<tr><td><input type=submit name=mcok value=OK required/></td></tr>");
        out.println("</table>");
        out.println("</form>");
        }
        else
            out.println("<p class=errormessage >Error in CDR configuration: "+res.get("message").toString()+"</p>");
    }
      private void cdrConfigForm(PrintWriter out) {
        out.println("<form method=POST >");
        out.println("<table>");
        out.println("<tr><td>Server  </td><td><input type=text name=ser size=30 required/></td></td>");
	
        out.println("<tr><td>Database User</td>");
        out.println("<td><input type=text name=duname size=30 required/></td></tr>");
	
        out.println("<tr><td>Database Password</td>");
        out.println("<td><input type=password name=dpass size=30 required/></td></tr>");
        
        out.println("<tr><td>Database Name </td>");
        out.println("<td><input type=text name=dname size=30 required/></td></td>");
		
        out.println("<tr><td>CDR Table</td>");
        out.println("<td><input type=text name=cdrtab size=30 required/></td></tr>");
	
        out.println("<tr><td>Key Field</td>");
        out.println("<td><input type=text name=key size=30 value=calldate required/></td></tr>");
        
        out.println("<tr><td><input type=submit name=cok value=OK required/></td></tr>");
        out.println("</table>");
        out.println("</form>");
        
    }
     private void displayConf(PrintWriter out,String url) throws Exception
     {
         String spl[];
          String resultText = General.restCallURL(url + "GetCDRConf", "");
          JSONParser parser = new JSONParser();
          JSONObject res = (JSONObject) parser.parse(resultText);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                    {
                        String result=res.get("result").toString();
                        spl=result.split(":");
                        out.println("<table class=dtable><tr><th>Server </th><th>Database</th>"
                  + "<th>Database Username</th><th>Database Password</th>"
                  + "<th>CDR Table</th><th>Table Key Field</th></tr>"
                                + "<tr><td>"+spl[0]+"</td><td>"+spl[3]+"</td>"
                                + "<td>"+spl[1]+"</td><td>"+spl[2]+"</td>"
                                + "<td>"+spl[4]+"</td><td>"+spl[5]+"</td></tr></table>");
                        out.println("<form style=margin-left:20px;margin-top:10px action=CDRConfig > "
                                + "<input type=submit value=Edit />"+
                                "<input type=hidden name=edit value=yes />"
                                + "</form>");
                    }
                    else
                        out.println("<p class=errormessage >Error in CDR configuration: "+res.get("message").toString()+"</p>");
     }
     private void displayCDRStatus(PrintWriter out,String url)throws Exception
     {
                    JSONObject res=checkCDRStatus(url);
                    boolean success = (Boolean.valueOf(res.get("success").toString()));
                    if(success)
                        out.println("<p style=margin-left:20px >CDR Conf Status:<span class=infomessage> OK </span></p>");
                    else
                         out.println("<p style=margin-left:20px >CDR Conf Status: <span class=errormessage> "+res.get("message").toString()+" </span></p>");
     }
     private boolean paramStatus(HttpServletRequest request){
         return request.getParameter("cf")==null && request.getParameter("cok")==null && request.getParameter("mcok")==null && request.getParameter("edit")==null;
     }
     private JSONObject checkCDRStatus(String url) throws Exception
     {
         String resultText = General.restCallURL(url + "GetCDRConfStatus", "");
          JSONParser parser = new JSONParser();
          JSONObject res = (JSONObject) parser.parse(resultText);
          return res;
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
