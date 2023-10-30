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
            String user = Web.getCookieValue(request, "st-user");
            String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
            try {
                if (Web.checkSession(request, user)) {
                    Web.setHeader(true, request, response, out, "advanced", "files");

                    out.println("<h2>Files</h2>");
                    String fileName = request.getParameter("file");
                    if (fileName == null){
                        fileName = "";
                    }
                    
                    displayFilesMenu(fileName, out);
  

                    displayFileContents(fileName, pbxfile, out);
                  
                
                    Web.setFooter(request, response);
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

    public void displayFilesMenu(String fileName, PrintWriter out) {
        
        String asteriskColor =  "";
        String sipColor =  "";
        String extensionsColor = "";
        String queuesColor = "";
        String agentsColor = "";
        String rtpColor = "";
        String cdrColor = "";
        String cdr_customColor = "";
        String managerColor =  "";
        String httpColor =  "";

        String allFilesColor = "";
        
        String selectedColor = "bgcolor=#FFFFcc";
        
        
        switch (fileName) {
            case "sip.conf":
                sipColor = selectedColor;
                break;
            case "extensions.conf":
                extensionsColor = selectedColor;
                break;
            case "queues.conf":
                queuesColor = selectedColor;
                break;
            case "agents.conf":
                agentsColor = selectedColor;
                break;
            case "rtp.conf":
                rtpColor = selectedColor;
                break;
            case "cdr.conf":
                cdrColor = selectedColor;
                break;
            case "cdr_custom.conf":
                cdr_customColor = selectedColor;
                break;
            case "http.conf":
                httpColor = selectedColor;
                break;     
            case "asterisk.conf":
                asteriskColor = selectedColor;
                break;      
            case "manager.conf":
                managerColor = selectedColor;
                break;
                case "all":
                allFilesColor = selectedColor;
                break;
            default:
                break;
        }
        out.println("<table><tr bgcolor=#eeeecc>");
        out.println("<td " + asteriskColor + "><a href='Files?file=asterisk.conf'>asterisk.conf</a></td>");
        out.println("<td " + sipColor + "><a href='Files?file=sip.conf'>sip.conf</a></td>");
        out.println("<td " + extensionsColor + "><a href='Files?file=extensions.conf'>extensions.conf</a></td>");
        out.println("<td " + queuesColor + "><a href='Files?file=queues.conf'>queues.conf</a></td>");
        out.println("<td " + agentsColor + "><a href='Files?file=agents.conf'>agents.conf</a></td>");
        out.println("<td " + rtpColor + "><a href='Files?file=rtp.conf'>rtp.conf</a></td>");
        out.println("<td " + cdrColor + "><a href='Files?file=cdr.conf'>cdr.conf</a></td>");
        out.println("<td " + cdr_customColor + "><a href='Files?file=cdr_custom.conf'>cdr_custom.conf</a></td>");
        out.println("<td " + managerColor + "><a href='Files?file=manager.conf'>manager.conf</a></td>");
        out.println("<td " + httpColor + "><a href='Files?file=http.conf'>http.conf</a></td>");
        out.println("<td " + allFilesColor + "><a href='Files?file=all'>All Files</a></td>");
        out.println("</tr></table>");
    }

    public void displayFileContents(String fileName, String pbxfile, PrintWriter out) throws IOException, ParseException {
        if (!fileName.isEmpty()){
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
    }

    private void displayFilesList(final PrintWriter out, String url, String requestText) throws ParseException, IOException {
        
        out.println("<h3>All configuration files</h3>");
        out.println("<a  href='CreateFile' class='linkbutton'> Create New File </a><br><br>");
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
                if (col > 5) {
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
        out.println("<input type=submit name=edit value= 'Edit file' class='button' />");
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
