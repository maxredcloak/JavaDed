package uoc.ded.practica;

import java.time.LocalDate;
import java.util.Date;
import java.util.Vector;

import uoc.ded.practica.exceptions.ActivityNotFoundException;
import uoc.ded.practica.exceptions.GroupNotFoundException;
import uoc.ded.practica.exceptions.LimitExceededException;
import uoc.ded.practica.exceptions.NoActivitiesException;
import uoc.ded.practica.exceptions.NoOrganizationException;
import uoc.ded.practica.exceptions.NoRatingsException;
import uoc.ded.practica.exceptions.NoRecordsException;
import uoc.ded.practica.exceptions.NoUserException;
import uoc.ded.practica.exceptions.NoWorkersException;
import uoc.ded.practica.exceptions.OrderNotFoundException;
import uoc.ded.practica.exceptions.OrganizationNotFoundException;
import uoc.ded.practica.exceptions.UserNotFoundException;
import uoc.ded.practica.exceptions.UserNotInActivityException;
import uoc.ded.practica.model.Activity;
import uoc.ded.practica.model.Group;
import uoc.ded.practica.model.IdComparable;
import uoc.ded.practica.model.Order;
import uoc.ded.practica.model.Organization;
import uoc.ded.practica.model.Record;
import uoc.ded.practica.model.Role;
import uoc.ded.practica.model.User;
import uoc.ded.practica.model.Worker;
import uoc.ded.practica.util.OrderedVector;
import uoc.ei.tads.ArbolAVL;
import uoc.ei.tads.ColaConPrioridad;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import uoc.ei.tads.Posicion;
import uoc.ei.tads.TablaDispersion;

public class SafetyActivities4Covid19Impl implements SafetyActivities4Covid19{
	private ArbolAVL<Activity> activities;
	private ArbolAVL<Order> orders;
	private ArbolAVL<Group> groups;
    
	private ColaConPrioridad<Record> records;
	
    private TablaDispersion<String, User> users;
    private TablaDispersion<String, Organization> organizations;
    
    private OrderedVector<Organization> bestOrganizations;
    private OrderedVector<Activity> bestActivities;
    
    private Vector<Role> roles;
    
    private User mostActiveUser;
	
    private int totalRecords;
    private int rejectedRecords;
    
    private int numOrganizations;    
    private int numUsers;
    private int numWorkers;
    
    public SafetyActivities4Covid19Impl() {
        users = new TablaDispersion<String, User>();
        numUsers = 0;
        numWorkers = 0;
        organizations = new TablaDispersion<String, Organization>();
        numOrganizations = 0;
        records = new ColaConPrioridad<Record>(Record.CMP);
        activities = new ArbolAVL<Activity>();
        totalRecords = 0;
        rejectedRecords = 0;
        mostActiveUser = null;
        bestActivities = new OrderedVector<Activity>(BEST_10_ACTIVITIES, Activity.CMP_V);
        bestOrganizations = new OrderedVector<Organization>(BEST_ORGANIZATIONS, Organization.CMP_V);
        orders = new ArbolAVL<Order>();
        groups = new ArbolAVL<Group>();
        roles = new Vector<Role>(R);
    }
    
	@Override
	public void addUser(String userId, String name, String surname, LocalDate birthday, boolean covidCertificate) {
        User u = getUser(userId);
        if (u != null) {
            u.setName(name);
            u.setSurname(surname);
            u.setBirthday(birthday);
            u.setCovidCertificate(covidCertificate);
        } else {
            u = new User(userId, name, surname, birthday, covidCertificate);
            addUser(u);
        }
	}
    public void addUser(User user) {
        users.insertar(user.getId(), user);
        numUsers++;
    }
	@Override
	public void addOrganization(String organizationId, String name, String description) {
        Organization organization = getOrganization(organizationId);
        if (organization != null) {
            organization.setName(name);
            organization.setDescription(description);
        } else {
            organization = new Organization(organizationId, name, description);
            organizations.insertar(organizationId,organization);
            numOrganizations++;
        }
	}

