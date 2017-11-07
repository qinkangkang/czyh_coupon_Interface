package com.innee.czyhInterface.util.coupon;

import java.math.BigDecimal;
import java.util.Comparator;

import com.innee.czyhInterface.dto.coupon.CouponDTO;

public class CouponComparator implements Comparator<CouponDTO> {

	@Override
	public int compare(CouponDTO couponA, CouponDTO couponB) {
		return new BigDecimal(couponB.getDiscountAmount()).compareTo(new BigDecimal(couponA.getDiscountAmount()));
	}
}