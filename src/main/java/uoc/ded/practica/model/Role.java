package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import uoc.ei.tads.Posicion;
import uoc.ei.tads.Recorrido;

public class Role {
	String roleId;
	String name;
	private Lista<Worker> users;
	
	public Role(String roleId, String name) {
		this.roleId = roleId;
		this.name = name;
		users = new ListaEncadenada<Worker>();
	}
	public String getId() {
		return roleId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void addWorker(Worker user) {
		users.insertarAlFinal(user);
	}

	public void removeUser(String userId) {
		boolean deleted = false;
		Recorrido<Worker> rec = users.posiciones();
		while(rec.haySiguiente() && !deleted){
			Posicion<Worker> next = rec.siguiente();
			if(next.getElem().getId() == userId) {
				users.borrar(next);
				deleted = true;
			}
		}
	}
	public int getNumUsers() {
		return users.numElems();
	}
	public Iterador<Worker> getWorkers() {
		return users.elementos();
	}
}