	@Override
	public void addRecord(String recordId, String actId, String description, Date date, LocalDate dateRecord, Mode mode,
			int num, String organizationId) throws OrganizationNotFoundException {
        Organization organization = getOrganization(organizationId);
        if (organization == null) {
        	throw new OrganizationNotFoundException();
        }
        Record record = new Record(recordId, actId, description, dateRecord, mode, num, organization);
        organization.addRecord(record);
        records.encolar(record);
        totalRecords++;
		
	}

	@Override
	public Record updateRecord(Status status, Date date, String description) throws NoRecordsException {
        if(records.estaVacio()) {
        	throw new NoRecordsException();
        }
		Record record = records.desencolar();
        record.update(status, date, description);
        if (record.isEnabled()) {
            Activity activity = record.newActivity();
            activities.insertar(activity);
        }
        else {
        	rejectedRecords++;
        }
        return record;
	}

	@Override
	public Order createTicket(String userId, String actId, LocalDate date)
			throws UserNotFoundException, ActivityNotFoundException, LimitExceededException {

        User user = getUser(userId);
        if (user == null) {
        	throw new UserNotFoundException();
        }

        Activity activity = getActivity(actId);
        if (activity  == null) {
        	throw new ActivityNotFoundException();
        }

        if (!activity.hasAvailabilityOfTickets()) {
        	throw new LimitExceededException();
        }
        Order newOrder = activity.addOrder(user, date);
        orders.insertar(newOrder);
        user.addActivity(activity);
        updateMostActiveUser(user);
        return newOrder;
	}

    public void updateMostActiveUser(User user) {
        if (mostActiveUser == null) {
            mostActiveUser = user;
        }
        else if (user.numActivities() > mostActiveUser.numActivities()) {
            mostActiveUser = user;
        }
    }
    
