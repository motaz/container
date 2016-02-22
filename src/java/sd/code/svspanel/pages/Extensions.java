/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.pages;

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
import sd.code.svspanel.common.ContextParser;
import sd.code.svspanel.common.General;
import sd.code.svspanel.common.Web;
import sd.code.svspanel.types.NodeInfo;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Extensions", urlPatterns = {"/Extensions"})
public class Extensions extends HttpServlet {

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
              
              Web.setHeader(true, request, response, out, "pbx", "extensions");
            
              String user = Web.getCookieValue(request, "user");
              String title = "Trunk";
              boolean isExten = false;
              String type = request.getParameter("type");
              if (type == null){
                  type = "ext";
                  title = "Extension";
                  isExten = true;
              }
              

              String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
              if (Web.checkSession(request, user)) {


                  out.println("<a href='EditNode?filename=sip.conf'>Add new SIP node</a>");

                  String url = General.getConfigurationParameter("url", "", pbxfile);
                  JSONObject obj = new JSONObject();
                  obj.put("filename", "sip.conf");
                  String requestText = obj.toJSONString();

                  String resultText = General.restCallURL(url + "GetFile", requestText);

                  JSONParser parser = new JSONParser();
                  JSONObject resObj = (JSONObject) parser.parse(resultText);
                  if ((boolean)resObj.get("success")) {
                        String content = resObj.get("content").toString();
                        String[] arr = content.split("\n");

                        ContextParser cparser = new ContextParser(arr, "sip.conf");
                        ArrayList<NodeInfo> nodes = cparser.getNodesWithInfo();

                        String reverseStr = Web.getCookieValue(request, "reverse");
                        boolean reverse = (reverseStr != null) && (reverseStr.equals("yes"));
                        out.println("<table><tr><th>" + title +"</th><th>User name</th>");
                        out.println("<th>Host</th><th>Context</th></tr>");
                        if (reverse) {
                            for (int i= nodes.size() -1; i >= 0; i--) {
                                NodeInfo node = nodes.get(i);
                                  displayNode(out, node, isExten);

                            }
                        }
                        else {
                          for (NodeInfo node: nodes) {
                                 displayNode(out, node, isExten);
                          }
                        }
                        out.println("</table>");
                        Web.setFooter(out);
                    }

                }
        
              Web.setFooter(out);
           
        } catch (Exception ex){
            out.println("<p class=errormessage>" + ex.toString() + "</p>");
        }
        }
    }

    private void displayNode(final PrintWriter out, NodeInfo node, boolean displayExtension) {
        if ((displayExtension &&node.isExtension()) || (! displayExtension && node.isTrunk())) {
            out.println("<tr>");
            out.println("<td><a href='EditNode?filename=sip.conf&nodename=[" + 
                    node.getNodeName() + "]'>" +
                    node.getNodeName() + "</a></td>");
            out.println("<td>" + node.getProperty("username") + "</td>");
            out.println("<td>" + node.getProperty("host") + "</td>");
            out.println("<td>" + node.getProperty("context") + "</td>");
            out.println("</tr>");
        }
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
