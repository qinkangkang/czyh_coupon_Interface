package com.innee.czyhInterface.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * TCustomer entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "t_customer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TCustomer extends UuidEntity {

	private static final long serialVersionUID = 1L;

	private String fusername;
	private String fname;
	private String fphoto;
	private String fweixinId;
	private String fweixinUnionId;
	private String fweixinName;
	private String fregion;
	private Integer fsex;
	private String fphone;
	private String fpassword;
	private String freferee;
	private Integer ftype;
	private String fsalt;
	private String fbaby;
	private Integer fstatus;
	private String fticket;
	private Timestamp fcreateTime;
	private Timestamp fupdateTime;

	// Constructors

	/** default constructor */
	public TCustomer() {
	}

	/** full constructor */
	public TCustomer(String fusername, String fname, String fphoto,
			String fweixinId, String fweixinUnionId, String fweixinName,
			String fregion, Integer fsex, String fphone, String fpassword,
			String freferee, Integer ftype, String fsalt, String fbaby,
			Integer fstatus, String fticket, Timestamp fcreateTime,
			Timestamp fupdateTime) {
		this.fusername = fusername;
		this.fname = fname;
		this.fphoto = fphoto;
		this.fweixinId = fweixinId;
		this.fweixinUnionId = fweixinUnionId;
		this.fweixinName = fweixinName;
		this.fregion = fregion;
		this.fsex = fsex;
		this.fphone = fphone;
		this.fpassword = fpassword;
		this.freferee = freferee;
		this.ftype = ftype;
		this.fsalt = fsalt;
		this.fbaby = fbaby;
		this.fstatus = fstatus;
		this.fticket = fticket;
		this.fcreateTime = fcreateTime;
		this.fupdateTime = fupdateTime;
	}

	@Column(name = "fUsername")
	public String getFusername() {
		return this.fusername;
	}

	public void setFusername(String fusername) {
		this.fusername = fusername;
	}

	@Column(name = "fName")
	public String getFname() {
		return this.fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	@Column(name = "fPhoto", length = 2048)
	public String getFphoto() {
		return this.fphoto;
	}

	public void setFphoto(String fphoto) {
		this.fphoto = fphoto;
	}

	@Column(name = "fWeixinID")
	public String getFweixinId() {
		return this.fweixinId;
	}

	public void setFweixinId(String fweixinId) {
		this.fweixinId = fweixinId;
	}

	@Column(name = "fWeixinUnionID")
	public String getFweixinUnionId() {
		return this.fweixinUnionId;
	}

	public void setFweixinUnionId(String fweixinUnionId) {
		this.fweixinUnionId = fweixinUnionId;
	}

	@Column(name = "fWeixinName")
	public String getFweixinName() {
		return this.fweixinName;
	}

	public void setFweixinName(String fweixinName) {
		this.fweixinName = fweixinName;
	}

	@Column(name = "fRegion")
	public String getFregion() {
		return this.fregion;
	}

	public void setFregion(String fregion) {
		this.fregion = fregion;
	}

	@Column(name = "fSex")
	public Integer getFsex() {
		return this.fsex;
	}

	public void setFsex(Integer fsex) {
		this.fsex = fsex;
	}

	@Column(name = "fPhone")
	public String getFphone() {
		return this.fphone;
	}

	public void setFphone(String fphone) {
		this.fphone = fphone;
	}

	@Column(name = "fPassword")
	public String getFpassword() {
		return this.fpassword;
	}

	public void setFpassword(String fpassword) {
		this.fpassword = fpassword;
	}

	@Column(name = "fReferee", length = 36)
	public String getFreferee() {
		return this.freferee;
	}

	public void setFreferee(String freferee) {
		this.freferee = freferee;
	}

	@Column(name = "fType")
	public Integer getFtype() {
		return this.ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

	@Column(name = "fSalt", length = 32)
	public String getFsalt() {
		return this.fsalt;
	}

	public void setFsalt(String fsalt) {
		this.fsalt = fsalt;
	}

	@Column(name = "fBaby", length = 2048)
	public String getFbaby() {
		return this.fbaby;
	}

	public void setFbaby(String fbaby) {
		this.fbaby = fbaby;
	}

	@Column(name = "fStatus")
	public Integer getFstatus() {
		return this.fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	@Column(name = "fTicket", length = 64)
	public String getFticket() {
		return this.fticket;
	}

	public void setFticket(String fticket) {
		this.fticket = fticket;
	}

	@Column(name = "fCreateTime", length = 19)
	public Timestamp getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Timestamp fcreateTime) {
		this.fcreateTime = fcreateTime;
	}

	@Column(name = "fUpdateTime", length = 19)
	public Timestamp getFupdateTime() {
		return this.fupdateTime;
	}

	public void setFupdateTime(Timestamp fupdateTime) {
		this.fupdateTime = fupdateTime;
	}

}