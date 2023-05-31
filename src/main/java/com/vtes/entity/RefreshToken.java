package com.vtes.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity()
@Table(name = "tbl_refresh_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@OneToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;

	@Column(name = "TOKEN", nullable = false, unique = true)
	private String token;

	@Column(name = "EXPIRY_DT", nullable = false)
	private Instant expiryDate;

	@Column(name = "`CREATE_DT`")
	private Instant createDt = Instant.now();

	@Column(name = "`UPDATE_DT`")
	private Instant updateDt;

	@Column(name = "DELETE_FLAG", nullable = false)
	private Boolean deleteFlag;

	@PreUpdate
	public void preUpdate() {
		this.updateDt = Instant.now();
	}

}
