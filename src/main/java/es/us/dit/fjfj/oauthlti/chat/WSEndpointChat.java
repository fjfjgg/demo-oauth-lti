package es.us.dit.fjfj.oauthlti.chat;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Versi�n donde se utiliza el almacen global para hacerlo compatible con el
 * resto de versiones.
 * @author fjfj
 *
 */
@ServerEndpoint(value = "/ws/chat",
	configurator = WebSocketConfigurator.class)
public class WSEndpointChat implements MensajesObserver {

	private static final String PREFIJO_TEXTO = "texto=";
	private AlmacenMensajes almacen = null;
    
	private Session session;
	private String user;
	private String role;
	
	public WSEndpointChat() {
		//no hay que hacer nada
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		this.session = session;
		//Iniciamos almacen, si no existe se crea 
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		if (httpSession != null) {
			@SuppressWarnings("unchecked")
			Map<String, AlmacenMensajes> almacenes = (Map<String, AlmacenMensajes>) httpSession.getServletContext()
					.getAttribute(AlmacenMensajes.class.getName());
			user = (String) httpSession.getAttribute("user");
			if (user == null) {
				user = "desconocido"; //desconocido en base64
			} 
			user = Base64.getUrlEncoder().encodeToString(
					URLEncoder.encode(user,StandardCharsets.UTF_8).getBytes());
			String launchId = (String) httpSession.getAttribute("launch_id");
			if (launchId == null) {
				launchId = "";
			}
			role = (String) httpSession.getAttribute("role");
			if (role == null) {
				role = "";
			}
			synchronized (almacenes) {
				almacen = almacenes.computeIfAbsent(launchId, k -> new AlmacenMensajes());
			}
		}
		
		// Enviar todos los comentarios
		synchronized (almacen) {
			try {
				for (Mensaje c : almacen.getMensajes()) {
					session.getBasicRemote().sendText(comentarioToXML(c));
				}
				almacen.nuevoObservador(this);
			} catch (IOException e) {
				// Cliente se ha desconectado antes de poder enviar.
			}
		}
	}

	@OnClose
	public void onClose() {
		if (almacen != null) {
			almacen.borrarObservador(this);
		}
	}

	@OnMessage
	public void onMessage(String mensaje) {
		// creamos nuevo comentario. Los datos llegan en formato autor=xxx&texto=yyy
		Mensaje c = new Mensaje();
		if (mensaje.startsWith(PREFIJO_TEXTO)) {
			c.setAutor(user);
			c.setTexto(mensaje.substring(PREFIJO_TEXTO.length()));
			almacen.setNuevoMensaje(c);
		} else if (mensaje.equals("limpiar-comentarios") && role.equals("profesor")) {
			synchronized (almacen) {
				almacen.getMensajes().clear();
			}
		}
	}

	@OnError
	public void onError(Throwable t) {
		// ignore
	}

	private static String comentarioToXML(Mensaje c) {
		return "<comentario><fecha>" + c.getFecha() + "</fecha>" + "<autor>" + c.getAutor() + "</autor>"
				+ "<texto>" + c.getTexto() + "</texto></comentario>";
	}

	@Override
	public void nuevoMensaje(Mensaje c) {
		if (c != null) {
			try {
				session.getBasicRemote().sendText(comentarioToXML(c));
			} catch (IOException e) {
				try {
					session.close();
				} catch (IOException e1) {
					// Ignore
				}
			}
		}
	}
}
