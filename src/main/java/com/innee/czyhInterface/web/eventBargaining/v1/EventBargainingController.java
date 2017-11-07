package com.innee.czyhInterface.web.eventBargaining.v1;

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
import com.innee.czyhInterface.service.FxlService;
import com.innee.czyhInterface.service.eventBargaining.v1.EventBargainingService;

/**
 * 砍一砍
 * 
 * @author jinshengzhi
 *
 */
@RestController("M_API_V1_BargainingController")
@RequestMapping(value = "/m/api/bargaining", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EventBargainingController {

	private static Logger logger = LoggerFactory.getLogger(EventBargainingController.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private FxlService fxlService;

	@Autowired
	private EventBargainingService eventBargainingService;

	@RequestMapping(value = "/eventBargainingList", method = { RequestMethod.GET, RequestMethod.POST })
	public String eventBargainingList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {

		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.eventBargainingList(pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取砍一砍商品列表时出错！");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	/*
	 * @RequestMapping(value = "/bargainToPayOrder", method = {
	 * RequestMethod.GET, RequestMethod.POST }) public String
	 * bargainToPayOrder(HttpServletRequest request, HttpServletResponse
	 * response,
	 * 
	 * @RequestParam(value = "callback", required = false) String callback,
	 * 
	 * @RequestParam(value = "clientType", required = false, defaultValue = "1")
	 * Integer clientType,
	 * 
	 * @RequestParam(value = "ticket", required = false) String ticket,
	 * 
	 * @RequestParam(value = "recipient", required = false) String recipient,
	 * 
	 * @RequestParam(value = "phone", required = false) String phone,
	 * 
	 * @RequestParam(value = "address", required = false) String address,
	 * 
	 * @RequestParam(value = "insuranceInfo", required = false) String
	 * insuranceInfo,
	 * 
	 * @RequestParam(value = "remark", required = false) String remark,
	 * 
	 * @RequestParam(value = "payType", required = false) Integer payType,
	 * 
	 * @RequestParam(value = "payClientType", required = false, defaultValue =
	 * "1") Integer payClientType,
	 * 
	 * @RequestParam(value = "channel", required = false) Integer channel,
	 * 
	 * @RequestParam(value = "gps", required = false) String gps,
	 * 
	 * @RequestParam(value = "customerBargainingId", required = false) String
	 * customerBargainingId,
	 * 
	 * @RequestParam(value = "deviceId", required = false) String deviceId) {
	 * ResponseDTO responseDTO = null; try { responseDTO =
	 * eventBargainingService.bargainToPayOrder(clientType, ticket,
	 * payClientType, recipient, phone, address, insuranceInfo, remark, payType,
	 * request.getRemoteAddr(), channel, gps, customerBargainingId, deviceId); }
	 * catch (Exception e) {
	 * logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e))
	 * ); responseDTO = new ResponseDTO(); responseDTO.setSuccess(false);
	 * responseDTO.setStatusCode(200); responseDTO.setMsg("砍一砍获取支付信息时出错！"); } if
	 * (StringUtils.isBlank(callback)) { return mapper.toJson(responseDTO); }
	 * else { return mapper.toJsonP(callback, responseDTO); } }
	 */

	@RequestMapping(value = "/getbarrage", method = { RequestMethod.GET, RequestMethod.POST })
	public String getbarrage(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getbarrage(customerId, customerBargainingId, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取弹幕列表出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	/**
	 * 翻译活动专用接口
	 * 
	 * @param request
	 * @param response
	 * @param callback
	 * @param customerBargainingId
	 * @return
	 */
	@RequestMapping(value = "/getGamesType", method = { RequestMethod.GET, RequestMethod.POST })
	public String getGamesType(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "eventId", required = false) String eventId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getGamesType(eventId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取活动翻译接口出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/getMyBargain", method = { RequestMethod.GET, RequestMethod.POST })
	public String getMyBargain(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "eventBargainingId", required = false) String eventBargainingId,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId,
			@RequestParam(value = "customerId", required = false) String customerId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getMyBargain(eventBargainingId, customerId, customerBargainingId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取砍一砍活动首页报错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/helpBargain", method = { RequestMethod.GET, RequestMethod.POST })
	public String helpBargain(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId,
			@RequestParam(value = "customerId", required = false) String customerId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.helpBargain(customerId, customerBargainingId);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("用户砍一砍报错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/getbargainList", method = { RequestMethod.GET, RequestMethod.POST })
	public String getbargainList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId,
			@RequestParam(value = "eventBargainingId", required = false) String eventBargainingId,
			@RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getbargainList(customerId, eventBargainingId,
					pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取砍价英雄榜列表出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/getbargainHelpList", method = { RequestMethod.GET, RequestMethod.POST })
	public String getbargainHelpList(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getbargainHelpList(customerId, customerBargainingId, pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取帮砍用户列表时出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

	@RequestMapping(value = "/getbargainShare", method = { RequestMethod.GET, RequestMethod.POST })
	public String getbargainShare(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerBargainingId", required = false) String customerBargainingId) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getbargainShare(customerBargainingId, request);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("分享当前砍一砍时出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}
	
	@RequestMapping(value = "/getbargaining", method = { RequestMethod.GET, RequestMethod.POST })
	public String getbargaining(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "callback", required = false) String callback,
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize,
			@RequestParam(value = "offset", required = false) Integer offset) {
		ResponseDTO responseDTO = null;
		try {
			responseDTO = eventBargainingService.getbargaining(customerId,pageSize, offset);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO = new ResponseDTO();
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(200);
			responseDTO.setMsg("获取正在砍价出错");
		}
		if (StringUtils.isBlank(callback)) {
			return mapper.toJson(responseDTO);
		} else {
			return mapper.toJsonP(callback, responseDTO);
		}
	}

}