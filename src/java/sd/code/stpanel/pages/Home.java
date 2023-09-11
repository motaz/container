/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author motaz
 */
@WebServlet(name = "Home", urlPatterns = {"/Home"})
public class Home extends HttpServlet {

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
	try(PrintWriter out = response.getWriter()) {
            
            try{
                Web.setHeader(true, request, response, out, "home", "home");
                
                String user = Web.getCookieValue(request, "st-user");
                
                if (Web.checkSession(request, user)){
                    
                    out.println("<h3>Select PBX</h3>");
                    
                    out.println("<a href='AddPBX' class='linkbutton'>Insert new PBX</a>");
                    
                    displayFiles(out);
                    
                }  else  {
                    
                    response.sendRedirect("Login");
                }
                    
                Web.setFooter(request, response);
            }
            catch (Exception ex){
                out.println("<p class=errormessage>" + ex.toString() + "</p>");
            }
        }
            
    }

    private void displayFiles(PrintWriter out) {
        
        File[] listOfFiles = getPbxFiles();
        if (listOfFiles != null){
       
            out.println("<table><tr>");
            int counter = 0;
            boolean done = false;

            while (!done ){

               done = true;
               for (int i=0; i<listOfFiles.length-1; i++){
                   String index1Str = General.getConfigurationParameter("index", "0", listOfFiles[i].getAbsolutePath());
                   String index2Str = General.getConfigurationParameter("index", "0", listOfFiles[i+1].getAbsolutePath());
                   int index1 = Integer.parseInt(index1Str);
                   int index2 = Integer.parseInt(index2Str);
                   if (index1 > index2) {

                       File temp;
                       temp = listOfFiles[i];
                       listOfFiles[i] = listOfFiles[i+1];
                       listOfFiles[i+1] = temp;
                       done = false;
                   }

               }
            }
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    String fileName = listOfFile.getName();
                    String title = General.getConfigurationParameter("title", 
                            "", listOfFile.getAbsolutePath());
                    if (fileName.endsWith(".stc")) {
                    if (counter % 5 == 0){
                        out.println("</tr><tr>");
                    }
                    String color;
                    if (counter % 2 == 0) {
                        color = "#dAbaa7";
                    } else {
                        color = "#dececa";
                    }
                    String url = General.getConfigurationParameter("url", 
                            "", listOfFile.getAbsolutePath());
                    String ip = url.substring(url.indexOf("//") + 2, url.length());
                    if (ip.contains(":")) {
                    ip = ip.substring(0, ip.indexOf(":"));
                    } else {
                        ip = ip.substring(0, ip.indexOf("/"));
                    }
                    String link = "<a href='SelectPBX?pbx=" + fileName + "'>" + title + "</a>";
                    String editLink = "<font >" + 
                            "<a style='font-size:80%'  href='EditPBX?pbx=" + fileName + "'>Edit" +
                            "</a></font>";
                    out.println("<td width=20% bgcolor=" + color  + 
                                "><b>" + link + "<br/>" + 
                            ip + "</b><br/>" +
                            fileName + "<br/" + editLink + "</td>");
                    
                    counter++;
                }
                }
            }
        
            
        }
    
        out.println("</tr></table>");
    }

    public static File[] getPbxFiles() {
        File folder = new File(General.getPBXsDir());
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
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
