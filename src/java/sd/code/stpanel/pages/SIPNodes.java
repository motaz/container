/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.ContextParser;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author motaz
 */
@WebServlet(name = "SIPNodes", urlPatterns = {"/SIPNodes"})
public class SIPNodes extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
           {
        response.setContentType("text/html;charset=UTF-8");
         PrintWriter out = response.getWriter();
         try {
          String user = Web.getCookieValue(request, "user");
          String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
          if (Web.checkSession(request, user)) {
              
              Web.setHeader(true, request, response, out, "advanced", "sip");
              out.println("<h2>SIP Nodes</h2>");
              
              out.println("<a href='EditNode?filename=sip.conf'>Add new SIP node</a>");
              
              String url = General.getConfigurationParameter("url", "", pbxfile);
              JSONObject obj = new JSONObject();
              obj.put("filename", "sip.conf");
              String requestText = obj.toJSONString();

              String resultText = General.restCallURL(url + "GetFile", requestText);
              JSONParser parser = new JSONParser();
              JSONObject resObj = (JSONObject) parser.parse(resultText);
              if (Boolean.valueOf(resObj.get("success").toString())) {
                    String content = resObj.get("content").toString();
                    String[] arr = content.split("\n");

                    ContextParser cparser = new ContextParser(arr, "sip.conf");
                    ArrayList<String> nodes = cparser.getNodes();

                    String reverseStr = Web.getCookieValue(request, "reverse");
                    boolean reverse = (reverseStr != null) && (reverseStr.equals("yes"));
                    
                    out.println("<table class=dtable><tr><th>Node</th><th></th></tr>");
                    if (reverse) {
                        for (int i= nodes.size() -1; i >= 0; i--) {
                            String node = nodes.get(i);
                            out.println("<tr>");
                            out.println("<td><a href='EditNode?filename=sip.conf&nodename=" + node + "'>" + node + "</a></td>");
                            out.println("</tr>");
                            
                        }
                    }
                    else {
                      for (String node: nodes) {
                          out.println("<tr>");
                          out.println("<td><a href='EditNode?filename=sip.conf&nodename=" + node + "'>" + node + "</a></td>");
                          out.println("</tr>");
                      }
                    }
                    out.println("</table>");
                    Web.setFooter(out);
                }
              
            }
            }
            catch (Exception ex){
                out.println(ex.toString());
            }
            out.close();
            
        
        
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
