/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author ameenah
 */
@WebServlet(name = "BackupFiles", urlPatterns = {"/BackupFiles"})
public class BackupFiles extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request , HttpServletResponse response ) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()){
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            
            try {
                if (Web.checkSession(request, user)){
                    Web.setHeader(true, request, response, out, "advanced", "tools");
                    
                    out.println("<h2>Files</h2>");

                    String fileName = request.getParameter("file");
                    
                    if (fileName != null) {
                        JSONObject obj = new JSONObject();
                        obj.put("foldername", "/etc/asterisk/backup/");
                        String requestText = obj.toJSONString();                        
                        
                         String url = General.getConfigurationParameter("url", "", pbxfile);
                         
                         if (fileName.equals("all")){
                             displayBackupFilesList(out, url, requestText);
                         }else{
                            out.println("<h3>" + fileName + "</h3>");
                            obj.put("filename", "/etc/asterisk/backup/"+fileName);
                            requestText = obj.toJSONString();
                            displayBackupFileContents(url, requestText, out, fileName);                             
                         }
                        
                    }
                              
                    Web.setFooter(out);
                } else {
                  response.sendRedirect("Login");
              }
            }
            catch (Exception ex){
		out.println(ex.toString());
		General.writeEvent("Error: in Backup  Files: " + ex.toString());
	    }
        }
    
    } 
    
    private void displayBackupFilesList(final PrintWriter out, String url, String requestText) throws ParseException, IOException {
        
        out.println("<h3>All configuration Backup files</h3>");
        
        out.println("<form method=GET action=EditFile>");
        //out.println("<input type=hidden name=filename value='" + fileName + "' />");
        out.println("<input type=submit name=diff value= 'Diff' />");
        out.println("</form>");
        
        out.println("<table><tr>");
        String resultText = General.restCallURL(url + "ListFiles", requestText);
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
        if (Boolean.valueOf(resObj.get("success").toString())) {
            JSONArray files = (JSONArray)resObj.get("files");
            
            
            for (Object file : files) {
                String afile = file.toString();
                
                
           
                out.println("<td><input type='checkbox' name='vehicle' value='"+afile+"'></td> ");
                out.println("<td><a href='BackupFiles?file=" + afile + "'>" + afile + "</a></td>");
                out.println("</tr><tr>");

                    
            }
        }
        out.println("</tr></table>");
    }

    
    
    private void  displayBackupFileContents(String url, String requestText, final PrintWriter out, String fileName) throws IOException, ParseException {
        
        String resultText = General.restCallURL(url + "GetFile", requestText);
        out.println("<form method=GET action=EditFile>");
        out.println("<input type=hidden name=filename value='" + fileName + "' />");
        out.println("<input type=submit name=edit value= 'Edit file' />");
        out.println("</form>");
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
        if (Boolean.valueOf(resObj.get("success").toString())) {
            String content = resObj.get("content").toString();
            
            // Display last updated time
            if (resObj.get("filetime") != null){
                String fileTime = resObj.get("filetime").toString();
                if (fileTime.contains(".") || fileTime.contains("+")){
                    String terminateAt = ".";
                    if (! fileTime.contains(".")){
                        terminateAt = "+";
                    }
                    fileTime = fileTime.substring(0, fileTime.indexOf(terminateAt));
                }
                out.println("Last updated: <font size=-1>" + fileTime + "</font><br/>");
            }
                    
            
            Web.displayIncludedFiles(content, out, "Files?file=");
            
            out.println("<pre>" + content + "</pre>");
        }
        else {
            out.println("<p class=errormessage>" + resObj.get("message") + "</p>");
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
