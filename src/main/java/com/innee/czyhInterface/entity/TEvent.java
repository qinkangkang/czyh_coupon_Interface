package com.innee.czyhInterface.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "t_goods")
// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TEvent extends UuidEntity {

	private static final long serialVersionUID = 1L;

	// Fields
	private String fsponsor;
	private String ftitle;
	private String fsubTitle;
	private Integer fsubTitleImg;
	private Integer fcity;
	private Integer ftypeA;
	private Integer ftypeB;
	//private Integer fappointment;
	private Integer freturn;
	private Integer fusePreferential;
	/*private String fage;
	private Integer fageA;
	private Integer fageB;
	private Integer fsiteType;
	private Integer fduration;
*/	private String ffocus;
	private String fbrief;
	private String fdetail;
	private String fdetailHtmlUrl;
	//private String fdeal;
	private BigDecimal forderPrice;
	private Integer fverificationType;
	private Integer fsettlementType;
	private String fimage1;
	private String fimage2;
	private Long fbdId;
	private Long fcreaterId;
	private Date fcreateTime;
	private Date fupdateTime;
	private String ftag;
	private Integer forderType;
	private Integer fstatus;
	private Integer feditLock;
	private Long feditLockId;
	private Date flockTime;
	/*private String feventTime;
	private Integer fstockFlag;
	private Integer fsaleFlag;*/
	private Long frecommend;
	private BigDecimal fscore;
	private Integer fbaseScore;
	private Integer fsalesType;
	private Date fonSaleTime;
	private Date foffSaleTime;
	private Integer fsellModel;
	private String fspec;
	private Integer fspecModel;
	private Integer fpromotionModel;
	private Integer flimitation;
	private Integer ftotal;
	private Integer fstock;
	private Integer fsaleTotal;
	private BigDecimal fpriceMoney;
	private BigDecimal fprice;
	private Integer fgoodsTag;
	private Date fsecondKill;
	private Integer fsdealsModel;

	// Constructors

	/** default constructor */
	public TEvent() {
	}

	/** minimal constructor */
	public TEvent(String id) {
		this.id = id;
	}

	// Property accessors
	@Column(name = "fTitle")
	public String getFtitle() {
		return this.ftitle;
	}

	public void setFtitle(String ftitle) {
		this.ftitle = ftitle;
	}

	@Column(name = "fSubTitle")
	public String getFsubTitle() {
		return this.fsubTitle;
	}

	public void setFsubTitle(String fsubTitle) {
		this.fsubTitle = fsubTitle;
	}

	@Column(name = "fSubTitleImg")
	public Integer getFsubTitleImg() {
		return this.fsubTitleImg;
	}

	public void setFsubTitleImg(Integer fsubTitleImg) {
		this.fsubTitleImg = fsubTitleImg;
	}

	@Column(name = "fCity")
	public Integer getFcity() {
		return this.fcity;
	}

	public void setFcity(Integer fcity) {
		this.fcity = fcity;
	}

	@JoinColumn(name = "fSponsor")
	public String getFsponsor() {
		return fsponsor;
	}

	public void setFsponsor(String fsponsor) {
		this.fsponsor = fsponsor;
	}

	@Column(name = "fTypeA")
	public Integer getFtypeA() {
		return this.ftypeA;
	}

	public void setFtypeA(Integer ftypeA) {
		this.ftypeA = ftypeA;
	}

	@Column(name = "fTypeB")
	public Integer getFtypeB() {
		return this.ftypeB;
	}

	public void setFtypeB(Integer ftypeB) {
		this.ftypeB = ftypeB;
	}

