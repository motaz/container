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
                    String backupFileName = request.getParameter("backupfile"); 
                    
                    JSONObject obj = new JSONObject();
                    String url = General.getConfigurationParameter("url", "", pbxfile);
                    
                    if (fileName != null) {
                        
                        obj.put("foldername", "/etc/asterisk/backup/");
                        String requestText = obj.toJSONString();                                              
                        displayBackupFilesList(out, url, requestText, fileName );
                        
                    }else{
                            if ((backupFileName != null)||(backupFileName != "")){
                              
                                out.println("<h3>" + backupFileName + "</h3>");
                                String originalFileName = backupFileName.substring(0 , backupFileName.indexOf("conf")+4);
                                doRetrieve(request, originalFileName, url, out);
                                obj.put("filename", "/etc/asterisk/backup/"+backupFileName);
                                String requestText = obj.toJSONString();
                                displayBackupFileContents(url, requestText, out, backupFileName);                                  
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
    
    private void displayBackupFilesList(final PrintWriter out, String url, String requestText, String fileName ) throws ParseException, IOException {
        
        out.println("<h3>"+fileName+"  Revisions Backup files</h3>");
        
        out.println("<form method=GET action=CompareFiles>");
        out.println("<input type=submit name=diff value= 'Diff' />");
        
        out.println("<table><tr>");
        String resultText = General.restCallURL(url + "ListFiles", requestText);
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
        if (Boolean.valueOf(resObj.get("success").toString())) {
            JSONArray files = (JSONArray)resObj.get("files");
            
            
            for (Object file : files) {
                String afile = file.toString();
                
                String originalFileName = afile.substring(0 , fileName.indexOf("conf")+4);
                   if (fileName.equals(originalFileName)){
                        out.println("<td><input type='checkbox' name='backupfilename' value='"+afile+"'></td> ");
                        out.println("<td><a href='BackupFiles?backupfile=" + afile + "'>" + afile + "</a></td>");
                        out.println("</tr><tr>");                     
                   }                     
            }
        }
        out.println("</tr></table>");
        out.println("</form>");      
    }

    
    
    private void  displayBackupFileContents(String url, String requestText, final PrintWriter out, String fileName) throws IOException, ParseException {
        
        
        
        String resultText = General.restCallURL(url + "GetFile", requestText);
        
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
            
        
        out.println("<form method=POST >");
        out.println("<input type=hidden name=filename value='" + fileName + "' />");
        out.println("<input type=submit name=retrieve value= 'Retrieve Backup File  ' /> <br/> " );
         


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
                              
            //Web.displayIncludedFiles(content, out, "Files?file=");            
            //out.println("<pre>" + content + "</pre>");
            
            out.println("<textarea  readonly cols=140 rows = 60 font name=content >");
            out.print(content);
            out.println("</textarea><br/>");
            
            out.println("</form>");   
        }
        else {
            out.println("<p class=errormessage>" + resObj.get("message") + "</p>");
        }
    }

       private void doRetrieve(HttpServletRequest request, String fileName, String url, final PrintWriter out) {    
        try {
        if (request.getParameter("retrieve") != null) {
            JSONObject saveobj = new JSONObject();
            saveobj.put("filename", fileName);
            saveobj.put("content", request.getParameter("content"));
            String requestText = saveobj.toJSONString();
            String resultText = General.restCallURL(url + "ReplaceFile", requestText);
            JSONParser saveparser = new JSONParser();
            JSONObject saveresObj = (JSONObject) saveparser.parse(resultText);
            boolean res = (Boolean.valueOf(saveresObj.get("success").toString()));
            if (res) {
                out.println("<p class=infomessage>File Replaced</p>");
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
