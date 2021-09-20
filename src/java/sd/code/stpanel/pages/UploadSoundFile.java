/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sd.code.stpanel.common.General;
import sd.code.stpanel.common.Web;

/**
 *
 * @author motaz
 */
@MultipartConfig
@WebServlet(name = "UploadSoundFile", urlPatterns = {"/UploadSoundFile"})
public class UploadSoundFile extends HttpServlet {
    private static final long serialVersionUID = 1L;


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
	//   File logFileToUpload;
	try (PrintWriter out = response.getWriter()) {
	    //   File logFileToUpload;
	    String filename = "";
	    JSONObject jsonrequest = new JSONObject();
	    
	    // Prepare upload
	    String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
	    String agenturl = General.getConfigurationParameter("url", "", pbxfile);	
	    String uploadurl = agenturl + "ReceiveFile";

	    String dir = request.getParameter("dir");
		 
	    URL serverUrl = new URL(uploadurl);
	    HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

	    
	    // Indicate that we want to write to the HTTP request body
	    urlConnection.setDoOutput(true);
	    //urlConnection.setDoInput(true);
	    urlConnection.setRequestMethod("POST");
	    

	    // Indicate that we want to write some data as the HTTP request body
	    urlConnection.setDoOutput(true);
	    urlConnection.setDoInput(true);

	    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
	    
	    // Read
	    
	    if (ServletFileUpload.isMultipartContent(request)) {
		for (Part part : request.getParts()) {
		    
		    String name = part.getName();
		    General.writeEvent(name, "stagent");
		    
		    String disposition = part.getHeader("content-disposition");
		    if (disposition.contains("filename")){

			filename = disposition.substring(disposition.indexOf("filename=") + 10, disposition.length());
			filename = filename.substring(0, filename.indexOf("\""));
			General.writeEvent(filename, "stagent");
			jsonrequest.put("filename", filename);
			jsonrequest.put("dir", dir);
			JSONArray arr = new JSONArray();


			writer.flush();
			
			try ( // Input stream from part
			    InputStream is = request.getPart(name).getInputStream()) {
			    byte data[] = new byte[1024];
			    int len;
			    
			    while ((len = is.read(data)) != -1) {
				    byte buf[] = Arrays.copyOf(data, len);
				    System.arraycopy(data, 0, buf, 0, len -1);
                                    String content = Base64.getEncoder().encodeToString(buf);
				    //String content =  DatatypeConverter.printBase64Binary(buf);
				    arr.add(content);				  
			    }
			    jsonrequest.put("content", arr);
			    writer.write(jsonrequest.toJSONString());
			   
			}
		    }
		}
		General.writeEvent("File [" + filename +"]  fileuploaded", "stagent");
	    }
	    else{
		System.out.println("No multipart");
	    }
	    
	    // Finishing
	    writer.flush();
	    // Mark the end of the multipart http request
	    
	    writer.close();
	    
	    // Read result
	    String line = "";
	    String result = "";
	    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    while ((line = reader.readLine()) != null) {
		
		   result = result + line + "\n";
		
	    }

            String rdir = URLEncoder.encode( dir, "UTF-8");
            String amessage = URLEncoder.encode( result, "UTF-8");
            response.sendRedirect("UploadSound?rdir=" + rdir + "&message=" + amessage);
	    		
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
