package uoc.ded.practica.model;

import java.time.LocalDate;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;

public class Group implements Comparable<Group>, IdComparable{
	private String groupId;
	@SuppressWarnings("unused")
	private String description;
	private LocalDate creationDate;
	private Lista<User> users;
	
	public Group(String groupId, String description, LocalDate creationDate, Lista<User> users) {
		this.groupId = groupId;
		this.description = description;
		this.creationDate = creationDate;
		this.users = users;
	}
	public boolean hasMembers() {
		return !users.estaVacio();
	}
	public int numMembers() {
		return users.numElems();
	}
	public Iterador<User> getMembers(){
		return users.elementos();
	}

	public String getId(){
		return groupId;
	}
	public LocalDate getDate(){
		return creationDate;
	}
	public Double getValue(LocalDate date) {
		Iterador<User> it = users.elementos();
		double badgeValue= 0;
		while(it.haySiguiente()) {
			 badgeValue += it.siguiente().getBadge(date).getValue();
		}
		return badgeValue / numMembers();
	}
	@Override
	public int compareTo(Group o) {
		return groupId.compareTo(o.getId());
	}
	@Override
	public int compareTo(String id) {
		return groupId.compareTo(id);
	}
}
