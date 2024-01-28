package es.us.dit.fjfj.oauthlti;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class CheckAccessToken
 */
@WebServlet("/access")
public class CheckAccessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String[] paramsRequested = { "time", "user" };
	private static final String[] paramsOptional = { "launch_id", "role" };
	private static final long MAX_INTERVAL = 60000L; //1 minuto
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckAccessToken() {
        super();
    }
    
    private String getToken(String data) {
		String token = null;
		try {
			byte[] container = Base64.getDecoder().decode(data);
			byte[] iv = CryptUtil.generateIv();
			if (container.length - iv.length > 0) {
				byte[] ciphertext = new byte[container.length - iv.length];
				System.arraycopy(container, 0, iv, 0, iv.length);
				System.arraycopy(container, iv.length, ciphertext, 0, ciphertext.length);
				token = new String(CryptUtil.decrypt(ciphertext, iv), StandardCharsets.UTF_8);
			} else {
				throw new IllegalBlockSizeException("");
			}
		} catch (IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e1) {
			// a�adir medidas para evitar ataques de fuerza bruta
			try {
				Thread.sleep(MAX_INTERVAL / 10);
			} catch (InterruptedException e) {
				// Restore interrupted state...
				Thread.currentThread().interrupt();
			}

		}
		return token;
    }

    private boolean checkToken(HttpServletRequest request, String authHeader) {
    	boolean res = false;
    	if (authHeader.startsWith("Bearer ")) {
			// desencriptar para garantizar la autenticidad
			String token = getToken(authHeader.substring(7));
			if (token != null) {
				//log(token);
				Map<String, String> paramsReceived = getParamsReceived(token);
				// Comprobamos que han llegado los obligatorios
				boolean missingParams = checkMissingParams(paramsReceived);
				if (!missingParams && checkTime(paramsReceived.get("time"))) {
					// Añadir a sesión resto de parámetros
					setSessionAttributes(request, paramsReceived);
					res = true;
				}
			}
		}
		return res;
    }
    
    private boolean checkTime(String time) {
    	boolean res = false;
    	try {
			Instant iTime = Instant.ofEpochMilli(Long.parseLong(time));
			Duration diff = Duration.between(iTime, Instant.now());
			if (diff.toMillis() > 0 && diff.toMillis() < MAX_INTERVAL) {
				res = true;
			} else {
				log("Token caducado");
			}
		} catch (NumberFormatException e) {
			log("Time incorrecto");
		}
    	return res;
    }

	private boolean checkMissingParams(Map<String, String> paramsReceived) {
		boolean missingParams = false;
		for (String p : paramsRequested) {
			if (!paramsReceived.containsKey(p)) {
				missingParams = true;
				break;
			}
		}
		return missingParams;
	}

	private void setSessionAttributes(HttpServletRequest request, Map<String, String> paramsReceived) {
		HttpSession s = request.getSession();
		for (String p : paramsRequested) {
			s.setAttribute(p, paramsReceived.get(p));
		}
		for (String p : paramsOptional) {
			if (paramsReceived.containsKey(p)) {
				s.setAttribute(p, paramsReceived.get(p));
			}
		}
	}

	private Map<String, String> getParamsReceived(String token) {
		//En esta demo no se soportan par�metros con valores m�ltiples.
		Map<String, String> paramsReceived = new HashMap<>();
		Set<String> prSet = new HashSet<>(Arrays.asList(paramsRequested)); //para b�squedas
		Set<String> poSet = new HashSet<>(Arrays.asList(paramsOptional)); //para b�squedas

		// Obtenemos par�metros y valores
		for (String pair : token.split("&")) {
			String[] t = pair.split("=");
			if (t.length == 2) {
				String key;
				String value;
				try {
					key = URLDecoder.decode(t[0], "UTF-8");
					value = URLDecoder.decode(t[1], "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// Ignore
					key = "";
					value = "";
				}
				if (prSet.contains(key) || poSet.contains(key)) {
					paramsReceived.put(key, value);
				}
			}
		}
		return paramsReceived;
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getHeader("Authorization") != null && checkToken(request, request.getHeader("Authorization"))) {
			String origin = request.getHeader("Origin");
			if ( origin != null) {
				// Allow from any origin
				response.setHeader("Access-Control-Allow-Origin", origin);
				response.setHeader("Access-Control-Allow-Credentials", "true");
				response.setHeader("Access-Control-Max-Age", "3600"); // cache 1 hora
			}
			String redirectUri = response.encodeRedirectURL("chat/chat.jsp");
			response.setHeader("X-Location", redirectUri);
			/* Otro posible cuerpo
			<!DOCTYPE HTML>
			<meta charset="UTF-8">
			<meta http-equiv="refresh" content="1; url=redirectUri">
			<script>window.location.href="redirectUri";</script>
			<title>Page Redirection</title>
			If you are not redirected automatically, follow the <a href='redirectUri'>link to web</a>
			*/
			response.setContentType("application/json;charset=UTF-8");
			try {
				response.getWriter().append("{ \"redirectURI\": \"").append(redirectUri).append("\" }");
			} catch (IOException e) {
				//ignore
			}
		} else {
			response.setHeader("WWW-Authenticate", "Bearer realm=\"Demo Oauth LTI\"");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		/* Acceso al recurso: https://datatracker.ietf.org/doc/html/rfc6750#section-2.1 */
	}


	/**
	 * @see HttpServlet#doOptions(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Hay que contemplar petici�nes con CORS
		String origin = request.getHeader("Origin");
		if (origin != null) {
			// Allow from any origin
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Max-Age", "3600"); // cache 1 hora
		}
		response.setHeader("Access-Control-Expose-Headers", "X-Location");
		
		if (request.getHeader("Access-Control-Request-Method") != null) {
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		}

		if (request.getHeader("Access-Control-Request-Headers") != null) {
			response.setHeader("Access-Control-Allow-Headers", "authorization");
		}

	}

}
