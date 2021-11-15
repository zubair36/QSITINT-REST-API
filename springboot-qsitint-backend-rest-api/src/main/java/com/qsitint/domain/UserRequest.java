package com.qsitint.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "tbl_user_request")
@SQLDelete(sql = "update tbl_user_request set deleted=true where id=?")
@Where(clause = "deleted = false")
public class UserRequest {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = true)
	private String description;

	@Column(nullable = true)
	private String requestType;

	@Column(nullable = true)
	private String lng;
	
	@Column(nullable = true)
	private String lat;
	
	@Column(nullable = true)
	private String status;
	
	@Column(nullable = true)
	private Boolean deleted = false;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}


}
