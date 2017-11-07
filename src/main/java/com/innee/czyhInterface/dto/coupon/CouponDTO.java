package com.innee.czyhInterface.dto.coupon;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class CouponDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String couponDeliveryId;

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
	
	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String discountAmount;
	
	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String freight;
	
	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String orderTotal;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String limitation;

	private Integer status;
	
	private Integer type = 1;
	
	private boolean checked = false;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String limitationInfo;

	public String getCouponDeliveryId() {
		return couponDeliveryId;
	}

	public void setCouponDeliveryId(String couponDeliveryId) {
		this.couponDeliveryId = couponDeliveryId;
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

	public Integer getStatus() {
		return status;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(String discountAmount) {
		this.discountAmount = discountAmount;
	}

	public String getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}

}
