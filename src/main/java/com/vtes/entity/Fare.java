
package com.vtes.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_fare")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fare {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
//	@JsonBackReference
	private User user;

	@Column(name = "VISIT_LOCATION")
	private String visitLocation;

	@Column(name = "DEPARTURE")
	private String departure;

	@Column(name = "DESTINATION")
	private String destination;

	@Column(name = "PAY_METHOD")
	private String payMethod;

	@Column(name = "USE_CP_FLAG")
	private Boolean useCommuterPass;

	@Column(name = "IS_ROUND_TRIP")
	private Boolean isRoundTrip;

	@Column(name = "FEE")
	private Integer fee;

	@Column(name = "TRANSPORTATION")
	private String transportation;

	@Column(name = "VISIT_DT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date visitDate;

	@Column(name = "CREATE_DT")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date createDate;

	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag;

	public Fare(Integer id) {
		super();
		this.id = id;
	}

}
