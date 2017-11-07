package com.innee.czyhInterface.web.coupon.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.utils.Exceptions;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.service.couponService.v1.CouponServiceImpl;

@RestController("M_API_CouponController")
@RequestMapping(value = "/m/api/coupon", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CouponController {

	private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private CouponServiceImpl couponService;

	/**
	 * 用户领取优惠券
	 * 
	 * @param request
	 * @param response
	 * @param ticket
	 *            用户票
	 * @return
	 */
	@RequestMapping(value = "/receiveCouponByOrder", method = { RequestMethod.GET, RequestMethod.POST })
	public String receiveCouponByOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "ticket", required = false) String ticket,
			@RequestParam(value = "fromOrderId", required = false) String fromOrderId,
			@RequestParam(value = "deliveryId", required = false) String deliveryId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.receiveCouponByOrder(ticket, fromOrderId, deliveryId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("领取优惠券出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	/**
	 * 获取用户订单可分享券额度及分享地址
	 * 
	 * @param request
	 * @param response
	 * @param ticket
	 *            用户票
	 * @return
	 */
	@RequestMapping(value = "/shareCouponByOrder", method = { RequestMethod.GET, RequestMethod.POST })
	public String shareCouponByOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "ticket", required = false) String ticket,
			@RequestParam(value = "fromOrderId", required = false) String fromOrderId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.shareCouponByOrder(ticket, fromOrderId, request);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("领取优惠券出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	
	/****************************************零到壹***************************************************/
	
	/**
	 * 获取优惠券频道券列表
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param ticket
	 *            票
	 * @param orderId
	 *            订单号
	 * @return
	 */
	@RequestMapping(value = "/getChannelCouponList", method = { RequestMethod.GET, RequestMethod.POST })
	public String getChannelCouponList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getChannelCouponList(customerId, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取优惠券频道券列表时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	
	/**
	 * 用户从优惠券频道页领取优惠券
	 * 
	 * @param request
	 * @param response
	 * @param ticket
	 *            用户票
	 * @return
	 */
	@RequestMapping(value = "/receiveCouponByChannel", method = { RequestMethod.GET, RequestMethod.POST })
	public String receiveCouponByChannel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "couponId", required = false) String couponId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.receiveCouponByChannel(customerId, couponId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("领取优惠券出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 获取用户的优惠券
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param ticket
	 *            票
	 * @param status
	 *            优惠卷的状态
	 * @return
	 */
	@RequestMapping(value = "/getUserCouponByStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public String getUserCouponByStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getUserCouponByStatus(customerId, status, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取优惠券时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 获取活动可用的优惠卷列表
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param ticket
	 *            票
	 * @param eventId
	 *            活动ID
	 * @return
	 */
	@RequestMapping(value = "/getAvailableCoupon", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAvailableCoupon(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "cityId", required = false, defaultValue = "1") Integer cityId,
			@RequestParam(value = "clientType", required = false, defaultValue = "1") Integer clientType,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "typeMap", required = false) String typeMap,
			@RequestParam(value = "sponsorMap", required = false) String sponsorMap,
			@RequestParam(value = "useCouponTotal", required = false) String useCouponTotal,
			@RequestParam(value = "goodMap", required = false) String goodMap,
			@RequestParam(value = "total", required = false) String total,
			@RequestParam(value = "freight", required = false) String freight,
			@RequestParam(value = "sponsorId", required = false) String sponsorId) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getAvailableCoupon(cityId, customerId, typeMap,sponsorMap,
					goodMap,total,useCouponTotal,clientType,freight);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取可用优惠券时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 获取活动可用的优惠卷列表
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param ticket
	 *            票
	 * @param eventId
	 *            活动ID
	 * @return
	 */
	@RequestMapping(value = "/getAvailableNum", method = { RequestMethod.GET, RequestMethod.POST })
	public String getAvailableNum(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "cityId", required = false, defaultValue = "1") Integer cityId,
			@RequestParam(value = "clientType", required = false, defaultValue = "1") Integer clientType,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "typeMap", required = false) String typeMap,
			@RequestParam(value = "sponsorMap", required = false) String sponsorMap,
			@RequestParam(value = "goodMap", required = false) String goodMap,
			@RequestParam(value = "total", required = false) String total,
			@RequestParam(value = "useCouponTotal", required = false) String useCouponTotal,
			@RequestParam(value = "freight", required = false) String freight,
			@RequestParam(value = "sponsorId", required = false) String sponsorId) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getAvailableNum(cityId, customerId, typeMap,sponsorMap,
					goodMap,total,useCouponTotal,clientType,freight);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取可用优惠券时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 用户从优惠券频道页领取优惠券
	 * 
	 * @param request
	 * @param response
	 * @param ticket
	 *            用户票
	 * @return
	 */
	@RequestMapping(value = "/backCoupon", method = { RequestMethod.GET, RequestMethod.POST })
	public String backCoupon(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "status", required = false) Integer status) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.backCoupon(orderId,status);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("取消优惠券出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 用户领取优惠券
	 * 
	 * @param request
	 * @param response
	 * @param ticket
	 *            用户票
	 * @return
	 */
	@RequestMapping(value = "/receiveCoupon", method = { RequestMethod.GET, RequestMethod.POST })
	public String receiveCouponByPhone(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "deliveryId", required = false) String deliveryId,
			@RequestParam(value = "customerId", required = false) String customerId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.receiveCoupon(deliveryId, customerId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("领取优惠券出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	@RequestMapping(value = "/getDiscountAmount", method = { RequestMethod.GET, RequestMethod.POST })
	public String getDiscountAmount(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "clientType", required = false, defaultValue = "1") Integer clientType,
			@RequestParam(value = "couponDeliveryId", required = false) String couponDeliveryId,
			@RequestParam(value = "postageCouponId", required = false) String postageCouponId,
			@RequestParam(value = "freight", required = false) String freight,
			@RequestParam(value = "goodsSkuList", required = false) String goodsSkuList,
			@RequestParam(value = "orderTotal", required = false) String orderTotal) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getDiscountAmount(customerId, orderTotal,couponDeliveryId,postageCouponId,freight,
					goodsSkuList,clientType);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("计算优惠金额时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	/**
	 * 获取优惠券频道券列表
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param ticket
	 *            票
	 * @param orderId
	 *            订单号
	 * @return
	 */
	@RequestMapping(value = "/getCouponByEvent", method = { RequestMethod.GET, RequestMethod.POST })
	public String getCouponByEvent(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "goodsId", required = false) String goodsId,
			@RequestParam(value = "sponsorId", required = false) String sponsorId,
			@RequestParam(value = "typeA", required = false) String typeA) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = couponService.getCouponByEvent(customerId, goodsId, typeA,sponsorId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取优惠券频道券列表时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

}