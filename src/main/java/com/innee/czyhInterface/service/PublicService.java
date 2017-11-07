package com.innee.czyhInterface.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.mapper.JsonMapper;
import org.springside.modules.utils.Exceptions;
import org.springside.modules.utils.Identities;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Maps;
import com.innee.czyhInterface.dao.PublicImageDAO;
import com.innee.czyhInterface.dto.coupon.PublicDTO;
import com.innee.czyhInterface.dto.coupon.ResponseDTO;
import com.innee.czyhInterface.entity.TPublicImages;
import com.innee.czyhInterface.util.Constant;
import com.innee.czyhInterface.util.PropertiesUtil;
import com.innee.czyhInterface.util.publicImage.common.QiniuException;
import com.innee.czyhInterface.util.publicImage.http.Response;
import com.innee.czyhInterface.util.publicImage.storage.UploadManager;
import com.innee.czyhInterface.util.publicImage.util.Auth;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 公共方法(1.公共上传图片方法)
 * 
 * @author jinshengzhi
 *
 */
@Component
@Transactional
public class PublicService {

	private static final Logger logger = LoggerFactory.getLogger(PublicService.class);

	private static JsonMapper mapper = new JsonMapper(Include.ALWAYS);

	private static final String imageMogr2 = "imageMogr2";// 图片高级处理

	@Autowired
	private PublicImageDAO publicImageDAO;

	@Autowired
	private CacheManager cacheManager;

