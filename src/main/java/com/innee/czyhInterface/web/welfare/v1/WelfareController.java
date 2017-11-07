package com.innee.czyhInterface.web.welfare.v1;

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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.service.InvitationService.v1.InvitationService;
import com.innee.czyhInterface.service.welfareService.v1.WelfareService;
import com.innee.czyhInterface.util.Constant;
import com.innee.czyhInterface.util.DictionaryUtil;
import com.innee.czyhInterface.util.PromptInfoUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@RestController("M_API_V1_WelfareController")
@RequestMapping(value = "/m/api/welfare", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class WelfareController {

	private static final Logger logger = LoggerFactory.getLogger(WelfareController.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private WelfareService welfareService;

	@RequestMapping(value = "/welfareGoodsList", method = { RequestMethod.GET, RequestMethod.POST })
	public String welfareGoodsList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.welfareGoodsList(customerId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取福利社商品列表时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/welfareGoodsListhtml", method = { RequestMethod.GET, RequestMethod.POST })
	public String welfareGoodsListhtml(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.welfareGoodsListhtml(customerId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取h5福利社商品列表时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/convertOrder", method = { RequestMethod.GET, RequestMethod.POST })
	public String convertOrder(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "bonusGoodsId", required = false) String bonusGoodsId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.convertOrder(customerId, bonusGoodsId, name, phone, address,
					request.getRemoteAddr());
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg(PromptInfoUtil.getPrompt(PromptInfoUtil.czyhInterface_BONUS,
					"czyhInterface.web.bonus.convertOrder.failure"));
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/welfareOrderGoodsList", method = { RequestMethod.GET, RequestMethod.POST })
	public String welfareOrderGoodsList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.welfareOrderGoodsList(customerId, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg(PromptInfoUtil.getPrompt(PromptInfoUtil.czyhInterface_BONUS,
					"czyhInterface.web.bonus.bonusOrderList.failure"));
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/welfareBonusDeail", method = { RequestMethod.GET, RequestMethod.POST })
	public String welfareBonusDeail(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.welfareBonusDeail(customerId, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg(PromptInfoUtil.getPrompt(PromptInfoUtil.czyhInterface_BONUS,
					"czyhInterface.web.bonus.myBonusDeail.failure"));
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/welfareGoodsDetail", method = { RequestMethod.GET, RequestMethod.POST })
	public String welfareGoodsDetail(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "goodsId", required = false) String goodsId) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = welfareService.welfareGoodsDetail(customerId, goodsId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取福利社商品详情时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

}