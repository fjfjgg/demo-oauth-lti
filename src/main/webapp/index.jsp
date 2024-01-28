<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Página inicial</title>
</head>
<body>
<h1>Página inicial</h1>
<%if (request.getSession(false)!=null &&  request.getSession(false).getAttribute("user")!=null) { %>
	<p>Acceso correcto.</p>
	<p><a href='<%=response.encodeRedirectURL("datos.jsp") %>'>Datos</a></p>
	<p><a href='<%=response.encodeRedirectURL("chat/chat.jsp") %>'>Chat</a></p>
<%} else { %>
	<p>No tiene acceso a esta página.</p>
<%} %>
</body>
</html>