	/**
	 * 为客户端生成图片上传Token
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ResponseDTO getToken() {

		ResponseDTO responseDTO = new ResponseDTO();

		PublicDTO publicDTO = new PublicDTO();
		Auth auth = Auth.create(PropertiesUtil.getProperty("qiniuAk"), PropertiesUtil.getProperty("qiniuSK"));
		// 获取token的时候为固定上传空间未来多空间时 可增加空间参数进行上传
		String token = auth.uploadToken(PropertiesUtil.getProperty("bucket"), null, 1000 * 3600 * 24 * 12 * 10, null);

		publicDTO.setToken(token);
		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("getToken", publicDTO);
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * app端图片上传接口
	 * 
	 * @param file
	 * @return
	 */
	public ResponseDTO appUpload(File file) {
		ResponseDTO responseDTO = new ResponseDTO();
		String res = null;

		String path = "comment/" + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "/";
		String name = Identities.uuid2();
		String type = ".jpg";

		StringBuilder urlPath = new StringBuilder(path).append(name).append(type);
		try {
			String token = getQnToken();
			res = appuploadQn(file, urlPath.toString(), token);
		} catch (IOException e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
		}
		TPublicImages tpublicImage = new TPublicImages();
		tpublicImage.setFurl(urlPath.toString());
		tpublicImage.setFtype(1);
		tpublicImage.setIsThumbnail(1);
		tpublicImage.setFcreateTime(new Date());
		publicImageDAO.save(tpublicImage);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("上传图片成功！");

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("urlPath", PropertiesUtil.getProperty("publicQnService") + urlPath.toString());
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * app图片上传接口七牛方法
	 * 
	 * @param FilePath
	 * @param name
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public String appuploadQn(File FilePath, String name, String token) throws IOException {
		Response res = null;
		try {
			// 创建上传对象
			UploadManager uploadManager = new UploadManager();
			// 调用put方法上传
			res = uploadManager.put(FilePath, name, token);
		} catch (QiniuException e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			Response r = e.response;
			// 请求失败时打印的异常的信息
			// System.out.println(r.toString());
			logger.info(r.bodyString());
			try {
				// System.out.println(r.bodyString());
				logger.info(r.bodyString());
			} catch (QiniuException e1) {
				logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
				// System.out.println(r.bodyString());
			}
		}
		return res.bodyString();
	}

	/**
	 * web图片上传接口
	 * 
	 * @param file
	 * @return
	 */
	public ResponseDTO webUpload(File file) {

		ResponseDTO responseDTO = new ResponseDTO();

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(now);

		String path = "comment/" + date + "/";
		String name = Identities.uuid2();
		String type = ".jpg";

		StringBuilder urlPath = new StringBuilder(path).append(name).append(type);
		try {
			String token = getQnToken();
			webuploadQn(file, urlPath.toString(), token);
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			responseDTO.setSuccess(false);
			responseDTO.setStatusCode(103);
			responseDTO.setMsg("图片上传到服务器时出错！");
			return responseDTO;
		}

		TPublicImages tpublicImage = new TPublicImages();
		tpublicImage.setFurl(urlPath.toString());
		tpublicImage.setFtype(1);
		tpublicImage.setIsThumbnail(1);
		tpublicImage.setFcreateTime(now);
		publicImageDAO.save(tpublicImage);

		responseDTO.setSuccess(true);
		responseDTO.setStatusCode(0);
		responseDTO.setMsg("图片上传到服务器成功！");

		Map<String, Object> returnData = Maps.newHashMap();
		returnData.put("urlPath", PropertiesUtil.getProperty("publicQnService") + urlPath.toString());
		responseDTO.setData(returnData);
		return responseDTO;
	}

	/**
	 * web端上传七牛调用接口
	 * 
	 * @param FilePath
	 * @return
	 * @throws IOException
	 */
	public String webuploadQn(File FilePath, String name, String token) throws IOException {
		Response res = null;
		try {
			UploadManager uploadManager = new UploadManager();
			// 调用put方法上传
			res = uploadManager.put(FilePath, name, token);
		} catch (QiniuException e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
			Response r = e.response;
			// 请求失败时打印的异常的信息
			// System.out.println(r.bodyString());
			logger.info(r.bodyString());
			try {

				// System.out.println(r.bodyString());
				logger.info(r.bodyString());
			} catch (QiniuException e1) {
				logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
				// System.out.println(r.bodyString());
				logger.info(r.bodyString());
			}
		}
		return res.bodyString();
	}

	
	/**
	 * 上传头像通用版接口
	 * 
	 * @param file
	 * @return
	 */
	public String logoUpload(String filebase64, File file, Integer flag) {
		String urlLogo = null;
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sdf.format(now);

		String path = "logo/" + date + "/";
		String name = Identities.uuid2();
		String type = ".jpg";

		StringBuilder urlPath = new StringBuilder(path).append(name).append(type);
		try {
			if (flag == 1) {
				String token = getQnTokenLogo();
//				System.out.println(token + "是什么");
				logoUploadQn(filebase64, urlPath.toString(), token);
			} else {
				String token = getQnTokenLogo();
				webuploadQn(file, urlPath.toString(), token);
			}
		} catch (Exception e) {
			logger.error(Exceptions.getStackTraceAsString(Exceptions.getRootCause(e)));
		}
//		System.out.println(PropertiesUtil.getProperty("publicQnServiceLogo") + urlPath.toString());
		urlLogo = PropertiesUtil.getProperty("publicQnServiceLogo") + urlPath.toString();
		
		return urlLogo;
	}
	
	/**
	 * 通用版接口上传调用七牛方法
	 * 
	 * @param FilePath
	 * @return
	 * @throws IOException
	 */
	public String logoUploadQn(String FilePath, String name, String token) throws IOException {
		Response res = null;
		try {
			UploadManager uploadManager = new UploadManager();
			String[] headAndBody = StringUtils.split(FilePath, ",");
			String body = headAndBody[1];
			byte[] fileByte = Base64.decodeBase64(body);
//			byte[] fileByte = Base64Utils.decodeFromString(body);
			
			// 调用put方法上传
			res = uploadManager.put(fileByte, name, token);
		} catch (QiniuException e) {
			Response r = e.response;
			// 请求失败时打印的异常的信息
			logger.warn(r.bodyString() + "失败原因");
		}
		return res.bodyString();
	}
	
	/**
	 * 申请comment空间token
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public String getQnToken() {

		String qtoken = "Qntoken";
		String res = null;

		// 获取缓存中的key
		Cache customerEentityCache = cacheManager.getCache(Constant.QnUserUploadToken);
		// 获取缓存中的token
		Element elementB = customerEentityCache.get(qtoken);
		if (elementB == null) {
			// token默认失效时间为86400s 为24小时
			Auth auth = Auth.create(PropertiesUtil.getProperty("qiniuAk"), PropertiesUtil.getProperty("qiniuSK"));
			String token = auth.uploadToken(PropertiesUtil.getProperty("bucket"));
			elementB = new Element(qtoken, token);
			customerEentityCache.put(elementB);
			res = token;
		} else {
			res = (String) elementB.getObjectValue();
		}
		return res;
	}
	
	/**
	 * 申请上传头像空间token
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public String getQnTokenLogo() {

		String qtoken = "Qntoken";
		String res = null;

		// 获取缓存中的key
		Cache customerEentityCache = cacheManager.getCache(Constant.QnUserUploadTokenLogo);
		// 获取缓存中的token
		Element elementB = customerEentityCache.get(qtoken);
		if (elementB == null) {
			// token默认失效时间为86400s 为24小时
			Auth auth = Auth.create(PropertiesUtil.getProperty("qiniuAk"), PropertiesUtil.getProperty("qiniuSK"));
			String token = auth.uploadToken(PropertiesUtil.getProperty("bucketlogo"));
			elementB = new Element(qtoken, token);
			customerEentityCache.put(elementB);
			res = token;
		} else {
			res = (String) elementB.getObjectValue();
		}
		return res;
	}

	/**
	 * 按照传入的比例对图片进行缩略
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public String getThumbnailview(String urlPath, Integer proportion) {

		StringBuffer sb = new StringBuffer();
		sb.append(urlPath);
		sb.append("?");
		sb.append(imageMogr2).append("/");
		sb.append("thumbnail").append("/").append("!");
		sb.append(proportion).append("p");

		return sb.toString();
	}

}