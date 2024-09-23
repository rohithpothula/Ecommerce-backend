package com.flipkart.ecommerce_backend.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="verification_token")
public class VerificationToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id",nullable=false)
	private Long id;
	
	@Column(name="token",nullable=false,unique=true,length = 512)
	private String token;
	
	@Column(name="created_timestamp",nullable=false)
	private Timestamp createdTimestamp;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id",nullable=false)
	private LocalUser localUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Timestamp createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public LocalUser getLocalUser() {
		return localUser;
	}

	public void setLocalUser(LocalUser localUser) {
		this.localUser = localUser;
	}
	
}
