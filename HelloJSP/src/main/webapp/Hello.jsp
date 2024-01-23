<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import = "java.util.Date"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>HelloJSP</title>
</head>
<body>	
<%=new Date().toString()%>
<%= 2+5 %>
<%= "<br><b>Hôm nay tôi học JSP</b>" %>
</body>
</html>