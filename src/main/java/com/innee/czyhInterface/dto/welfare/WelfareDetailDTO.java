package com.innee.czyhInterface.dto.welfare;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class WelfareDetailDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String goodsId;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String[] imageUrls;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String goodsTitle;

	private int goodsbouns = 0;
	
	private int goodsbounsLevel = 0;// 商品所属积分等级

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String spec;

	private boolean covert = false;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String detailHtmlUrl;

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String[] getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String[] imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public int getGoodsbouns() {
		return goodsbouns;
	}

	public void setGoodsbouns(int goodsbouns) {
		this.goodsbouns = goodsbouns;
	}

	public int getGoodsbounsLevel() {
		return goodsbounsLevel;
	}

	public void setGoodsbounsLevel(int goodsbounsLevel) {
		this.goodsbounsLevel = goodsbounsLevel;
	}

	public String getDetailHtmlUrl() {
		return detailHtmlUrl;
	}

	public void setDetailHtmlUrl(String detailHtmlUrl) {
		this.detailHtmlUrl = detailHtmlUrl;
	}

	public boolean isCovert() {
		return covert;
	}

	public void setCovert(boolean covert) {
		this.covert = covert;
	}

	

}