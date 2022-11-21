package com.nh.dataspider.util.ocr.huawei;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司
 * @date 2021/3/16 10:51
 */
@Data
@Component
public class HuaweiOCRConfig {
	/**
	 * 华北-北京四：cn-north-4
	 * 华东-上海一：cn-east-3
	 * 华北-北京一：cn-north-1
	 * 华南-广州：cn-south-1
	 */
//	@Value("${ocr_config.huawei.regionName}")
	public String regionName = "cn-east-3";
	
	/**
	 * Access Key Id
	 */
//	@Value("${ocr_config.huawei.AK}")
	public String AK = "U5MQE1J0CJEDE5J4MWKM";
	
	/**
	 * Secret Access Key
	 */
//	@Value("${ocr_config.huawei.SK}")
	public String SK = "BDLdgOwwclQ7KIx5uHWt8LohdakS3RCf3q34lZ9h";
	
}
