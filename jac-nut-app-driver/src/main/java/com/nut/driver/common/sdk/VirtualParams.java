package com.nut.driver.common.sdk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author yangyudong
 *
 */
public class VirtualParams {

	private String appKey;//应用标示appKey
	private Date timestamp;//时间戳,当前时间毫秒值
	private Long credits;//积分
	private String developBizId="";//充值流水号
	private String uid="";//用户id
	private String description="";//虚拟商品标示符
	private String orderNum="";//兑吧订单号
	private String account="";//虚拟商品充值账号，非必须参数
	private String transfer="";//非必须参数
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	private String params="";
	public Long getCredits() {
		return credits;
	}
	public void setCredits(Long credits) {
		this.credits = credits;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Map<String, String> toRequestMap(String appSecret){
		Map<String, String> map=new HashMap<String, String>();
		
		map.put("description", description);
		map.put("uid", uid);
		map.put("appKey", appKey);
		map.put("developBizId", developBizId);
		map.put("appSecret", appSecret);
		map.put("timestamp",  System.currentTimeMillis()+"");
		map.put("orderNum", orderNum);
		map.put("params", params);
		putIfNotEmpty(map, "transfer", transfer);
		putIfNotEmpty(map, "account", account);

		
		String sign=SignTool.sign(map);
		
		map.remove("appSecret");
		map.put("sign", sign);
		return map;
	}

	private void putIfNotEmpty(Map<String, String> map,String key,String value){
		if(value==null || value.length()==0){
			return;
		}
		map.put(key, value);
	}

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getDevelopBizId() {
		return developBizId;
	}
	public void setDevelopBizId(String developBizId) {
		this.developBizId = developBizId;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	
	public String getTransfer() {
		return transfer;
	}
	public void setTransfer(String transfer) {
		this.transfer = transfer;
	}
}

