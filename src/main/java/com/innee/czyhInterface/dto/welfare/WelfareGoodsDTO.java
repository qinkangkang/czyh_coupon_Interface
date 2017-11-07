package com.innee.czyhInterface.dto.welfare;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class WelfareGoodsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String goodsId;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String goodsTitle;

	@JsonSerialize(nullsUsing = NullToEmptySerializer.class)
	private String goodsImage;

	private int goodsbouns = 0;

	private int goodsbounsLevel = 0;// 商品所属积分等级

	private int welfareGoodstype = 0;// 1.可以兑换2.积分不足3.已兑换过不可在兑换

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsTitle() {
		return goodsTitle;
	}

	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}

	public String getGoodsImage() {
		return goodsImage;
	}

	public void setGoodsImage(String goodsImage) {
		this.goodsImage = goodsImage;
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

	public int getWelfareGoodstype() {
		return welfareGoodstype;
	}

	public void setWelfareGoodstype(int welfareGoodstype) {
		this.welfareGoodstype = welfareGoodstype;
	}

}