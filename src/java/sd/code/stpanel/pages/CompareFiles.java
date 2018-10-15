/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
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
import sd.code.stpanel.types.DiffPosition;
/**
 *
 * @author ameenah
 */
@WebServlet(name = "CompareFiles", urlPatterns = {"/CompareFiles"})
public class CompareFiles extends HttpServlet {
    
    protected void processRequest (HttpServletRequest request , HttpServletResponse response ) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try(PrintWriter out = response.getWriter()){
            String user = Web.getCookieValue(request, "user");
            String pbxfile = General.getPBXsDir() + Web.getCookieValue(request, "file");
            
            try {
                String  originalFileName  = request.getParameter("originalfilename");
                String  backupFileName  = request.getParameter("backupfilename");               
               
                if(Web.checkSession(request, user)){
                    Web.setHeader(true, request, response, out,"advanced", "tools");
                    
                   if ((request.getParameter("CompareFiles") != null)){
                                        
                        if ((originalFileName != null) &(backupFileName != null))  {
                            
                            String url = General.getConfigurationParameter("url", "", pbxfile);
             
                            
                            JSONObject obj1 = new JSONObject(); 
                            obj1.put("filename", "/etc/asterisk/" + originalFileName);
                            String requestText = obj1.toJSONString();                                                          
                            String originalResultText = General.restCallURL(url + "GetFile", requestText);                          
                            
                            JSONParser parser = new JSONParser();
                            JSONObject resObj1 = (JSONObject) parser.parse(originalResultText);                           
                            
                            
                            JSONObject obj2 = new JSONObject();                            
                            obj2.put("filename", "/etc/asterisk/backup/" + backupFileName);
                            requestText = obj2.toJSONString();                                                          
                            String backupResultText = General.restCallURL(url + "GetFile", requestText);
                       
                            JSONObject resObj2 = (JSONObject) parser.parse(backupResultText);                            
                                                               
                            String command = "diff " +"/etc/asterisk/backup/" +backupFileName +"  /etc/asterisk/" + originalFileName  ;
                            JSONObject diffobj = new JSONObject(); 
                            diffobj.put("command", command );
                            requestText = diffobj.toJSONString();                                                          
                            String resultText = General.restCallURL(url + "Shell", requestText); 
                                
                            JSONObject diffObject = (JSONObject) parser.parse(resultText);                                
                            ArrayList<DiffPosition> dpArr = diff(out , diffObject); 
                                                      
                            displayCompareFile(out, resObj1,resObj2 ,  originalFileName, backupFileName , dpArr);                                                       
                            
                        }
                   }
                    Web.setFooter(out);
                }else{
                    response.sendRedirect("Login");
                }
                
            }catch (Exception ex ){
                out.println(ex.toString());
                General.writeEvent("Error: in Compare Files: " + ex.toString());
            }
            
        }
        
    }
    private void displayCompareFile(final PrintWriter out, JSONObject firstResObj,JSONObject secondResObj,  String originalFileName , String backupFileName , ArrayList<DiffPosition> dpArr) {
        
        String originalContent = "";
        if (Boolean.valueOf(firstResObj.get("success").toString())) {
            originalContent = firstResObj.get("content").toString();
        }
        String backupContent = "";
        if (Boolean.valueOf(secondResObj.get("success").toString())) {
            backupContent = secondResObj.get("content").toString();
        }
        
        
        String[] originalContentArr = originalContent.split("\n");
        String[] backupContentArr = backupContent.split("\n");
        
        //int contentLength = (originalContentArr.length > backupContentArr.length)? originalContentArr.length:backupContentArr.length;
            
        out.println("<br><br><br>");
        out.println("<div>");
          //out.println("<h3> "+ originalFileName +"</h3>");
        
            out.println("<table width='50%' style='float: left; display: inline-block;' >");
                out.println("<tbody>");
                    
                    out.println("<tr> <th> </th>");
                    out.println(" <th> <h3>"+originalFileName+"</h3></th> </tr>");
                    
                        int originCount = 0 ;              
                        for (int i = 0;i<=originalContentArr.length; i++ ){
                           
                            if(i  >= originalContentArr.length){
                                out.println("<tr>");
                                out.println("<td>"+(i+1) +"</td>");
                                out.println("<td>  \t </td>");
                                out.println("</tr>");
                            }else{
                                int startPoint = dpArr.get(originCount).secondFileStartPos -1  ;
                                int endPoint = dpArr.get(originCount).secondFileEndPos    ;
                                if (startPoint == i ){
                                    while (startPoint < endPoint ){
                                        out.println("<tr>");
                                        out.println("<td>"+(i+1) +"</td>");
                                        switch(dpArr.get(originCount).type){
                                            case 'a':
                                                out.println("<td bgcolor='#B4FFB4'>"+originalContentArr[i] +" </td>");
                                                break ;
                                            case 'd':
                                                out.println("<td bgcolor='#FFA0B4'>"+originalContentArr[i] +" </td>");
                                                break ;
                                            case 'c':
                                                out.println("<td bgcolor='#A0C8FF'>"+originalContentArr[i] +" </td>");
                                                break ;                                                
                                       }
                                        
                                        out.println("</tr>");
                                        startPoint++ ;
                                        i++ ;
                                        
                                    }
                                    if (originCount < dpArr.size()-1 ){
                                         originCount++ ;
                                    }

                               }else{
                                   out.println("<tr>");
                                   out.println("<td>"+(i+1) +"</td>");
                                   out.println("<td>"+originalContentArr[i] +" </td>");
                                   out.println("</tr>");
                               }
                               
                             }
                        }   
                
                
                out.println("</tbody>");
                out.println("</table>");       
                
                        
                            /////////////////////////////////////////////////////
            

                            
            out.println("<table width='50%'>");
                out.println("<colgroup '> <colgroup>");
                out.println("<colgroup> <colgroup>");
                out.println("<tbody>");
                    out.println("<tr> <th>  </th> ");
                    out.println(" <th> <h3>"+backupFileName+"</h3></th> </tr>");
                        int backupCount = 0 ;                             
                        for (int i = 0;i<=backupContentArr.length ; i++ ){  
                            
                            if(i  >= backupContentArr.length){
                                out.println("<tr>");
                                out.println("<td>"+(i+1) +"</td>");
                                out.println("<td>  \t </td>");
                                out.println("</tr>");
                            }else{
                                int startpoint = dpArr.get(backupCount).firstFileStartPos -1  ;
                                int endPoint = dpArr.get(backupCount).firstFileEndPos   ;
                                if (startpoint== i ){
                                    while (startpoint < endPoint){
                                       out.println("<tr>");
                                       out.println("<td>"+(i+1) +"</td>");
                                       switch(dpArr.get(backupCount).type){
                                            case 'a':
                                                out.println("<td bgcolor='#B4FFB4'>"+backupContentArr[i] +" </td>");
                                                break ;
                                            case 'd':
                                                out.println("<td bgcolor='#FFA0B4'>"+backupContentArr[i] +" </td>");
                                                break ;
                                            case 'c':
                                                out.println("<td bgcolor='#A0C8FF'>"+backupContentArr[i] +" </td>");
                                                break ;
                                                
                                       }
                                       
                                       out.println("</tr>");
                                       startpoint++ ;
                                       i++ ;
                                    }
                                   if (backupCount < dpArr.size()-1){
                                       backupCount++ ;
                                   }
                                   
                                   
                                }else{
                                   out.println("<tr>");
                                   out.println("<td>"+(i+1) +"</td>");
                                   out.println("<td>"+backupContentArr[i] +" </td>");
                                   out.println("</tr>");
                                }                                
                            }                                                          
                        }
                out.println("</tbody>");
            out.println("</table>");   
        out.println("</div>");    
    }
       
    
  
    private ArrayList<DiffPosition> diff(final PrintWriter out , JSONObject diffObject  ) {
               
        String diffText = diffObject.get("result").toString();      
        StringTokenizer token = new StringTokenizer(diffText,"\n");  

        ArrayList<DiffPosition> dpArr = new ArrayList<DiffPosition>();
        
        
        int count = 0 ;
        String diffToken = "";
        while (token.hasMoreTokens()) {
            diffToken = token.nextToken();
            if ((diffToken.charAt(0) != '>') & (diffToken.charAt(0) != '<')& (diffToken.charAt(0) != '-')){                          
                dpArr.add(extractLineNumbers(diffToken ));
            }
            
            count++ ;
	}        
        return dpArr ;
    }
    
    private DiffPosition extractLineNumbers(String token ){
   
        DiffPosition  dp = new DiffPosition();    
     
        if (token.contains("a")){
            dp.type = 'a';
        }else if(token.contains("d")){
            dp.type = 'd';
        }else if (token.contains("c")){
            dp.type = 'c';
        }

        String [] linesNumber = token.split("[a|d|c]");
        
        String [] firstFileLines = linesNumber[0].split(",");
        dp.firstFileStartPos = Integer.valueOf(firstFileLines[0]);
        if(firstFileLines.length>=2){
            dp.firstFileEndPos = Integer.valueOf(firstFileLines[1]);
                   
        }else {
            dp.firstFileEndPos = Integer.valueOf(firstFileLines[0]);            
        }
 
        
        String [] SecondFileLines = linesNumber[1].split(",");
        dp.secondFileStartPos = Integer.valueOf(SecondFileLines[0]);
        if(SecondFileLines.length>=2){
            dp.secondFileEndPos = Integer.valueOf(SecondFileLines[1]);
                   
        }else {
            dp.secondFileEndPos = Integer.valueOf(SecondFileLines[0]);            
        }       
        
        return dp ;
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
