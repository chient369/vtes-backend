
package com.vtes.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_commuter_pass")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommuterPass {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name = "USER_ID",referencedColumnName = "ID")
	private User user;
	
	@Column(name = "DEPARTURE")
	private String departure;
	
	@Column(name = "DESTINATION")
	private String destination;
	
	@Column(name = "VIA_DETAIL")
	private String viaDetail;
	
	@Column(name = "DELETE_FLAG")
	@ColumnDefault("false")
	private Boolean deleteFlag;

	public CommuterPass(String departure, String destination, String viaDetail) {
		super();
		this.departure = departure;
		this.destination = destination;
		this.viaDetail = viaDetail;
	}
	
	

}
