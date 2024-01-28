<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
<!--/*FJFJ*/-->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Mensajes</title>
<link href="estilo.css" rel="stylesheet" type="text/css"/>
<link href="chat.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" charset="UTF-8" src="chat.js"></script>
</head>
<body>
	<div id="titulo">
		<h1>Mensajes</h1>
		<h1><span id="subtitulo">${launch_id}</span></h1>
	</div>
	<div id="divform">
		<form id="datos" accept-charset="UTF-8">
			<label>Autor: ${user}</label><br />
			<input type="text" name="texto" id="texto" />
			<input type="button" id="boton" value="Enviar"/>
			<input type="button" id="boton_logout" value="Salir"/>
			<% if ("profesor".equals(session.getAttribute("role")) ) { %>
			  <input type="button" id="boton_borrar" value="Borrar mensajes" onclick="ws.send('limpiar-comentarios');" />	
			  <input type="button" id="boton_ayuda" value="Ayuda" onclick="elem('ayuda').style.display='block';" />
			<% } %>
			<div id="resultadoEnvio"></div>
			<p></p>	
		</form>
	</div>
	<div id="cuerpo">
		<h3>Mensajes (<span id="total">0</span>)</h3>
		<div id="comentarios">
		
		</div>
	</div>
	<% if ("profesor".equals(session.getAttribute("role")) ) { %>
	<div id="ayuda">
	<h1>Ayuda</h1>
	<h2>Aplicación de intercambio de mensajes integrada con enseñanza virtual</h2>
	<p>Soporta distintas salas de conversación, cada sala está identificada por el curso y el nombre
	del enlace. Si desea crear una nueva sala, simplemente edite el enlace que ha usado para acceder 
	a la aplicación, copie los valores y cree un nuevo enlace con los mismos valores cambiando el 
	nombre o curso. Es preferible configurar que el enlace NO se abra en una nueva ventana, porque
	Firefox (al menos hasta la versión 98) no funciona adecuadamente así.</p>
	<p>Los mensajes se guardan en memoria, por lo que si se reinicia el servidor se pierden. Los mensajes
	se pueden borrar. El borrado de mensajes solo afecta a los usuarios que acceden posteriormente.</p> 
	<p>El nombre del usuario se obtiene de enseñanza virtual. También se obtiene el tipo de usuario
	(profesor o estudiante). Ver esta ayuda y borrar los comentarios son acciones que solo puede realizar
	el profesor.</p>
	</div>
	<% } %>
</body>
</html>