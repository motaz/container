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
@WebServlet(name = "Status", urlPatterns = {"/Status"})
public class Status extends HttpServlet {

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
                    Web.setHeader(true, request, response, out, "status");

                    out.println("<h1>Status</h1>");

                    String command = request.getParameter("command");
                    
                    String commandLine = "";
                    String chColor =  "";
                    String peerColor = "";
                    String usersColor = "";
                    String codecColor = "";
                    String statusColor = "";
                    String queueColor = "";
                    String agentColor = "";
                    
                    if (command != null) {
                        
                        switch (command) {
                            case "channels":
                                commandLine = "core show channels";
                                chColor = "bgcolor=#AAAADD";
                                break;
                            case "peers":
                                commandLine = "sip show peers";
                                peerColor = "bgcolor=#AAAADD";
                                break;
                            case "users":
                                commandLine = "sip show users";
                                usersColor = "bgcolor=#AAAADD";
                                break;
                            case "codecs":
                                commandLine = "core show codecs";
                                codecColor = "bgcolor=#AAAADD";
                                break;
                            case "stats":
                                commandLine = "sip show channelstats";
                                statusColor = "bgcolor=#AAAADD";
                                break;
                            case "queues":
                                commandLine = "queue show";
                                queueColor = "bgcolor=#AAAADD";
                                break;
                            case "agents":
                                commandLine = "agent show";
                                agentColor = "bgcolor=#AAAADD";
                                break;
                        }
                    }
                    
                    
                    out.println("<table><tr>");
                    out.println("<td " + chColor + "><a href='Status?command=channels'>Channels</a></td>");
                    out.println("<td " + peerColor + "><a href='Status?command=peers'>Peers</a></td>");
                    out.println("<td " + usersColor + "><a href='Status?command=users'>Users</a></td>");
                    out.println("<td " + statusColor + "><a href='Status?command=stats'>Channel stats.</a></td>");
                    out.println("<td " + queueColor + "><a href='Status?command=queues'>Queue</a></td>");
                    out.println("<td " + agentColor + "><a href='Status?command=agents'>Agents</a></td>");
                    out.println("<td " + codecColor + "><a href='Status?command=codecs'>Codecs</a></td>");
                    out.println("</tr></table>");
                    
                    if (command != null){

                        out.println("<h3>" + command + "</h3>");
                        out.println("<form method=post>");
                        out.println("<input type=submit value=Refresh class=btn />");
                        out.println("</form>");

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
            catch (Exception ex){
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
