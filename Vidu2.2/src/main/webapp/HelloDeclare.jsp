<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Hello-Declare + Code + Expression</title>
</head>
<body>
	<%! int x = 10, y, z=0; %>
	<%
	y = 200;
	z = x+y;
	out.append("Kết quả là: ");
	out.append(String.valueOf(z));
	%>
	<h2>Kiểu Expression</h2>
	<hr>
	<%="Kết quả là: " %>
	<%=z %>
</body>
</html>