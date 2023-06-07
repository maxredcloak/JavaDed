package uoc.ded.practica.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;

import uoc.ded.practica.SafetyActivities4Covid19;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class User implements Comparable<User>{
    public static final Comparator<String> CMP = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    private String id;
    private String name;
    private String surname;
    private Lista<Activity> activities;
    private LocalDate birthday; // No necesitamos la hora de nacimiento, LocalDate no lleva hora
    private boolean covidCertificate;
    private int valorations;

	public User(String idUser, String name, String surname, LocalDate birthday, boolean covidCertificate) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.setCovidCertificate(covidCertificate);
        this.activities = new ListaEncadenada<Activity>();
        this.valorations = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isCovidCertificate() {
		return covidCertificate;
	}

    public int compareTo(User o) {
        return getId().compareTo(o.getId());
    }

    public Iterador<Activity> answers() {
        return activities.elementos();
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setCovidCertificate(boolean covidCertificate) {
        this.covidCertificate = covidCertificate;
    }

    public boolean is(String userId) {
        return id.equals(userId);
    }

    public Iterador<Activity> activities() {
        return activities.elementos();
    }

    public void addActivity(Activity activity) {
        activities.insertarAlFinal(activity);
    }

    public int numActivities() {
        return activities.numElems();
    }

    public boolean isInActivity(String actId) {
        Iterador<Activity> it = activities.elementos();

        boolean found = false;
        Activity act = null;

        while (!found && it.haySiguiente()) {
        	act = it.siguiente();
            found = act.is(actId);
        }

        return found;
    }

    public boolean hasActivities() {
        return activities.numElems() > 0;
    }
    public SafetyActivities4Covid19.Badge getBadge(LocalDate date){
    	int years = Period.between(birthday, date).getYears();
    	if(years > 65 && covidCertificate) {
    		return SafetyActivities4Covid19.Badge.SENIOR_PLUS;
    	}
    	else if(years < 65 && years >=50 && covidCertificate) {
    		return SafetyActivities4Covid19.Badge.SENIOR;
    	}
    	else if(years < 50 && years >=30 && covidCertificate && valorations >= 5) {
    		return SafetyActivities4Covid19.Badge.MASTER_PLUS;
    	}
    	else if(years < 30 && years >=18 && covidCertificate && valorations >= 10) {
    		return SafetyActivities4Covid19.Badge.YOUTH_PLUS;
    	}
    	else if(years < 50 && years >=30 && covidCertificate ) { //no es necesario hacer valorations < 5 por el else
    		return SafetyActivities4Covid19.Badge.MASTER;
    	}
    	else if(years < 30 && years >=18 && covidCertificate ) { //no es necesario hacer valorations < 5 por el else
    		return SafetyActivities4Covid19.Badge.YOUTH;
    	}
    	else if(years < 18 && years >=12 && covidCertificate ) {
    		return SafetyActivities4Covid19.Badge.JUNIOR_PLUS;
    	}
    	else if(years < 12 ) {
    		return SafetyActivities4Covid19.Badge.JUNIOR;
    	}
		return SafetyActivities4Covid19.Badge.DARK;
    }
}
