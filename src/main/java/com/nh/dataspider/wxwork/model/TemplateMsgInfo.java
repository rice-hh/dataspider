package com.nh.dataspider.wxwork.model;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 授权的应用信息，注意是一个数组，但仅旧的多应用套件授权时会返回多个agent，对新的单应用授权，永远只返回一个agent
 * @author ni.hua
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMsgInfo {
	
    /**
     * 发送人 对应的 value
     */
    private String msgUserValue;
    
    /**
     * 标题对应的内容
     */
    private String msgTitleValue;
    
    /**
     * 时间 对应的 value
     */
    private String msgTimeValue;
    
    /**
     * 拼接的url
     */
    private String appendUrl;
    
    /**
     * 标题
     */
    private String msgTitle;
    
    /**
     * 消息提醒的用户id（企业微信用户id集合）
     */
    private List<String> toUserIdL;
    
    /**
     * 是否全员推送
     */
    private boolean isAll = false;
    
    /**
     * 业务参数
     */
    private BusinesInfo businesInfo;
    
    @Data
    public static class BusinesInfo{
    	
    	/**
    	 * 流程审批
    	 */
    	private String flowTaskOperatorId;
    	
    	private String taskId;
    	
    	private String businessId;
    	
    	/**
    	 * flowengine中的encode=visualdev中的encode
    	 */
    	private String flowCode;
    	
    	private String flowId;
    	
    	/**
         * 消息提醒的用户id（企业微信用户id集合）
         */
        private List<String> approveUserIdL;
    }
}
