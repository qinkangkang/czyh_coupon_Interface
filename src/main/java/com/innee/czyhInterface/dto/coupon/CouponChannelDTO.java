package com.innee.czyhInterface.dto.coupon;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class CouponChannelDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String couponDeliveryId;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String subTitle;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String title;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String couponId;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String useStartTime;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String useEndTime;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String useRange;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String amount;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String discount;
	
	private Integer percentage = 0;//百分比

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String limitation;

	private Integer status;
	
	private Integer type = 1;

	/** 默认优惠券没领取光 */
	private boolean receiveFinish = false;

	/** 默认用户没领取 */
	private boolean receive = false;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String limitationInfo;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String limitationClient;
	// private int useType = 0;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String useUrl;
	
	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String fobjectTitle;

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getUseStartTime() {
		return useStartTime;
	}

	public void setUseStartTime(String useStartTime) {
		this.useStartTime = useStartTime;
	}

	public String getUseEndTime() {
		return useEndTime;
	}

	public void setUseEndTime(String useEndTime) {
		this.useEndTime = useEndTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getLimitation() {
		return limitation;
	}

	public void setLimitation(String limitation) {
		this.limitation = limitation;
	}

	public String getLimitationInfo() {
		return limitationInfo;
	}

	public void setLimitationInfo(String limitationInfo) {
		this.limitationInfo = limitationInfo;
	}

	public String getLimitationClient() {
		return limitationClient;
	}

	public void setLimitationClient(String limitationClient) {
		this.limitationClient = limitationClient;
	}

	public Integer getStatus() {
		return status;
	}

	

	public boolean isReceiveFinish() {
		return receiveFinish;
	}

	public void setReceiveFinish(boolean receiveFinish) {
		this.receiveFinish = receiveFinish;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUseRange() {
		return useRange;
	}

	public void setUseRange(String useRange) {
		this.useRange = useRange;
	}

	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public String getUseUrl() {
		return useUrl;
	}

	public void setUseUrl(String useUrl) {
		this.useUrl = useUrl;
	}

	public String getCouponDeliveryId() {
		return couponDeliveryId;
	}

	public void setCouponDeliveryId(String couponDeliveryId) {
		this.couponDeliveryId = couponDeliveryId;
	}

	public Integer getPercentage() {
		return percentage;
	}

	public void setPercentage(Integer percentage) {
		this.percentage = percentage;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getFobjectTitle() {
		return fobjectTitle;
	}

	public void setFobjectTitle(String fobjectTitle) {
		this.fobjectTitle = fobjectTitle;
	}

}
