package com.innee.czyhInterface.service.InvitationService.v1;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.internal.QueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.JsonMapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.innee.czyhInterface.dao.CustomerDAO;
import com.innee.czyhInterface.dao.CustomerInfoDAO;
import com.innee.czyhInterface.dao.CustomerSubscribeDAO;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.dto.invitation.AppShareDTO;
import com.innee.czyhInterface.dto.invitation.FansOrderDTO;
import com.innee.czyhInterface.dto.invitation.InvitationDTO;
import com.innee.czyhInterface.entity.TCustomer;
import com.innee.czyhInterface.entity.TCustomerInfo;
import com.innee.czyhInterface.entity.TCustomerSubscribe;
import com.innee.czyhInterface.impl.invitationImpl.InvitationsService;
import com.innee.czyhInterface.service.CommonService;
import com.innee.czyhInterface.util.Constant;
import com.innee.czyhInterface.util.DictionaryUtil;

@Component
@Transactional
public class InvitationService implements InvitationsService{

	private static final Logger logger = LoggerFactory.getLogger(InvitationService.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private CustomerInfoDAO customerInfoDAO;
	
	@Autowired
	private CommonService commonService;

	@Autowired
	private CustomerSubscribeDAO customerSubscribeDAO;

	@Transactional(readOnly = true)
	public ResponseDTO appShareSign(String customerId, HttpServletRequest request) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}

		AppShareDTO appShareDTO = new AppShareDTO();
		appShareDTO.setTitle(DictionaryUtil.getString(DictionaryUtil.Invitation, 1));
		appShareDTO.setImageUrl(Constant.defaultHeadImgUrl);// "http://goods.021-sdeals.cn/logo.png"
		appShareDTO.setBrief(DictionaryUtil.getCode(DictionaryUtil.Invitation, 1));