	@Override
	public Order assignSeat(String actId) throws ActivityNotFoundException {
        Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }
        Order order = activity.pop();
        return order;
	}

	@Override
	public void addRating(String actId, Rating rating, String message, String userId)
			throws ActivityNotFoundException, UserNotFoundException, UserNotInActivityException {
        Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }

        User user = getUser(userId);
        if (user == null) {
        	throw new UserNotFoundException();
        }

        if (!user.isInActivity(actId)) {
        	throw new UserNotInActivityException();
        }

        activity.addRating(rating, message, user);
        updateBestActivity(activity);
	}
    private void updateBestActivity(Activity activity) {
    	bestActivities.delete(activity);
    	bestActivities.update(activity);
    }
	@Override
	public Iterador<uoc.ded.practica.model.Rating> getRatings(String actId)
			throws ActivityNotFoundException, NoRatingsException {
        Activity activity = getActivity(actId);
        if (activity  == null) {
        	throw new ActivityNotFoundException();
        }

        if (!activity.hasRatings()) {
        	throw new NoRatingsException();
        }

        return activity.ratings();
	}

	@Override
	public Activity bestActivity() throws ActivityNotFoundException {
        if (bestActivities.numElems() == 0) {
        	throw new ActivityNotFoundException();
        }
        return bestActivities.elementAt(0);
	}

	@Override
	public User mostActiveUser() throws UserNotFoundException {
        if (mostActiveUser == null) {
        	throw new UserNotFoundException();
        }
        return mostActiveUser;
	}

	@Override
	public double getInfoRejectedRecords() {
        return (double)rejectedRecords / totalRecords;
	}

	@Override
	public Iterador<Activity> getAllActivities() throws NoActivitiesException {
        if (activities.numElems() == 0) {
        	throw new NoActivitiesException();
        }
        return activities.elementos();
	}

	@Override
	public Iterador<Activity> getActivitiesByUser(String userId) throws NoActivitiesException {
        User user = getUser(userId);

        if (!user.hasActivities()) {
        	throw new NoActivitiesException();
        }
        return user.activities();
	}

	@Override
	public User getUser(String userId) {
		return users.consultar(userId);
	}

	@Override
	public Organization getOrganization(String organizationId) {
		return organizations.consultar(organizationId);
	}

	@Override
	public Record currentRecord() {
		return (records.numElems() > 0 ? records.primero() : null);
	}

	@Override
	public int numUsers() {
		return numUsers;
	}

	@Override
	public int numOrganizations() {
		return numOrganizations;
	}

	@Override
	public int numPendingRecords() {
		return records.numElems();
	}

	@Override
	public int numRecords() {
		return totalRecords;
	}

	@Override
	public int numRejectedRecords() {
        return rejectedRecords;
	}

	@Override
	public int numActivities() {
        return activities.numElems();
	}

	@Override
	public int numActivitiesByOrganization(String organizationId) {
        Organization organization = getOrganization(organizationId);
        return organization.numActivities();
	}

	@Override
	public int numRecordsByOrganization(String organizationId) {
		return organizations.consultar(organizationId).numRecords();
	}

	@Override
	public Activity getActivity(String actId) {
        return ArbolSearch(activities, actId);
	}

	@Override
	public int availabilityOfTickets(String actId) {
        Activity activity = getActivity(actId);
        return (activity != null ? activity.availabilityOfTickets() : 0);
	}

	@Override
	public void addRole(String roleId, String name) {
		Role role = getRole(roleId);
		if(role != null) {
			role.setName(name);
		}else {
			roles.add(new Role(roleId,name));
		}
	}

	@Override
	public void addWorker(String userId, String name, String surname, LocalDate birthday, boolean covidCertificate,
			String roleId, String organizationId) {
        Worker w = getWorker(userId);
        if (w != null) {
            w.setName(name);
            w.setSurname(surname);
            w.setBirthday(birthday);
            w.setCovidCertificate(covidCertificate);
            if(w.getRoleId() != roleId) {
            	getRole(w.getRoleId()).removeUser(userId);
            	getRole(roleId).addWorker(w);
            	w.setRoleId(roleId);
            } 
            if(w.getOrganizationId() != organizationId) {
            	organizations.consultar(w.getOrganizationId()).removeWorker(w.getId());
            	organizations.consultar(organizationId).addWorker(w);
            	w.setOrganizationId(organizationId);
            }
        } else {
            w = new Worker(userId, name, surname, birthday, covidCertificate,roleId,organizationId);
            organizations.consultar(organizationId).addWorker(w);
            getRole(roleId).addWorker(w);
            addUser(w);
            numWorkers++;
        }
	}

	@Override
	public Iterador<Worker> getWorkersByOrganization(String organizationId)
			throws OrganizationNotFoundException, NoWorkersException {
		Organization organization = organizations.consultar(organizationId);
		if(organization == null) {
			throw new OrganizationNotFoundException();
		}
		if(organization.numWorkers() == 0) {
			throw new NoWorkersException();
		}
		return organization.workers();
	}

	@Override
	public Iterador<User> getUsersInActivity(String activityId) throws ActivityNotFoundException, NoUserException {
		Activity act = getActivity(activityId);
		if(act == null) {
			throw new ActivityNotFoundException();
		}
		if(!act.getUsers().haySiguiente()) {
			throw new NoUserException();
		}
		return act.getUsers();
	}

	@Override
	public Badge getBadge(String userId, LocalDate day) throws UserNotFoundException {
		User user = users.consultar(userId);
		if(user == null) {
			throw new UserNotFoundException();
		}
		return user.getBadge(day);
	}

	@Override
	public void addGroup(String groupId, String description, LocalDate date, String... members) {
		Lista<User> list = new ListaEncadenada<User>();
		for(int i = 0 ; i < members.length; i++) {
			User member = users.consultar(members[i]);
			list.insertarAlFinal(member);
		};
		groups.insertar(new Group(groupId,description,date,list));
	}

	@Override
	public Iterador<User> membersOf(String groupId) throws GroupNotFoundException, NoUserException {
		Group g = getGroup(groupId);
		if(g == null) {
			throw new GroupNotFoundException();
		}
		if(!g.getMembers().haySiguiente()) {
			throw new NoUserException();
		}
		return getGroup(groupId).getMembers();
	}

	@Override
	public double valueOf(String groupId) throws GroupNotFoundException {
		Group group = getGroup(groupId);
		if(group == null) {
			throw new GroupNotFoundException();
		}
		return group.getValue(LocalDate.now());
	}

	@Override
	public Order createTicketByGroup(String groupId, String actId, LocalDate date)
			throws GroupNotFoundException, ActivityNotFoundException, LimitExceededException {
		Group group = getGroup(groupId);
		if(group == null) {
			throw new GroupNotFoundException();
		}
		Activity activity = getActivity(actId);
		if(activity == null) {
			throw new ActivityNotFoundException();
		}
		if(!activity.hasAvailabilityOfTickets(group.numMembers())) {
			throw new LimitExceededException();
		}
		Order order = activity.addOrder(group, date);
		orders.insertar(order);
		return order;
	}
	

	@Override
	public Order getOrder(String orderId) throws OrderNotFoundException {
		Order order = ArbolSearch(orders, orderId);
		if(order == null) {
			throw new OrderNotFoundException();
		}
        return order;
	}

	@Override
	public Iterador<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
		Role r = getRole(roleId);
		if(!r.getWorkers().haySiguiente()) {
			throw new NoWorkersException();
		}
		return r.getWorkers();
	}

	@Override
	public Iterador<Activity> getActivitiesByOrganization(String organizationId) throws NoActivitiesException {
		if(organizations.consultar(organizationId).numActivities() == 0) {
			throw new NoActivitiesException();
		}
		return organizations.consultar(organizationId).activities();
	}

	@Override
	public Iterador<Record> getRecordsByOrganization(String organizationId) throws NoRecordsException {
		Organization o = organizations.consultar(organizationId);
		if(!o.getRecords().haySiguiente()) {
			throw new NoRecordsException();
		}
		return o.getRecords();
	}

	@Override
	public Iterador<Organization> best5Organizations() throws NoOrganizationException {
		if(bestOrganizations.numElems() == 0) {
			throw new NoOrganizationException();
		}
		return bestOrganizations.elementos();
	}

	@Override
	public Worker getWorker(String workerId) {
		if(users.consultar(workerId) instanceof Worker) {
			return (Worker)users.consultar(workerId);
		}
		return null;
	}

	@Override
	public Role getRole(String roleId) {
        int i = 0;
        Role findedRole =null;
        
        while(i< roles.size() && findedRole == null) {
        	if(roles.get(i).getId() ==roleId) {
        		findedRole = roles.get(i);
        	}
        	i++;
        }
        return findedRole;
	}

	@Override
	public Group getGroup(String groupId){
        return ArbolSearch(groups, groupId);
	}
	
	public <E extends IdComparable> E ArbolSearch(ArbolAVL<E> arbol, String id){
		Posicion<E> posicion = arbol.raiz();
		E findedObject = null;
		while( findedObject == null && posicion != null && !arbol.esHoja(posicion)) {
			int result = posicion.getElem().compareTo(id);
			if(result > 0) {
				posicion = arbol.hijoIzquierdo(posicion);
			}else if(result < 0) {
				posicion = arbol.hijoDerecho(posicion);
			}else {
				findedObject = (E) posicion.getElem();
			}
		}
		if(findedObject == null && posicion != null && posicion.getElem().compareTo(id) == 0) {
			findedObject = (E) posicion.getElem();
		}
        return findedObject;
	}

	@Override
	public int numWorkers() {
		return numWorkers;
	}

	@Override
	public int numWorkers(String organizationId) {
		return organizations.consultar(organizationId).numWorkers();
	}

	@Override
	public int numRoles() {
		return roles.size();
	}

	@Override
	public int numWorkersByRole(String roleId) {
		return getRole(roleId).getNumUsers();
	}

	@Override
	public int numGroups() {
		return groups.numElems();
	}

	@Override
	public int numOrders() {
		return orders.numElems();
	}

	@Override
	public Iterador<Activity> best10Activities() throws ActivityNotFoundException {
		if(bestActivities.numElems() == 0) {
			throw new ActivityNotFoundException();
		}
		return bestActivities.elementos();
	}

}
