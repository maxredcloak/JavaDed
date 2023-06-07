package uoc.ded.practica.model;

import java.time.LocalDate;

public class Worker extends User{
	String roleId;
	String organizationId;
	public Worker(String idUser, String name, String surname, LocalDate birthday, boolean covidCertificate,String roleId, String organizationId) {
		super(idUser, name, surname, birthday, covidCertificate);
		this.roleId = roleId;
		this.organizationId = organizationId;
	}
	public String getRoleId() {
		return roleId;
	}	
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getOrganizationId() {
		return organizationId;
	}	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}
