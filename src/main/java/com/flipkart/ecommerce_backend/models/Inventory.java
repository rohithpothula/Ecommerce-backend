package com.flipkart.ecommerce_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="inventory")
public class Inventory {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id",unique=true)
	private long id;
	
	@JsonIgnore
	@OneToOne(optional=false,orphanRemoval = true)
	@JoinColumn(name="product_id",nullable=false,unique=true)
	private Product product;
	
	@Column(name="quantity",nullable=false)
	private Integer quantity;

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
	}
	

}
