package com.innee.czyhInterface.dto.welfare;

import java.io.Serializable;

public class WelfareCustomerDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private int level = 0;

	private String imageUrl;

	private int bouns = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getBouns() {
		return bouns;
	}

	public void setBouns(int bouns) {
		this.bouns = bouns;
	}

}