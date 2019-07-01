<%-- 
    Document   : header
    Created on : May 11, 2019, 10:14:05 AM
    Author     : motaz
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    String version = request.getAttribute("version").toString();
    String logouttext = request.getAttribute("logouttext").toString();
    String selectedpbx = request.getAttribute("selectedpbx").toString();

%>

<html lang="en">
    <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <title>Simple Trunk Panel</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <meta name="Description" lang="en" content="ADD SITE DESCRIPTION">
            <meta name="author" content="Code for Computer Software">
            <meta name="robots" content="index, follow">

            <!-- icons -->
            <link rel="apple-touch-icon" href="img/apple-touch-icon.png">
            <link rel='shortcut icon' href='img/icon.png'>

            <!-- Override CSS file - add your own CSS rules -->
            <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
    <div class=header>
    <div class=container>
            <table ><tr bgcolor=#cccc88 >
                    <td class=titletd style="color:red;background-color: #cccc88; vertical-align:bottom;font-size:30px;">
                        &emsp;Simple Trunk&emsp; </td>
            <td class=titletd><td bgcolor=#cccc88 style='color:black;vertical-align:bottom;'>
                <%=logouttext%></td>
            <td bgcolor=#cccc88 style='vertical-align:bottom;'><%=selectedpbx%></td>
            <td bgcolor=#cccc88 style='vertical-align:bottom;font-size:12px; color:black;'>
                Version <%=version%> 
            </td>
                </tr>
            </table>	
                </div>
            	</div>
            	<div class="nav-bar">
            	<div class="container">
            	<ul class="nav"> 