/*	@Column(name = "fAppointment")
	public Integer getFappointment() {
		return this.fappointment;
	}

	public void setFappointment(Integer fappointment) {
		this.fappointment = fappointment;
	}*/

	@Column(name = "fReturn")
	public Integer getFreturn() {
		return this.freturn;
	}

	public void setFreturn(Integer freturn) {
		this.freturn = freturn;
	}

	@Column(name = "fUsePreferential")
	public Integer getFusePreferential() {
		return this.fusePreferential;
	}

	public void setFusePreferential(Integer fusePreferential) {
		this.fusePreferential = fusePreferential;
	}

	/*@Column(name = "fAge")
	public String getFage() {
		return this.fage;
	}

	public void setFage(String fage) {
		this.fage = fage;
	}

	@Column(name = "fAgeA")
	public Integer getFageA() {
		return this.fageA;
	}

	public void setFageA(Integer fageA) {
		this.fageA = fageA;
	}

	@Column(name = "fAgeB")
	public Integer getFageB() {
		return this.fageB;
	}

	public void setFageB(Integer fageB) {
		this.fageB = fageB;
	}

	@Column(name = "fSiteType")
	public Integer getFsiteType() {
		return this.fsiteType;
	}

	public void setFsiteType(Integer fsiteType) {
		this.fsiteType = fsiteType;
	}

	@Column(name = "fDuration")
	public Integer getFduration() {
		return this.fduration;
	}

	public void setFduration(Integer fduration) {
		this.fduration = fduration;
	}*/

	@Column(name = "fFocus")
	public String getFfocus() {
		return this.ffocus;
	}

	public void setFfocus(String ffocus) {
		this.ffocus = ffocus;
	}

	@Column(name = "fBrief")
	public String getFbrief() {
		return this.fbrief;
	}

	public void setFbrief(String fbrief) {
		this.fbrief = fbrief;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Lazy
	@Column(name = "fDetail", columnDefinition = "longtext")
	public String getFdetail() {
		return this.fdetail;
	}

	public void setFdetail(String fdetail) {
		this.fdetail = fdetail;
	}

	@Column(name = "fDetailHtmlUrl", length = 2048)
	public String getFdetailHtmlUrl() {
		return this.fdetailHtmlUrl;
	}

	public void setFdetailHtmlUrl(String fdetailHtmlUrl) {
		this.fdetailHtmlUrl = fdetailHtmlUrl;
	}

	@Column(name = "fPrice")
	public BigDecimal getFprice() {
		return fprice;
	}

	public void setFprice(BigDecimal fprice) {
		this.fprice = fprice;
	}

	/*@Column(name = "fDeal", length = 32)
	public String getFdeal() {
		return this.fdeal;
	}

	public void setFdeal(String fdeal) {
		this.fdeal = fdeal;
	}
*/
	@Column(name = "fOrderPrice", precision = 18)
	public BigDecimal getForderPrice() {
		return this.forderPrice;
	}

	public void setForderPrice(BigDecimal forderPrice) {
		this.forderPrice = forderPrice;
	}

	@Column(name = "fImage1", length = 255)
	public String getFimage1() {
		return this.fimage1;
	}

	public void setFimage1(String fimage1) {
		this.fimage1 = fimage1;
	}

	@Column(name = "fImage2", length = 255)
	public String getFimage2() {
		return this.fimage2;
	}

	public void setFimage2(String fimage2) {
		this.fimage2 = fimage2;
	}

	@Column(name = "fBdId")
	public Long getFbdId() {
		return this.fbdId;
	}

	public void setFbdId(Long fbdId) {
		this.fbdId = fbdId;
	}

	@Column(name = "fCreaterId")
	public Long getFcreaterId() {
		return this.fcreaterId;
	}

	public void setFcreaterId(Long fcreaterId) {
		this.fcreaterId = fcreaterId;
	}

	@Column(name = "fCreateTime", length = 19)
	public Date getFcreateTime() {
		return this.fcreateTime;
	}

	public void setFcreateTime(Date fcreateTime) {
		this.fcreateTime = fcreateTime;
	}

	@Column(name = "fUpdateTime", length = 19)
	public Date getFupdateTime() {
		return this.fupdateTime;
	}

	public void setFupdateTime(Date fupdateTime) {
		this.fupdateTime = fupdateTime;
	}

	@Column(name = "fTag", length = 2048)
	public String getFtag() {
		return this.ftag;
	}

	public void setFtag(String ftag) {
		this.ftag = ftag;
	}

	@Column(name = "fOrderType")
	public Integer getForderType() {
		return this.forderType;
	}

	public void setForderType(Integer forderType) {
		this.forderType = forderType;
	}

	@Column(name = "fStatus")
	public Integer getFstatus() {
		return this.fstatus;
	}

	public void setFstatus(Integer fstatus) {
		this.fstatus = fstatus;
	}

	@Column(name = "fEditLock")
	public Integer getFeditLock() {
		return this.feditLock;
	}

	public void setFeditLock(Integer feditLock) {
		this.feditLock = feditLock;
	}

	@Column(name = "fEditLockId")
	public Long getFeditLockId() {
		return this.feditLockId;
	}

	public void setFeditLockId(Long feditLockId) {
		this.feditLockId = feditLockId;
	}

	@Column(name = "fLockTime", length = 19)
	public Date getFlockTime() {
		return this.flockTime;
	}

	public void setFlockTime(Date flockTime) {
		this.flockTime = flockTime;
	}

	/*@Column(name = "fEventTime")
	public String getFeventTime() {
		return this.feventTime;
	}

	public void setFeventTime(String feventTime) {
		this.feventTime = feventTime;
	}

	@Column(name = "fStockFlag")
	public Integer getFstockFlag() {
		return this.fstockFlag;
	}

	public void setFstockFlag(Integer fstockFlag) {
		this.fstockFlag = fstockFlag;
	}

	@Column(name = "fSaleFlag")
	public Integer getFsaleFlag() {
		return this.fsaleFlag;
	}

	public void setFsaleFlag(Integer fsaleFlag) {
		this.fsaleFlag = fsaleFlag;
	}*/

	@Column(name = "fRecommend")
	public Long getFrecommend() {
		return this.frecommend;
	}

	public void setFrecommend(Long frecommend) {
		this.frecommend = frecommend;
	}

	@Column(name = "fScore", precision = 8)
	public BigDecimal getFscore() {
		return this.fscore;
	}

	public void setFscore(BigDecimal fscore) {
		this.fscore = fscore;
	}

	@Column(name = "fVerificationType")
	public Integer getFverificationType() {
		return this.fverificationType;
	}

	public void setFverificationType(Integer fverificationType) {
		this.fverificationType = fverificationType;
	}

	@Column(name = "fSettlementType")
	public Integer getFsettlementType() {
		return this.fsettlementType;
	}

	public void setFsettlementType(Integer fsettlementType) {
		this.fsettlementType = fsettlementType;
	}

	@Column(name = "fBaseScore")
	public Integer getFbaseScore() {
		return fbaseScore;
	}

	public void setFbaseScore(Integer fbaseScore) {
		this.fbaseScore = fbaseScore;
	}

	@Column(name = "fSalesType")
	public Integer getFsalesType() {
		return this.fsalesType;
	}

	public void setFsalesType(Integer fsalesType) {
		this.fsalesType = fsalesType;
	}

	@Column(name = "fOnSaleTime", length = 19)
	public Date getFonSaleTime() {
		return this.fonSaleTime;
	}

	public void setFonSaleTime(Date fonSaleTime) {
		this.fonSaleTime = fonSaleTime;
	}

	@Column(name = "fOffSaleTime", length = 19)
	public Date getFoffSaleTime() {
		return this.foffSaleTime;
	}

	public void setFoffSaleTime(Date foffSaleTime) {
		this.foffSaleTime = foffSaleTime;
	}

	@Column(name = "fSellModel")
	public Integer getFsellModel() {
		return fsellModel;
	}

	public void setFsellModel(Integer fsellModel) {
		this.fsellModel = fsellModel;
	}

	@Column(name = "fSpecModel")
	public Integer getFspecModel() {
		return fspecModel;
	}

	public void setFspecModel(Integer fspecModel) {
		this.fspecModel = fspecModel;
	}

	@Column(name = "fPromotionModel")
	public Integer getFpromotionModel() {
		return fpromotionModel;
	}

	public void setFpromotionModel(Integer fpromotionModel) {
		this.fpromotionModel = fpromotionModel;
	}

	@Column(name = "fLimitation")
	public Integer getFlimitation() {
		return flimitation;
	}

	public void setFlimitation(Integer flimitation) {
		this.flimitation = flimitation;
	}

	@Column(name = "fTotal")
	public Integer getFtotal() {
		return ftotal;
	}

	public void setFtotal(Integer ftotal) {
		this.ftotal = ftotal;
	}

	@Column(name = "fStock")
	public Integer getFstock() {
		return fstock;
	}

	public void setFstock(Integer fstock) {
		this.fstock = fstock;
	}

	@Column(name = "fSaleTotal")
	public Integer getFsaleTotal() {
		return fsaleTotal;
	}

	public void setFsaleTotal(Integer fsaleTotal) {
		this.fsaleTotal = fsaleTotal;
	}

	@Column(name = "fSpec")
	public String getFspec() {
		return fspec;
	}

	public void setFspec(String fspec) {
		this.fspec = fspec;
	}

	@Column(name = "fPriceMoney")
	public BigDecimal getFpriceMoney() {
		return fpriceMoney;
	}

	public void setFpriceMoney(BigDecimal fpriceMoney) {
		this.fpriceMoney = fpriceMoney;
	}

	@Column(name = "fGoodsTag")
	public Integer getFgoodsTag() {
		return fgoodsTag;
	}

	public void setFgoodsTag(Integer fgoodsTag) {
		this.fgoodsTag = fgoodsTag;
	}

	@Column(name = "fSecondKill")
	public Date getFsecondKill() {
		return fsecondKill;
	}

	public void setFsecondKill(Date fsecondKill) {
		this.fsecondKill = fsecondKill;
	}

	@Column(name = "fSdealsModel")
	public Integer getFsdealsModel() {
		return fsdealsModel;
	}

	public void setFsdealsModel(Integer fsdealsModel) {
		this.fsdealsModel = fsdealsModel;
	}

}