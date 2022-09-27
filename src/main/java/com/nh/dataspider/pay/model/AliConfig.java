package com.nh.dataspider.pay.model;

public class AliConfig {

	// 第三方应用appid
	public static String APPID = "2021003141610273";

	// 私钥 pkcs8格式的
	public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCB7zWuPRTGkmeRWd40WU8ieUrdms0PyweAZw1Vaa47vT+nLub3g0EJW/GmXJfaZg/6cVyhYi2AkrpDGclfs8QIKajIGswt4cKDhJSZJGB5g87+CAnqCmYTUQWXy2VAHNKjknLnZXSWanKZImEync+/wmoXhhh+Ff/u/8+VXfzjFMEM+bPDwMiRjPYpVBfH/Ml8Fi8oMOVgJhWuk+DbqqI1MPNs/sjoBRFKzn7jVA4xaX2s3rVhoICShd63kiVL/gfYXiJFAvx/QAthvMZoRhQzRu+fdeI+DqSSsJIRkA8DRYDV/CTqXjCsP145DxtJbG3lRY5XE8xJRYbd7WdH7OubAgMBAAECggEAG81aeCDbvC2Beal4EU120FKRwV8pN5Li1Qe346nMPT4eS6COjTGJjf4dc9JmL7Yc/yKYhnOidttufYMcAp1Nec2oLsEFJWAkJc4NmcMd3YJlnReNP4XgF4wDnVpJHPHN5HAmo/qierjmMnwXviPkWwy3dLcdsIT2kWYtPbGxtcjeXTKDt3K8Qc8wP6G3TciRk8Mho3tJIzRMNDJ0RX1tcmf+eOi4xAT89EK36H+zy0Im2kgesasXBut4QUszDiMdHOpdsqm2Ta7X3oMf7BigQTp/1IoJurvhv1swKUewokejJQMOXXtogE3Vd7fed4A/S5z8DJK5p2gvqWtoMzK8CQKBgQD3Zdv1nXW+cTNqHZGuB81T/Zd3KBFF9dcAMwzvYp6IY6pTppqg7UIRv6ALO4luMpYTmJOTIgRPKOAzO8TU8nfTKsVSNomyeGEFZgUxwuRT//tbd82S2IyNoHX80DUjofQt79whh3bT1bvyKpcjTAhipoxsrqdxTgchyUstMFespQKBgQCGc8iG0THoJHxXZlOjd1jt68wdnnBD4/BY9wPbzefF6w69sqc7z1qmJA+UCYDUeziomOKjcrY7MpQtfKbXul4l7Uz3nby59rZSP8r11eCVy4QUQS5uFqs2A1qYGDpMTOJy9l51jD0lh0JYOIFiogtcgPfT6jPnkPrm9YGlvC2DPwKBgQCfpVBq0AByM3R9LrWaexhp8cQzNVzoK3thepAHTjmFQ+Ne9azk2hX/hAvQE8xcmzIzVQsuMA5d0r/S7+fVjt36G6iXo+/5hir9o9cIRH65If/fB5FWCH2yyvA4g1c3NkRu/FCuJk9s5YjR/d/n/CGRH26Ql6gfur1pdQ8ZCxAYrQKBgBHhFbpv2S4go/RPZDHprga9g4Aw0IcGqbB68KVrJTYZgvVQ65I1ib1sMDIkbWBUHn21DvZjS75lhHTkDZ/EZb6vZrvK2wzztN0NJKU6Uso4rRCwdkcndo8RNRvipOX+l2JoRrlk88Co0va+VN5T5CTh9ZLVox9ZFTRfU2VBt9+rAoGBAODTI2pwYMiaoDWd71i5L4scVCGAsYoqdc3NFx+occP5GPgSzdq/9WMyhSMcKCTDhw9kmQEIAa3KYAyfvI4+IzAIFhx0KEaS7KFSR/uIYM46MIPQJH1X+h0sEiOwMbdfRhZCpiRrNIpyqYwVWt+ZKr9qwpw07Jbc+9c3y0gbSa5W";

	// 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "https://tcloud.ylsaas.com.cn:8848/api/visualdev/OnlineDev/public/pay/alipay/businessNotify";
//	public static String notify_url = "https://cloud.ylsaas.com.cn/api/system/Base/Pay/alipay/notify";

	// 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
	public static String return_url = "https://tcloud.ylsaas.com.cn:8848/api/visualdev/OnlineDev/public/pay/alipay/businessNotify";
//	public static String return_url = "https://cloud.ylsaas.com.cn/api/system/Base/Pay/alipay/notify";

	// 请求网关地址（支付宝网关 固定）
	public static String URL = "https://openapi.alipay.com/gateway.do";

	// 编码
	public static String CHARSET = "UTF-8";

	// 返回格式
	public static String FORMAT = "json";

	// 支付宝公钥
	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgs6Ve+HShTArz1PCrzptSE3+N7xp/ycKO3iTvsrBtwS54SSfe4SKzJ2R7CrVDDc0QhGIXbJfdXPervaSS+gNLqP+G6sF+Fou0o28SWWYvK5VpReCystndTSxD3FYgg6tfnjgYggNvPJ6XwUkbY/i4gJbeUkoWEh44xlgb8rCJ4H+zQSQGaKD6kJh2EO/xW+PEpkq/EJDf11BZ90w3Zq9NrLJCxoW4sfx1qX29dBn0rZPICp/bRHSQn+KOp7LMeJBF4yC0aTg7prSDAylhUbPwC0xYh2Aq3MOqSni3dwFRGvPqZwgOv9HISMXgbHMAWhswRE8QndN4nQnqi2JHgahcQIDAQAB";

	// 日志记录目录定义在 logFile 中
	public static String log_path = "/log";

	// RSA2
	public static String SIGNTYPE = "RSA2";
}
