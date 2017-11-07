package com.innee.czyhInterface.dto.invitation;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class InvitationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String name;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String imageUrl;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String firstOrderTime;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String couponDes;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String arrival;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getFirstOrderTime() {
		return firstOrderTime;
	}

	public void setFirstOrderTime(String firstOrderTime) {
		this.firstOrderTime = firstOrderTime;
	}

	public String getCouponDes() {
		return couponDes;
	}

	public void setCouponDes(String couponDes) {
		this.couponDes = couponDes;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

}