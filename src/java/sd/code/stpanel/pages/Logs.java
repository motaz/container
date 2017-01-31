/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Logs", urlPatterns = {"/Logs"})
public class Logs extends HttpServlet {

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
        PrintWriter out = response.getWriter();
          String user = Web.getCookieValue(request, "user");
          String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            try {
                if (Web.checkSession(request, user)) {
                    Web.setHeader(true, request, response, out, "advanced", "logs");

                    out.println("<h2>Logs</h2>");

                    String file = request.getParameter("file");
                    out.println("<table><tr>");
                    out.println("<td ><a href='Logs?file=Messages'>Messages</a></td>");
                    out.println("<td ><a href='Logs?file=Full'>Full Log</a></td>");
                    out.println("</tr></table>");
                    
                    String fileName = "";
                    String linesSize = request.getParameter("size");
                    if (linesSize == null){
                        linesSize = Web.getCookieValue(request, "logsize");
                        if (linesSize == null) {
                            linesSize = "40";
                        }
                    }
                    
                    if (file != null) {
                        if (file.equals("Messages")){
                                fileName = "/var/log/asterisk/messages";
			}
			else if (file.equals("Full")){
                                fileName = "/var/log/asterisk/full";
                                
                        }                        
                        out.println("<h3>" + file + "</h3>");
                        out.println("<form method=post>");
                        out.println("Display last <input type=text name=size size=2 value='" + linesSize + "' /> lines");
                        out.println("<input type=submit value=Refresh class=btn />");
                        out.println("</form>");                    

                        Cookie co = new Cookie("logsize", linesSize);
                        response.addCookie(co);

                        // Call service
                        JSONObject obj = new JSONObject();
                        obj.put("file", fileName);
                        obj.put("lines", linesSize);
                        String requestText = obj.toJSONString();

                        String url = General.getConfigurationParameter("url", "", pbxfile);

                        String resultText = General.restCallURL(url + "GetLogTail", requestText);
                        JSONParser parser = new JSONParser();
                        JSONObject resObj = (JSONObject) parser.parse(resultText);

                        if (Boolean.valueOf(resObj.get("success").toString())) {
                            String text = resObj.get("content").toString();
                            out.println("<pre>" + text + "</pre>");
                        }
                        else {
                           out.println("<p class=errormessage>" + resObj.get("message") + "</p>");
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
