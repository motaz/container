/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import sd.code.stpanel.types.Operation;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Backup", urlPatterns = {"/Backup"})
public class Backup extends HttpServlet {

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
        
        try {
            response.setContentType("application/zip");
            String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
            String url = General.getConfigurationParameter("url", "", pbxfile);          
            JSONObject obj = new JSONObject();
            obj.put("directory", "/etc/asterisk/");
            obj.put("ext", ".conf");
            obj.put("name", pbxfile);
            String requestText = obj.toJSONString();
            OutputStream output = response.getOutputStream();
            File file = new File(pbxfile);
                
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName() + ".zip");
            Operation op = General.downloadFile(url + "BackupFiles",requestText, "application/zip", output);
            response.setContentLength((int)op.size);
        }
        catch (Exception ex){
          PrintWriter out = response.getWriter();
          out.println(ex.toString());
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
