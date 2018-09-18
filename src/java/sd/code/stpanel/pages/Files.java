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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author motaz
 */
@WebServlet(name = "Files", urlPatterns = {"/Files"})
public class Files extends HttpServlet {

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
                  Web.setHeader(true, request, response, out, "advanced", "files");

                  out.println("<h2>Files</h2>");
                
                  out.println("<table><tr>");
                  out.println("<td><a href='Files?file=sip.conf'>sip.conf</a></td>");
                  out.println("<td><a href='Files?file=extensions.conf'>extensions.conf</a></td>");
                  out.println("<td><a href='Files?file=queues.conf'>queues.conf</a></td>");
                  out.println("<td><a href='Files?file=agents.conf'>agents.conf</a></td>");
                  out.println("<td><a href='Files?file=rtp.conf'>rtp.conf</a></td>");
                  out.println("<td><a href='Files?file=cdr.conf'>cdr.conf</a></td>");
                  out.println("<td><a href='Files?file=cdr_custom.conf'>cdr_custom.conf</a></td>");
                  out.println("<td><a href='Files?file=all'>All Files</a></td>");
                  out.println("</tr></table>");
                
                  String fileName = request.getParameter("file");
                
                  if (fileName != null) {

                    JSONObject obj = new JSONObject();
                    obj.put("filename", fileName);
                    String requestText = obj.toJSONString();

                    String url = General.getConfigurationParameter("url", "", pbxfile);

                    if (fileName.equals("all")) {
                        displayFilesList(out, url, requestText);
                        
                    }
                    else {
                        out.print("<h3>" + fileName + " <a href='BackupFiles?file=" + fileName + "'> \t Revisions </a>  "+"</h3>");
                       // out.println("<h3> <a href='BackupFiles?file=" + fileName + "'> Revisions </a></h3>");
                        displayFileContents(url, requestText, out, fileName);
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

    private void displayFilesList(final PrintWriter out, String url, String requestText) throws ParseException, IOException {
        
        out.println("<h3>All configuration files</h3>");
        out.println("<table><tr>");
        String resultText = General.restCallURL(url + "ListFiles", requestText);
        JSONParser parser = new JSONParser();
        JSONObject resObj = (JSONObject) parser.parse(resultText);
        int col=0;
        if (Boolean.valueOf(resObj.get("success").toString())) {
            JSONArray files = (JSONArray)resObj.get("files");
            
            for (Object file : files) {
                String afile = file.toString();
                out.println("<td><a href='Files?file=" + afile + "'>" + afile + "</a></td>");
                col++;
                if (col > 6) {
                    col = 0;
                    out.println("</tr><tr>");
                }
            }
        }
        out.println("</tr></table>");
    }

    private void displayFileContents(String url, String requestText, final PrintWriter out, String fileName) throws IOException, ParseException {
        
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
