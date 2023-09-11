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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author motaz
 */
@WebServlet(name = "EditFile", urlPatterns = {"/EditFile"})
public class EditFile extends HttpServlet {

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
            String user = Web.getCookieValue(request, "st-user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            try {
                String fileName = request.getParameter("filename");
                if (Web.checkSession(request, user)) {
                    Web.setHeader(true, request, response, out, "advanced", "files");
                    String url = General.getConfigurationParameter("url", "", pbxfile);

                    out.println("<h4>Edit file: <b>[" + fileName + "]</b> </h4>");

                    doSave(request, fileName, url, out);

                    JSONObject obj = new JSONObject();
                    obj.put("filename", fileName);
                    String requestText = obj.toJSONString();


                    String resultText = General.restCallURL(url + "GetFile", requestText);
                    JSONParser parser = new JSONParser();
                    JSONObject resObj = (JSONObject) parser.parse(resultText);
   
                    displayEditForm(out, resObj, fileName);

                
		}
            }catch (Exception ex){
                General.writeEvent("Error in EditNode: " + ex.toString());
            }
        out.close();
    }
    
    private void displayEditForm(final PrintWriter out, JSONObject resObj, String fileName) {
        
        out.println("<form method=POST>");
        if (Boolean.valueOf(resObj.get("success").toString())) {
            String content = resObj.get("content").toString();
            
            //String[] arr = content.split("\n");
            
            out.println("<input type=hidden name=filename value='" + fileName + "' />");
            out.println("<input type=submit value='Save modifications' name=save class='button' />");
            out.println("<textarea cols=140 rows = 60 font name=content >");
            out.print(content);
            out.println("</textarea><br/>");

        }
        out.println("</form>");
    }

    private void doSave(HttpServletRequest request, String fileName, String url, final PrintWriter out) {
        
        try {
        if (request.getParameter("save") != null) {
            JSONObject saveobj = new JSONObject();
            saveobj.put("filename", fileName);
            saveobj.put("content", request.getParameter("content"));
            String requestText = saveobj.toJSONString();
            String resultText = General.restCallURL(url + "ModifyFile", requestText);
            JSONParser saveparser = new JSONParser();
            JSONObject saveresObj = (JSONObject) saveparser.parse(resultText);
            boolean res = (Boolean.valueOf(saveresObj.get("success").toString()));
            if (res) {
                out.println("<p class=infomessage>Saved</p>");
                out.println("<a href='Files?file=" + fileName + "'>View (Read only)</a>");
                Web.displayReloadLink(fileName, out);
            }
            else {
                out.println("<p class=errormessage>Error: " + saveresObj.get("message").toString() + "</p>");
            }
            
        }
        }
        catch (Exception ex){
                out.println("<p class=errormessage>Error: " + ex.toString() + "</p>");
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
