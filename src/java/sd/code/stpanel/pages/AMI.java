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
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sd.code.stpanel.types.AMIResult;

/**
 *
 * @author motaz
 */
@WebServlet(name = "AMI", urlPatterns = {"/AMI"})
public class AMI extends HttpServlet {

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
        try {
            String user = Web.getCookieValue(request, "st-user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            if (Web.checkSession(request, user)) {
                Web.setHeader(true, request, response, out, "advanced", "ami");
                out.println("<h2>AMI command</h2>");

                String command = request.getParameter("command"); 
                out.println("<form method=post>");
                out.println("<textarea name=command cols=50 rows=7 >");
                if (command != null) {
                    out.print(command);
                }
                out.println("</textarea></br>");
                out.println("<input type=submit value=Execute name=execute class=btn />");
                out.println("</form>");

                if ( request.getParameter("execute") != null) {
                    AMIResult result = callAMI(pbxfile, command);
                    if (result.success) {
                        Date now = new Date();
                        if (result.result != null){
                            out.println("<pre>" + now.toString() + "\n" + result.result + "</pre>");
                        }
                    } else {
                        out.println("<p class=errormessage>" +  result.errorMessage );
               
                    }

                }
                Web.setFooter(request, response);

            }
            else {
              response.sendRedirect("Login");
            }
        }
        catch (Exception ex){
            out.println("<p class=errormessage>-" + ex.toString() + "</p>");
        }

        
    }

    public static AMIResult callAMICommand(String pbxfile, String command) throws IOException {
        
        command = "action:command\ncommand:" + command;
        return callAMI(pbxfile, command);
    }

    public static AMIResult callAMI(String pbxfile, String command) throws IOException {
        
        AMIResult result = new AMIResult();
        String url = General.getConfigurationParameter("url", "", pbxfile);
        JSONObject obj = new JSONObject();
        String username = General.getConfigurationParameter("amiuser", "", pbxfile);
        String secret = General.getConfigurationParameter("amipass", "", pbxfile);
        obj.put("username", username);
        obj.put("secret", secret);
        obj.put("command", command);
        String requestText = obj.toJSONString();
        String resultText = General.restCallURL(url + "CallAMI", requestText);
        try{
            JSONParser parser = new JSONParser();
            JSONObject resObj = (JSONObject) parser.parse(resultText);
            
            result.result = resObj.get("message").toString();
            result.success = true;
            
        }
        catch (Exception ex){
            result.success = false;
            result.errorMessage = ex.toString();
            
        }
        return result;
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
