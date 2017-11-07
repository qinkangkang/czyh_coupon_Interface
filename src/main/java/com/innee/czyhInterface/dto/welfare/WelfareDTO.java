package com.innee.czyhInterface.dto.welfare;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.innee.czyhInterface.util.NullToEmptySerializer;

public class WelfareDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int level = 0;// 会员等级

	private int growthValue = 0;// 兑换所需成长值

	private boolean levelType;// 是否解锁1.不解 2.解锁

	private List<WelfareGoodsDTO> welfareGoodsDTOList;// 商品列表list

	public int getGrowthValue() {
		return growthValue;
	}

	public void setGrowthValue(int growthValue) {
		this.growthValue = growthValue;
	}

	public List<WelfareGoodsDTO> getWelfareGoodsDTOList() {
		return welfareGoodsDTOList;
	}

	public void setWelfareGoodsDTOList(List<WelfareGoodsDTO> welfareGoodsDTOList) {
		this.welfareGoodsDTOList = welfareGoodsDTOList;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isLevelType() {
		return levelType;
	}

	public void setLevelType(boolean levelType) {
		this.levelType = levelType;
	}

}