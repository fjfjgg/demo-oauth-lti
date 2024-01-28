package es.us.dit.fjfj.oauthlti;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetAccessToken
 */
@WebServlet("/token")
public class GetAccessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String PARAM_GRANT_TYPE = "grant_type";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAccessToken() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Client Credentials Grant: https://datatracker.ietf.org/doc/html/rfc6749#section-4.4 */
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		//Requisitos mínimos
		if (request.getContentType() != null && 
				request.getContentType().startsWith("application/x-www-form-urlencoded") && 
				request.getParameter(PARAM_GRANT_TYPE) != null &&
				request.getParameter(PARAM_GRANT_TYPE).equals("client_credentials")) {
			//Correcto
			response.getWriter().append("{\n  \"token_type\":\"bearer\",\n  \"access_token\":\"")
				.append(getToken(request.getParameterMap())).append("\"\n}");

		} else {
			//Incorrecto
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().append("{\n  \"error\":\"invalid_request\"\n}");
		}
	}
	
	private String getToken(Map<String, String[]> parameters) {
		Map<String, String[]> params = new HashMap<>(parameters);
		params.remove(PARAM_GRANT_TYPE);
		params.put("time", new String[] {String.valueOf( Instant.now().toEpochMilli())});
		params.put("_", new String[] {CryptUtil.createRandomBase64String(32)});
		String data = getDataString(params);
		//encriptar data con una clave aleatoria de la aplicación, MAC
		byte[] iv = CryptUtil.generateIv();
		String token;
		try {
			byte[] ciphertext = CryptUtil.encrypt(data.getBytes(StandardCharsets.UTF_8), iv);
			byte[] container = new byte[ciphertext.length + iv.length];
			System.arraycopy(iv, 0, container, 0, iv.length);
			System.arraycopy(ciphertext, 0, container, iv.length, ciphertext.length);
			token = new String(Base64.getEncoder().encode(container), StandardCharsets.UTF_8);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			log("Generando sin encriptar");
			token = new String(Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8)),
					StandardCharsets.UTF_8);
		}
		
		return token;
	}
	
	private String getDataString(Map<String, String[]> params) {
	    StringBuilder result = new StringBuilder();
	    boolean first = true;
	    for(Map.Entry<String, String[]> entry : params.entrySet()){
	    	for (String value: entry.getValue()) {
		        try {
					String key = URLEncoder.encode(entry.getKey(), "UTF-8");
					String val = URLEncoder.encode(value, "UTF-8");
					if (first) {
			            first = false;
			        } else {
			            result.append("&");
			        }
			        result.append(key).append("=").append(val);
				} catch (UnsupportedEncodingException e) {
					//ignore
					this.log("Error in URLEncoder", e);
				}
	    	}
	    }    
	    return result.toString();
	}

}
