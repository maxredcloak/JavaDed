package uoc.ded.practica.model;

import java.time.LocalDate;
import java.util.Comparator;
import uoc.ded.practica.SafetyActivities4Covid19;
import uoc.ei.tads.ColaConPrioridad;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Activity implements Comparable<Activity>, IdComparable {
    public static final Comparator<String> CMP_K = (String o1, String o2)->o1.compareTo(o2);
    public static final Comparator<Activity> CMP_V = (Activity a1, Activity a2)->Double.compare(a1.rating(), a2.rating());

    private String actId;
    @SuppressWarnings("unused")
	private String description;
    @SuppressWarnings("unused")
    private LocalDate date;
    @SuppressWarnings("unused")
    private SafetyActivities4Covid19.Mode mode;
    @SuppressWarnings("unused")
    private int total;
    private int nextSeat;
    private int availabilityOfTickets;
    @SuppressWarnings("unused")
    private Record record;
    private ColaConPrioridad<Order> orders;
    private Lista<Rating> ratings;
    private Lista<User> users;
    private int totalRatings;

    public Activity(String actId, String description, LocalDate dateAct, SafetyActivities4Covid19.Mode mode, int num, Record record) {
        this.actId = actId;
        this.description = description;
        this.date = dateAct;
        this.mode = mode;
        this.total = num;
        this.nextSeat = 1;
        this.availabilityOfTickets = num;
        this.record = record;
        orders = new ColaConPrioridad<Order>(Order.CMP);
        ratings = new ListaEncadenada<Rating>();
        users = new ListaEncadenada<User>();
    }

    public String getActId() {
        return actId;
    }


    public boolean hasAvailabilityOfTickets() {
        return (availabilityOfTickets > 0  );
    }
    public boolean hasAvailabilityOfTickets(int qtty) {
        return (availabilityOfTickets > qtty  );
    }

    public Order addOrder(Group group, LocalDate date) {
    	Order newOrder = new Order(group, this, date);
    	orders.encolar(newOrder);
        availabilityOfTickets-= group.numMembers();
        Iterador<User> it = group.getMembers();
        while(it.haySiguiente()) {
        	users.insertarAlFinal(it.siguiente());
        }
        return newOrder;
    }
    public Order addOrder(User user, LocalDate date) {
    	Order newOrder = new Order(user, this, date);
    	orders.encolar(newOrder);
        availabilityOfTickets--;
        users.insertarAlFinal(user);
        return newOrder;
    }

    public Order pop() {
        Order o = orders.desencolar();
        Iterador<Ticket> it = o.tickets();
        while(it != null && it.haySiguiente()) {
        	Ticket t = it.siguiente();
        	t.setSeat(nextSeat++);
        }
        return o;
    }

    public boolean is(String actId) {
        return this.actId.equals(actId);
    }

    public void addRating(SafetyActivities4Covid19.Rating rating, String message, User user) {
        Rating newRating = new Rating(rating, message, user);
        ratings.insertarAlFinal(newRating);
        totalRatings += rating.getValue();
    }

    public double rating() {
        return (ratings.numElems() != 0 ? (double)totalRatings / ratings.numElems() : 0);
    }

    public Iterador<Rating> ratings() {
        return ratings.elementos();
    }

    public boolean hasRatings() {
        return ratings.numElems() > 0;
    }

    public int numPendingTickets() {
        return orders.numElems();
    }

    public int availabilityOfTickets() {
        return availabilityOfTickets;
    }

    @Override
    public int compareTo(Activity o) {
        return actId.compareTo(o.actId);
    }
    public Iterador<User> getUsers(){
    	return users.elementos();
    }

	@Override
	public int compareTo(String id) {
		return actId.compareTo(id);
	}
}
