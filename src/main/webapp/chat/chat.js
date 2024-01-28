/*FJFJ*/

// Variables globales
var ultComentarioObtenido = 0;
var ws = null;

// Codificación de cadenas, del navegador al servidor
// Para evitar problemas con los caracteres UTF-8
// Más información https://developer.mozilla.org/en-US/docs/Web/API/Window.btoa
function utf8_to_b64(str) {
	// escape() codifica caracteres especiales
	// encodeURIComponent codifica más caracteres especiales
	// btoa comvierte la cadena en base64
	return window.btoa(encodeURIComponent(escape(str)));
}

// Decodificación de cadenas, del servidor al navegador
// Para evitar problemas con los caracteres UTF-8
function b64_to_utf8(str) {
	// Se hace lo inverso a utf8_to_b64
	return unescape(decodeURIComponent(window.atob(str))).replace(/</g, "&lt;")
			.replace(/>/g, "&gt;");
}

// Función para acortar las llamadas a getElementById
function elem(id) {
	return document.getElementById(id);
}

function enviarComentario() {
	var comentario = elem("texto").value;
	// IMPORTANTE, los campos se envían codificados en base64, para que los
	// caracteres no ascii no den problemas
	var datos = "texto=" + utf8_to_b64(comentario);
	// Ocultamos el estado del último envio
	elem("resultadoEnvio").style.visibility = "hidden";
	try {
		//Enviamos
		if (ws.readyState == ws.OPEN )
			ws.send(datos);
		else
			throw "error";
		elem("resultadoEnvio").innerHTML = "<p class='info'>Envío correcto</p>";
	} catch (error) {
		elem("resultadoEnvio").innerHTML = "<p class='error'>Envío incorrecto</p>";
	} finally {
		elem("resultadoEnvio").style.visibility = "visible";
	}
	setTimeout(function() {elem("resultadoEnvio").style.visibility = "";}, 2000);
}


function recibirComentario(datos) {
	ultComentarioObtenido++;
	// Escribimos HTML
	var parser = new DOMParser();
	var xmlDoc = parser.parseFromString(datos, "text/xml");
	var autor = xmlDoc.getElementsByTagName("autor")[0].childNodes[0].nodeValue;
	var txt = xmlDoc.getElementsByTagName("texto")[0].childNodes[0].nodeValue;
	var fecha = xmlDoc.getElementsByTagName("fecha")[0].childNodes[0].nodeValue;
	var divNuevo = document.createElement("div");
	divNuevo.className = "divComentario";
	// IMPORTANTE, los campos se reciben codificados en base64, para que
	// los
	// caracteres no ascii no den problemas (menos el campo fecha)
	divNuevo.innerHTML = "<span class='idcomentario'>"
			+ ultComentarioObtenido + " - </span>"
			+ "<span class='autor'>" + b64_to_utf8(autor) + "</span>"
			+ "<span class='fecha'>" + fecha + "</span>"
			+ "<span class='comentario'>" + b64_to_utf8(txt)
			+ "</span>";
	var comentarios = elem("comentarios");
	comentarios.insertBefore(divNuevo, comentarios.firstChild);
	elem("total").innerHTML = ultComentarioObtenido;
}

function logout() {
	let xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			console.log("logout");
		}
	};
	xmlhttp.open("GET", "../logout", false);
	xmlhttp.send();
	//try to close windows
	ws.close();
	window.close();
	window.location.href="../index.jsp";
}

function iniciarChat() {
	elem("boton").onclick = enviarComentario;
	elem("boton_logout").onclick = logout;
	// Si se pulsa intro en el campo texto, se envia el comentario
	// inmediatamente también
	elem("texto").onkeypress = function(event) {
		if (event.keyCode == 13) {
			event.preventDefault();
			enviarComentario();
			this.value="";
		}
	};
	elem("texto").focus();
	var loc = window.location;
	var new_uri;
	if (loc.protocol === "https:") {
	    new_uri = "wss:";
	} else {
	    new_uri = "ws:";
	}
	new_uri += "//" + loc.host 
		+ loc.pathname.substring(0, loc.pathname.lastIndexOf('/')) + "/../ws/chat"
	ws = new WebSocket(new_uri);
	
	ws.onmessage = function(event) {
		recibirComentario(event.data);
	};
	ws.onclose = function(event) {
		console.log("ws closed");
		elem("resultadoEnvio").innerHTML = "<p class='error'>Conexión cerrada.<br /><a href=''>Recargar</a></p>";
		elem("resultadoEnvio").style.visibility = "visible";
	}
	ws.onerror = function(event) {
		console.log("ws error");
	}
}

window.addEventListener("load", iniciarChat);