		appShareDTO.setUrl(new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName())
				.append(":").append(request.getServerPort()).append(request.getContextPath())
				.append("/api/system/share/invite/").append(customerId).toString());

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("appShare", appShareDTO);
		responseDTO.setData(returnData);
		return responseDTO;
	}

	@Transactional(readOnly = true)
	public ResponseDTO getInvitationList(String customerId) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}

		List<InvitationDTO> invitationDTOList = Lists.newArrayList();
		InvitationDTO invitationDTO = null;
		TCustomer tCustomer = null;

		List<TCustomerSubscribe> tCustomerSubscribeList = customerSubscribeDAO.getByCustomer(customerId);
		if (tCustomerSubscribeList != null) {
			for (TCustomerSubscribe tCustomerSubscribe : tCustomerSubscribeList) {
				if (tCustomerSubscribe.getFtype() == 1) {
					invitationDTO = new InvitationDTO();
					tCustomer = customerDAO.findCustomerIdByName(tCustomerSubscribe.getFoperationId());

					invitationDTO.setName(tCustomer.getFname());
					if (tCustomer.getFphoto() != null) {
						invitationDTO.setImageUrl(tCustomer.getFphoto());
					} else {
						invitationDTO.setImageUrl(Constant.defaultHeadImgUrl);// "http://goods.021-sdeals.cn/logo.png"
					}

					if (tCustomerSubscribe.getFupdateTime() != null) {
						invitationDTO.setFirstOrderTime(
								DateUtil.formatDate(tCustomerSubscribe.getFupdateTime(), "yyyy-MM-dd"));
					}
					invitationDTO.setCouponDes(tCustomerSubscribe.getFcouponValue());
					invitationDTO.setArrival("已到帐");
					invitationDTOList.add(invitationDTO);
				}
			}

		}

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("invitationList", invitationDTOList);
		returnData.put("couponPerson", tCustomerSubscribeList.size());
		returnData.put("couponNum", tCustomerSubscribeList.size());
		responseDTO.setData(returnData);
		return responseDTO;
	}
	
	@Transactional(readOnly = true)
	public ResponseDTO getInvitationTop(String customerId) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (StringUtils.isBlank(customerId)) {
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(201);
			responseDTO.setMsg("customerId参数不能为空，请检查customerId的传递参数值！");
			return responseDTO;
		}
		
		List<FansOrderDTO> FansOrderDTOList = Lists.newArrayList();
		FansOrderDTO fansOrderDTO = null;
		boolean isMine = true;
		

		StringBuilder hql = new StringBuilder();
		hql.append("select c.id as id,c.fname as name,t.fans as fans,c.fcreateTime as fcreateTime,c.fphone as phone,c.fphoto as photo ")
			.append(",t.forderFans as forderFans from TCustomerInfo t inner join TCustomer c on t.fcustomerId = c.id ")
			.append(" order by t.fans desc,t.fcreateTime");
		Query q = commonService.createQuery(hql.toString());
	    q.unwrap(QueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    q.setFirstResult(0).setMaxResults(3);
		List<Map<String, Object>> list =  q.getResultList();
		int i = 1;
		for (Map<String, Object> amap : list) {
			fansOrderDTO = new FansOrderDTO();
			if (amap.get("name") != null && StringUtils.isNotBlank(amap.get("name").toString())) {
				String str =amap.get("name").toString();
				if(str.length()>2){
					fansOrderDTO.setName(str.substring(0,str.length()-(str.substring(1)).length())+"**");
				}else{
					fansOrderDTO.setName(str);
				}
			}
			if (amap.get("id") != null && StringUtils.isNotBlank(amap.get("id").toString())) {
				if(customerId.equals(amap.get("id").toString())){
				}
			}
			if (amap.get("forderFans") != null && StringUtils.isNotBlank(amap.get("forderFans").toString())) {
				fansOrderDTO.setOrderFansNum(Integer.parseInt(amap.get("forderFans").toString()));
			}
			if (amap.get("fans") != null && StringUtils.isNotBlank(amap.get("fans").toString())) {
				fansOrderDTO.setFansNum(Integer.parseInt(amap.get("fans").toString()));
			}
			if (amap.get("photo") != null && StringUtils.isNotBlank(amap.get("photo").toString())) {
				fansOrderDTO.setImageUrl(amap.get("photo").toString());
			}else{
				fansOrderDTO.setImageUrl(Constant.defaultHeadImgUrl);// "http://goods.021-sdeals.cn/logo.png"
			}
			if (amap.get("phone") != null && StringUtils.isNotBlank(amap.get("phone").toString())) {
				String str =amap.get("phone").toString();
				fansOrderDTO.setPhone(str.substring(0,str.length()-(str.substring(3)).length())+"****"+str.substring(7));
			}
			fansOrderDTO.setRanking(i);
			i++;
			FansOrderDTOList.add(fansOrderDTO);
		}
		TCustomerInfo customerInfo = customerInfoDAO.getByCustomerId(customerId);
		TCustomer tCustomer = customerDAO.findOne(customerId);
		fansOrderDTO = new FansOrderDTO();
		if(customerInfo.getFans()!=null){
			fansOrderDTO.setFansNum(customerInfo.getFans());
		}
		if(customerInfo.getForderFans()!=null){
			fansOrderDTO.setOrderFansNum(customerInfo.getForderFans());
		}
		if(tCustomer.getFphoto()!=null){
			fansOrderDTO.setImageUrl(tCustomer.getFphoto());
		} else {
			fansOrderDTO.setImageUrl(Constant.defaultHeadImgUrl);// "http://goods.021-sdeals.cn/logo.png"
		}
		fansOrderDTO.setMine(true);
		String name =tCustomer.getFname();
		if(name.length()>2){
			fansOrderDTO.setName(name.substring(0,name.length()-(name.substring(1)).length())+"**");
		}else{
			fansOrderDTO.setName(name);
		}
		String str = null;
		fansOrderDTO.setRanking(customerInfoDAO.rankIng(customerInfo.getFans(), customerInfo.getFcreateTime()).intValue()+1);
		if(StringUtils.isNotBlank(tCustomer.getFphone())){
			str = tCustomer.getFphone();
			fansOrderDTO.setPhone(str.substring(0,str.length()-(str.substring(3)).length())+"****"+str.substring(7));
		}
		FansOrderDTOList.add(fansOrderDTO);

		
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("invitationList", FansOrderDTOList);
		responseDTO.setData(returnData);
		return responseDTO;
	}
}