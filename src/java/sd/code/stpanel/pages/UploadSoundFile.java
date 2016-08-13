/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd.code.stpanel.pages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
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
	
	      if (ServletFileUpload.isMultipartContent(request)) {
		  String filename="";
		  System.out.println("First: Is multipart");
                  for (Part part : request.getParts()) {
          
		    String name = part.getName();
		    General.writeEvent(name, "stagent");
		    
		    String disposition = part.getHeader("content-disposition");
		    if (disposition.contains("filename")){

			filename = disposition.substring(disposition.indexOf("filename=") + 10, disposition.length());
			filename = filename.substring(0, filename.indexOf("\""));
			General.writeEvent(filename, "stagent");
			/*File osf = new File(dir + filename);
			FileOutputStream os = new FileOutputStream(osf);
			InputStream is = request.getPart(name).getInputStream();
			int data;
			while ((data = is.read()) != -1) {
			      os.write(data);
			}
			os.close();
			is.close();*/
		    }	     
	       }
	       General.writeEvent("File [" + filename +"]  fileuploaded", "stagent");
	    }
	    else{
	         System.out.println("No multipart");
	    }	
	   
	
	// Upload
	    String pbxfile = General.getPBXsDir()  + Web.getCookieValue(request, "file");
	    String agenturl = General.getConfigurationParameter("url", "", pbxfile);	
	    String uploadurl = agenturl + "UploadFile";
	    //uploadurl = "http://localhost:9090";
	    System.out.println(uploadurl);

	    String dir = request.getParameter("dir");
	    System.out.println("Dir in middleware: " + dir);
	    URL serverUrl = new URL(uploadurl);
	    //HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();

	    URLConnection urlConnection = serverUrl.openConnection();
	    String boundaryString = "-----------------------------735323031399963166993862150";
	    System.out.println(boundaryString);
	    String fileUrl = "/home/motaz/first.py";
	    File logFileToUpload = new File(fileUrl);

	    // Indicate that we want to write to the HTTP request body
	    urlConnection.setDoOutput(true);
	    //urlConnection.setDoInput(true);
	    //urlConnection.setRequestMethod("POST");
	    
	    urlConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);

	    // Indicate that we want to write some data as the HTTP request body
	    urlConnection.setDoOutput(true);

	    BufferedWriter httpRequestBodyWriter;
	try (OutputStream outputStreamToRequestBody = urlConnection.getOutputStream()) {
	    httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));
	    // Include value from the myFileDescription text area in the post data
	    httpRequestBodyWriter.write("\n\n--" + boundaryString + "\n");
	    httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"myFileDescription\"");
	    httpRequestBodyWriter.write("\n\n");
	    httpRequestBodyWriter.write("Log file for 20150208");
	    // Include the section to describe the file
	    httpRequestBodyWriter.write("\n--" + boundaryString + "\n");
	    httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"dir\"\n" +
		     dir +"\n");
		    
	    httpRequestBodyWriter.write(boundaryString + "\n");
	    
	    httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"myFile\"; filename=\""+ logFileToUpload.getName() +"\"\n"
					+ "\nContent-Type: text/plain\n\n");
	    
	    httpRequestBodyWriter.flush();
	    // Write the actual file contents
	    FileInputStream inputStreamToLogFile = new FileInputStream(logFileToUpload);
	    int bytesRead;
	    byte[] dataBuffer = new byte[1024];
	    while((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
		outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
	    }
	    outputStreamToRequestBody.flush();
	    // Mark the end of the multipart http request
	    httpRequestBodyWriter.write("\n" + boundaryString + "--\n");
	    httpRequestBodyWriter.flush();
	    // Close the streams
	}
	    httpRequestBodyWriter.close();
	    
	    // Read result
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    while ((line = reader.readLine()) != null) {

		    System.out.println(line);

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
