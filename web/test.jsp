<%-- 
    Document   : test
    Created on : May 22, 2016, 2:42:32 PM
    Author     : motaz
--%>

<%@page import="java.util.Date"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
	<%
	    Date date = new Date();
	    out.println(date.toString());
        %>
    </body>
</html>
