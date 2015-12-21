/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.svspanel.pages;

import sd.code.svspanel.common.General;
import sd.code.svspanel.common.Web;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "Commands", urlPatterns = {"/Commands"})
public class Commands extends HttpServlet {

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
          String user = Web.getCookieValue(request, "user");
          String pbxfile = "/etc/code/pbxs/" + Web.getCookieValue(request, "file");
            try {
                if (Web.checkSession(request, user)) {
                    Web.setHeader(true, request, response, out, "commands");

                    out.println("<h1>Commands</h1>");

                    String command = request.getParameter("command");
                    
                    String commandLine = "";
                    String chColor =  "";
                    String peerColor = "";
                    String usersColor = "";
                    String versionColor = "";
                    String helpColor = "";
                    
                    String commandText = request.getParameter("commandtext");
                    if (commandText == null){
                        commandText = "";
                    }
   
                    if (command != null) {
                        
                        switch (command) {
                            case "corereload":
                                commandLine = "core reload";
                                chColor = "bgcolor=#AAAADD";
                                break;
                            case "sipreload":
                                commandLine = "sip reload";
                                peerColor = "bgcolor=#AAAADD";
                                break;
                            case "dialplanreload":
                                commandLine = "dialplan reload";
                                usersColor = "bgcolor=#AAAADD";
                                break;
                            case "version":
                                commandLine = "core show version";
                                versionColor = "bgcolor=#AAAADD";
                                break;
                            case "help":
                                commandLine = "core show help";
                                helpColor = "bgcolor=#AAAADD";
                                break;
                            case "text":
                                commandLine = commandText;
                                break;
                        }
                    }
                    
                    out.println("<table><tr>");
                    out.println("<td " + chColor + "><a href='Commands?command=corereload'>core reload</a></td>");
                    out.println("<td " + peerColor + "><a href='Commands?command=sipreload'>sip reload</a></td>");
                    out.println("<td " + usersColor + "><a href='Commands?command=dialplanreload'>dialplan reload</a></td>");
                    out.println("<td " + versionColor + "><a href='Commands?command=version'>version</a></td>");
                    out.println("<td " + helpColor + "><a href='Commands?command=help'>Help</a></td>");
                    out.println("</tr></table>");
                    
                    
                    out.println("<br/><form method=POST action=Commands?command=text >");
                    out.println("Command <input type=text name=commandtext size = 40 value = '" + commandText + "' /> &emsp;");
                    out.println("<input type=submit name=execute value='Execute' class=btn />");
                    out.println("</form>");
                            
                    if (command != null){

                        if (!command.equals("text")) {
                            out.println("<h3>" + command + "</h3>");
                        }

                        JSONObject obj = new JSONObject();
                        obj.put("command", commandLine);
                        String requestText = obj.toJSONString();

                        String url = General.getConfigurationParameter("url", "", pbxfile);

                        String resultText = General.restCallURL(url + "Command", requestText);
                        JSONParser parser = new JSONParser();
                        JSONObject resObj = (JSONObject) parser.parse(resultText);
                        if ((boolean)resObj.get("success")) {
                            String text = resObj.get("result").toString();
                            out.println("<pre>" + text + "</pre>");
                        }
                    }

                    Web.setFooter(out);
                }
                else {
                    response.sendRedirect("Login");
                }                    
        }
            catch (Exception ex) {
                out.println(ex.toString());
            }
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
