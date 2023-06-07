package uoc.ded.practica.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Order implements Comparable<Order>, IdComparable{
    public static final Comparator<Order> CMP = (Order o1, Order o2)->Double.compare(o2.getValue(),o1.getValue());


    private String id;
    private Group group;
    private User user;
    @SuppressWarnings("unused")
	private Activity activity;
    private LocalDate date;
    
	private Lista<Ticket> tickets;
    
	public Order(Group group, Activity activity, LocalDate date) {
		baseConstructor(activity, date);
		this.id = "O-"+date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"-"+ group.getId();
		this.group = group;
		tickets = new ListaEncadenada<Ticket>();
		Iterador<User> members = group.getMembers();
		while(members.haySiguiente()) {
			User user =  members.siguiente();
			tickets.insertarAlFinal(new Ticket(user,activity));
			user.addActivity(activity);
		}
	}
	public Order(User user, Activity activity, LocalDate date) {
		baseConstructor(activity, date);
		this.id = "O-"+date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"-"+ user.getId();
		this.user = user;
		tickets = new ListaEncadenada<Ticket>();
		tickets.insertarAlFinal(new Ticket(user, activity));
		user.addActivity(activity);
	}
	
	
	public void baseConstructor(Activity activity, LocalDate date) {
		this.activity = activity;		
		this.date = date;
	}
    
	
	public Iterador<Ticket> tickets() {
		return tickets.elementos();
	}

	public int numberOfTickets() {
		return tickets.numElems();
	}
	public String getId() {
		return id;
	}
	public double getValue(){
		return group != null ? group.getValue(date) : user.getBadge(date).getValue();
	}
	@Override
	public int compareTo(Order o) {
		return getId().compareTo(o.getId());
	}
	@Override
	public int compareTo(String id) {
		return getId().compareTo(id);
	}
	
}
