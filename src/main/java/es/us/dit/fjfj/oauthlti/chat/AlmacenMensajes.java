/**
 * 
 */
package es.us.dit.fjfj.oauthlti.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fjfj
 * Almacena una lista de comentarios
 */
public class AlmacenMensajes {
	
	private List<Mensaje> mensajes;
	private List<MensajesObserver> observadores;

	/**
	 * Constructor
	 */
	public AlmacenMensajes() {
		mensajes = new ArrayList<>();
		observadores = new ArrayList<>();
	}
	
	public List<Mensaje> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}
	
	public int getTotal() {
		return mensajes.size();
	}
	
	public synchronized void setNuevoMensaje(Mensaje nuevo) {
		if ( nuevo.getAutor()!=null && nuevo.getTexto()!=null ) {
			mensajes.add(nuevo);
			for (MensajesObserver obs: observadores) {
				obs.nuevoMensaje(nuevo);
			}
		}
	}
	
	public synchronized void nuevoObservador(MensajesObserver obs) {
		observadores.add(obs);
	}
	
	public synchronized void borrarObservador(MensajesObserver obs) {
		observadores.remove(obs);
	}
}
