package com.flipkart.ecommerce_backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;


@Entity
@Table(name="local_user")
public class LocalUser {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",nullable=false)
	private long id;
	
	@Column(name="user_name",nullable=false,unique=true)
	private String username;
	
	@JsonIgnore
	@Column(name="password",nullable=false,length=1000)
	private String password;
	
	@Column(name="email",nullable=false,unique=true)
	private String email;
	
	@Column(name="first_name",nullable=false)
	private String first_name;
	
	@Column(name="last_name",nullable=false)
	private String last_name;
	
	@JsonIgnore
	@OneToMany(mappedBy="id",cascade=CascadeType.REMOVE,orphanRemoval = true)
	private List<Address> address = new ArrayList<>();
	

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}
	
	
}
