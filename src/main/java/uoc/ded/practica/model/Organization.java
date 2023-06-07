package uoc.ded.practica.model;

import java.util.Comparator;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import uoc.ei.tads.Posicion;
import uoc.ei.tads.Recorrido;

public class Organization {
    public static final Comparator<Organization> CMP_V = (Organization o1, Organization o2)->Double.compare(o1.rating(), o2.rating());
    
    private String organizationId;
    private String description;
    private  String name;

    private Lista<Record> records;
    private Lista<Activity> activities;
    private Lista<Worker> workers;
    
    public Organization(String organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        activities = new ListaEncadenada<Activity>();
        records = new ListaEncadenada<Record>();
        workers = new ListaEncadenada<Worker>();

    }
    public String getName() {
        return name;
    }
    public String getOrganizationId() {
        return organizationId;
    }
    public String getDescription() {
        return description;
    }
    public Iterador<Record> getRecords() {
    	return records.elementos();
    }
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Iterador<Activity> activities() {
        return activities.elementos();
    }
    public Iterador<Worker> workers() {
        return workers.elementos();
    }
    public void addActivity(Activity activity) {
        activities.insertarAlFinal(activity);
    }
    public void addRecord(Record record) {
    	records.insertarAlFinal(record);
    }
    public void addWorker(Worker worker) {
    	workers.insertarAlFinal(worker);
    }
    public void removeWorker(String userId) {
		boolean deleted = false;
		Recorrido<Worker> rec = workers.posiciones();
		while(rec.haySiguiente() && !deleted){
			Posicion<Worker> next = rec.siguiente();
			if(next.getElem().getId() == userId) {
				workers.borrar(next);
				deleted = true;
			}
		}
    }
    public int numActivities() {
        return activities.numElems();
    }
    public int numRecords() {
        return records.numElems();
    }
    public int numWorkers() {
    	return workers.numElems();
    }
    public boolean hasActivities() {
        return activities.numElems() > 0;
    }
    public double rating() {
    	return 0.0;
    }

}
