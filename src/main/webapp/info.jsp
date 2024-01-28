<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.Map,es.us.dit.fjfj.oauthlti.chat.*"%>
<% 
String user = (String) session.getAttribute("user");
String role = (String) session.getAttribute("role");
String launchId = (String) session.getAttribute("launch_id");
if ("test".equals(user) && "profesor".equals(role)) { 
	@SuppressWarnings("unchecked")
	Map<String, AlmacenMensajes> almacenes = (Map<String, AlmacenMensajes>) application
			.getAttribute(AlmacenMensajes.class.getName());
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Info</title>
<style>
table, th, td {
  border: 1px solid black;
  border-collapse: collapse;
}
</style>
</head>
<body>
<table>
<caption>Salas activas y mensajes:</caption>
<tr><th>Sala</th><th>Mensajes</th></tr>
<%
	for (Map.Entry<String, AlmacenMensajes> entry : almacenes.entrySet()) {
%>
	<tr><td><strong>[<%=entry.getKey() %>]</strong></td><td><%=entry.getValue().getTotal() %></td></tr>
<%
	}
%>
</table>
</body>
</html>
<%
} else {
	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
}
%>
