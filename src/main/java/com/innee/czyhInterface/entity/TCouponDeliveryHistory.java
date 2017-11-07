package com.innee.czyhInterface.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_coupon_delivery_history")
public class TCouponDeliveryHistory extends UuidEntity {

	private static final long serialVersionUID = 1L;

	// Fields
	private TCouponInformation TCouponInformation;
	private String fcustomerId;
	private Date fdeliverTime;
	private TDelivery TDelivery;
	private Date fuseStartTime;
	private Date fuseEndTime;
	private String forderId;
	private Date fuseTime;
	private Integer fstatus;
	private String ffromOrderId;

	// Constructors

	/** default constructor */
	public TCouponDeliveryHistory() {
	}

	/** minimal constructor */
	public TCouponDeliveryHistory(String id) {
		this.id = id;
	}

	// Property accessors
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fCouponID")
	public TCouponInformation getTCouponInformation() {
		return TCouponInformation;
	}

	public void setTCouponInformation(TCouponInformation tCouponInformation) {
		TCouponInformation = tCouponInformation;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fDeliveryId")
	public TDelivery getTDelivery() {
		return TDelivery;
	}

	public void setTDelivery(TDelivery tDelivery) {
		TDelivery = tDelivery;
	}

	@JoinColumn(name = "fCustomerID")
	public String getFcustomerId() {
		return fcustomerId;
	}
	
	public void setFcustomerId(String fcustomerId) {
		this.fcustomerId = fcustomerId;
	}

	@Column(name = "fDeliverTime", length = 19)
	public Date getFdeliverTime() {
		return this.fdeliverTime;
	}

	public void setFdeliverTime(Date fdeliverTime) {
		this.fdeliverTime = fdeliverTime;
	}

	@Column(name = "fUseStartTime")
	public Date getFuseStartTime() {
		return fuseStartTime;
	}

	public void setFuseStartTime(Date fuseStartTime) {
		this.fuseStartTime = fuseStartTime;
	}

	@Column(name = "fUseEndTime")
	public Date getFuseEndTime() {
		return fuseEndTime;
	}

	public void setFuseEndTime(Date fuseEndTime) {
		this.fuseEndTime = fuseEndTime;
	}
	
	@JoinColumn(name = "fOrderID")
	public String getForderId() {
		return forderId;
	}
	
	public void setForderId(String forderId) {
		this.forderId = forderId;
	}
	
	public Date getFuseTime() {
		return fuseTime;
	}


	public void setFuseTime(Date fuseTime) {
		this.fuseTime = fuseTime;
	}

	public Integer getFstatus() {
		return fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}
	
	@Column(name = "fFromOrder")
	public String getFfromOrderId() {
		return ffromOrderId;
	}

	public void setFfromOrderId(String ffromOrderId) {
		this.ffromOrderId = ffromOrderId;
	}

}