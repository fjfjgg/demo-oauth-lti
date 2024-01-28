package es.us.dit.fjfj.oauthlti.chat;

import java.util.HashMap;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class AppComentariosListener
 *
 */
@WebListener
public class AppChatListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public AppChatListener() {
    	// no hay que hacer nada
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event)  {
    	// nada que hacer
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event)  {
    	event.getServletContext().log("AppComentarios");
    	event.getServletContext().setAttribute(AlmacenMensajes.class.getName(),
        		 new HashMap<String, AlmacenMensajes>());
    }
	
}
