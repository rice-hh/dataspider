package com.nh.dataspider.pay.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayOpenAuthTokenAppRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.nh.dataspider.pay.model.AliConfig;
import com.nh.dataspider.pay.model.AlipayRequestModel;
import com.nh.dataspider.pay.model.PaySysConfig;
import com.nh.dataspider.pay.model.WechatTradeRequestModel;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;

/**
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司
 * @date 2021/3/16 10:45
 */
@Component
public class PayUtil {
	
	public static String wehchatPayUrl = "https://api.mch.weixin.qq.com/v3/pay/transactions/native";
	public static String wechatRefundUrl = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
	
	private static CloseableHttpClient httpClient = null;
	
	private static String WECHAT_LOGO_URL = "static/image/wxpay.png";
    private static String ALIPAY_LOGO_URL = "static/image/alipay.png";
	
	@Autowired
    private PaySysConfig paySysConfig;
	
	/**
	 * 
	 * @param merchantId
	 * @param merchantSerialNumber
	 * @param apiV3Key 用户apiv3秘钥
	 * @param privateKeyUrl
	 * @return
	 * @throws IOException
	 * @throws NotFoundException
	 */
	public Verifier getVerifier(String merchantId, String merchantSerialNumber, String apiV3Key, String privateKeyUrl) throws IOException, NotFoundException {
        //获取商户私钥
        PrivateKey merchantPrivateKey = getPrivateKey(privateKeyUrl);
        // 获取证书管理器单例实例
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        
//        Security.setProperty("crypto.policy", "unlimited");
 
        // 向证书管理器增加需要自动更新平台证书的商户信息
        try {
            // 该方法底层已实现同步线程更新证书
            // 详见beginScheduleUpdate()方法
            certificatesManager.putMerchant(merchantId, new WechatPay2Credentials(merchantId,
                    new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException | HttpCodeException e) {
            e.printStackTrace();
        }
 
        //从证书管理器中获取验签器
        return certificatesManager.getVerifier(merchantId);
    }
	
	/**
	 * 加载商户私钥
	 * @param privateKeyUrl 商户私钥文件路径（apiclient_key.pem文件）
	 * @return
	 */
	public PrivateKey getPrivateKey(String privateKeyUrl){
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(privateKeyUrl));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在",e);
        }
    }

	/**
	 * 获取请求微信的httpClient
	 * @param merchantId 商户号
	 * @param merchantSerialNumber 商户API证书序列号
	 * @param privateKeyUrl 商户私钥文件路径（apiclient_key.pem文件）
	 * @param apiV3Key 微信支付平台证书url
	 * @return
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	private CloseableHttpClient getWechatPayHttpClient (String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key) throws NotFoundException, IOException {
		PrivateKey merchantPrivateKey = getPrivateKey(privateKeyUrl);
		
		//获取verifier
        Verifier verifier = getVerifier(merchantId, merchantSerialNumber, apiV3Key, privateKeyUrl);
		
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
		        .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
		        .withValidator(new WechatPay2Validator(verifier));
		
		httpClient = builder.build();
		
		return httpClient;
	}
	
	/**
	 * 获取httppost
	 * @param url 请求url
	 * @param paramJson 请求参数
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private HttpPost getHttpPost(String url, String param) throws JsonGenerationException, JsonMappingException, IOException {
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Content-type","application/json; charset=utf-8");
		httpPost.setEntity(new StringEntity(param, "UTF-8"));
		return httpPost;
	}
	
	/**
	 * 生成微信支付（native支付）二维码
	 * @param wechatTradeModel 请求参数
	 * @param merchantId 商户号
	 * @param merchantSerialNumber 商户证书序列号
	 * @param privateKey 商户私钥
	 * @param apiV3Key 用户apiv3秘钥
	 * @param url 租户id+visualDevId（这边用于拼接回调函数的动态地址【因为apiv3版本的好像没有解谜前的返回参数中没有业务数据】）
	 * @return 二维码url
	 * @throws Exception 
	 */
	public String getWeChatPayQRCode(WechatTradeRequestModel wechatTradeModel, String merchantId, String merchantSerialNumber, String privateKey, String apiV3Key, String url) throws Exception{
		String codeUrl = "";
		if(wechatTradeModel != null) {
			//设置回调地址
//			wechatTradeModel.setNotify_url(notifyUrl+"/api/visualdev/OnlineDev/public/pay/wechatpay/businessNotify/"+url);
			wechatTradeModel.setNotify_url(paySysConfig.getWechatTradeNotifyUrl()+"/"+url);
			
			//请求微信
			CloseableHttpClient httpClient = getWechatPayHttpClient(merchantId, merchantSerialNumber, privateKey, apiV3Key);
			HttpPost httpPost = getHttpPost(wehchatPayUrl, JSONObject.toJSONString(wechatTradeModel));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			String bodyAsString = EntityUtils.toString(response.getEntity());
			System.out.println("bodyAsString="+bodyAsString);
			JSONObject retJson = JSONObject.parseObject(bodyAsString);
			if(response.getStatusLine().getStatusCode() == 200) {
				System.out.println("retJson="+retJson.toJSONString());
				codeUrl = (retJson.containsKey("code_url") && !"".equals(retJson.getString("code_url")))?retJson.getString("code_url"):"";
			}else {
				throw new Exception((retJson.containsKey("message") && !"".equals(retJson.getString("message")))?retJson.getString("message"):"获取二维码出错！");
			}
		}
		if("".equals(codeUrl)) {
			throw new Exception("获取二维码出错！");
		}
		ClassPathResource classPathResource = new ClassPathResource(WECHAT_LOGO_URL);
        codeUrl = this.generateQRCode(codeUrl, classPathResource.getInputStream());
		return codeUrl;
	}
	
	/**
	 * 代商家调用 获取支付二维码 接口
	 * @param model 请求参数
	 * @param appAuthToken 商家授权的token
	 * @return
	 * @throws Exception
	 */
	public String getAliPayQRCode(AlipayTradePrecreateModel model, String appAuthToken) throws Exception {
		//传入的都是 第三方应用 相关参数
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		
		//创建API对应的request
		AlipayTradePrecreateRequest alipayRequest =  new  AlipayTradePrecreateRequest(); 
	    alipayRequest.setReturnUrl(paySysConfig.getAliReturnUrl());
	    alipayRequest.setNotifyUrl(paySysConfig.getAliNotifyUrl()); //在公共参数中设置回跳和通知地址
	    //授权token，代调用必传
	    //若商家从服务市场（包括线下推广渠道）订购模板服务，服务商解析应用收到的 授权通知 获取 app_auth_token；
	    //若商家是通过二维码授权，服务商通过接口换取app_auth_token，可查看 第三方应用授权。
	    alipayRequest.putOtherTextParam("app_auth_token", appAuthToken);

	    alipayRequest.setBizModel(model);

	    AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);
	    if(response.isSuccess()){
	    	JSONObject jsonObject = JSONObject.parseObject(response.getBody());
	    	System.out.println("getAliPayQRCode="+jsonObject);
	        String qr_code = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
	        System.out.println("alipay_trade_precreate_response="+jsonObject.getJSONObject("alipay_trade_precreate_response"));
	        
	        ClassPathResource classPathResource = new ClassPathResource(WECHAT_LOGO_URL);
	        qr_code = this.generateQRCode(qr_code, classPathResource.getInputStream());
	    	return qr_code;
	    }else {
	    	return "error";
	    }
	}
	
	/**
	 * 微信支付回调验证签名
	 * @param request 
	 * @param merchantId 商户号
	 * @param merchantSerialNumber 商户证书序列号
	 * @param privateKeyUrl 商户私钥文件路径（apiclient_key.pem文件）
	 * @param apiV3Key 用户apiv3秘钥
	 * @return
	 * @throws HttpCodeException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws NotFoundException
	 * @throws ValidationException
	 * @throws ParseException
	 */
	public String wechatNotifyVerfiyAndSign(HttpServletRequest request, String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key) throws HttpCodeException, IOException, GeneralSecurityException, NotFoundException, ValidationException, ParseException {
		//获取报文
        String body = getRequestBody(request);
        System.out.println("body="+body);
		//随机串
        String nonce = request.getHeader("Wechatpay-Nonce");
        System.out.println("Wechatpay-Nonce="+nonce);
        //微信传递过来的签名
        String signature = request.getHeader("Wechatpay-Signature");
        System.out.println("Wechatpay-Signature"+signature);
        //时间戳
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        System.out.println("Wechatpay-Timestamp"+timestamp);
        //证书序列号（微信平台）
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        System.out.println("Wechatpay-Serial"+wechatPaySerial);
        
        //获取verifier
        Verifier verifier = getVerifier(merchantId, merchantSerialNumber, apiV3Key, privateKeyUrl);
		
		// 构建request，传入必要参数
		NotificationRequest notificationRequest = new NotificationRequest.Builder().withSerialNumber(wechatPaySerial)
		        .withNonce(nonce)
		        .withTimestamp(timestamp)
		        .withSignature(signature)
		        .withBody(body)
		        .build();
		NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));
		// 验签和解析请求体
		Notification notification = handler.parse(notificationRequest);
		System.out.println("notificatio="+JSONObject.toJSONString(notification));
		System.out.println("DecryptData="+notification.getDecryptData());
		// 从notification中获取解密报文
		return notification.getDecryptData();
	}
	
	public String getRequestBody(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        try {
        	ServletInputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
	
	/**
	 * 微信退款
	 * @param wechatTradeModel 请求参数
	 * @param merchantId 商户号
	 * @param merchantSerialNumber 商户证书序列号
	 * @param privateKeyUrl 商户私钥文件路径（apiclient_key.pem文件）
	 * @param apiV3Key 用户apiv3秘钥
	 * @param url 租户id+visualDevId（这边用于拼接回调函数的动态地址【因为apiv3版本的好像没有解谜前的返回参数中没有业务数据】）
	 * @return
	 * @throws Exception
	 */
	public JSONObject weChatPayRefund(WechatTradeRequestModel wechatTradeModel, String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key, String url) throws Exception{
		JSONObject retJson = null;
		if(wechatTradeModel != null) {
			//设置回调地址
//			wechatTradeModel.setNotify_url(notifyUrl+"/api/visualdev/OnlineDev/public/pay/wechatpay/refundNotify/"+url);
			wechatTradeModel.setNotify_url(paySysConfig.getWechatRefundNotifyUrl()+"/"+url);
			
			//请求微信
			CloseableHttpClient httpClient = getWechatPayHttpClient(merchantId, merchantSerialNumber, privateKeyUrl, apiV3Key);
			HttpPost httpPost = getHttpPost(wechatRefundUrl, JSONObject.toJSONString(wechatTradeModel));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			String bodyAsString1 = EntityUtils.toString(response.getEntity());
			System.out.println(bodyAsString1);
			if(response.getStatusLine().getStatusCode() == 200) {
				HttpEntity responseEntity = response.getEntity();
				
				String bodyAsString = EntityUtils.toString(responseEntity);
				
				retJson = JSONObject.parseObject(bodyAsString);
			}
		}
		if(retJson == null) {
			throw new Exception("微信退款出错！");
		}
		return retJson;
	}

	
	/**
	 * 支付宝退款
	 * @param model 请求参数
	 * @param appAuthToken 商家授权的token
	 * @return
	 * @throws AlipayApiException
	 */
	public JSONObject aliPayRefund(AlipayTradeRefundModel model, String appAuthToken) throws AlipayApiException {
		//传入的都是 第三方应用 相关参数
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		//授权token，代调用必传
	    //若商家从服务市场（包括线下推广渠道）订购模板服务，服务商解析应用收到的 授权通知 获取 app_auth_token；
	    //若商家是通过二维码授权，服务商通过接口换取app_auth_token，可查看 第三方应用授权。
		request.putOtherTextParam("app_auth_token", appAuthToken);
		request.setBizModel(model);
		
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			JSONObject jsonObject = JSONObject.parseObject(response.getBody());
			JSONObject responseJson = jsonObject.getJSONObject("alipay_trade_refund_response");
			//接口中code=10000，仅代表本次退款请求成功，不代表退款成功。
			//接口返回fund_change=Y为退款成功，fund_change=N或无此字段值返回时需通过退款查询接口进一步确认退款状态
			if("10000".equals(responseJson.getString("code"))) {
				return responseJson;
			}else {
				return null;
			}
		}else {
	    	return null;
	    }
	}
	
	/**
	 * 获取应用授权令牌（授权令牌 app_auth_token 在没有重新授权、取消授权或刷新授权的情况下，永久有效。）
	 * 详看官方文档（https://opendocs.alipay.com/isv/03l9c0）
	 * @param appAuthCode 授权回调函数返回的
	 * @return
	 * @throws AlipayApiException
	 */
	public JSONObject getAppAuthTokenInfo(String appAuthCode) throws AlipayApiException {
		
		AlipayRequestModel requestModel = AlipayRequestModel.builder().grant_type("authorization_code").code(appAuthCode).build();
		
		//传入的都是 第三方应用 相关参数
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
		        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		//创建API对应的request
		AlipayOpenAuthTokenAppRequest  request =  new  AlipayOpenAuthTokenAppRequest ();
		request.setBizContent(JSONObject.toJSONString(requestModel));
		AlipayOpenAuthTokenAppResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("getAppAuthTokenInfo="+JSONObject.parseObject(response.getBody()));
			//获取返回参数
			JSONObject jsonObject = JSONObject.parseObject(response.getBody()).getJSONObject("alipay_open_auth_token_app_response");
			System.out.println("alipay_open_auth_token_app_response="+jsonObject);
			//code=10000成功返回，
			if("10000".equals(jsonObject.getString("code"))) {
				return jsonObject;
			}else {
				System.out.println(jsonObject);
			}
		}
		return null;
	}
	
	/**
     * 推荐二维码的白边设置(0-4) 0为无白边
     */
    private static final int QRCODE_MARGIN = 2;
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 55;
    // LOGO高度
    private static final int HEIGHT = 55;
    
    private static final int black = 0xFF000000;

    private static final int white  = 0xFFFFFFFF;
	
	/**
	 * @Decription: 生成带logo图片的二维码并使用Base64编码
	 * @param content 二维码中的内容
	 * @param logoPath logo图片路径
	 * @return 返回的base64格式的图片字符串(png格式)
	 */
	public String generateQRCode(String content, InputStream inputStream) throws Exception {
	    String binary = "";
	    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
	    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
	    hints.put(EncodeHintType.MARGIN, QRCODE_MARGIN);  //设置白边
	    BitMatrix bitMatrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE,QRCODE_SIZE,QRCODE_SIZE,hints);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    BufferedImage image = writeLogoToQrcode(bitMatrix,inputStream);
	    ImageIO.write(image,"png",out);
	    byte[] bytes = out.toByteArray();
	    binary = "data:image/png;base64,".concat(Base64.getEncoder().encodeToString(bytes).trim());
//	    binary = "data:image/png;base64,".concat(new BASE64Encoder().encode(bytes).trim());
	    return binary;
	}
	
	/**
    *
    * @param matrix 二维码距阵相关
    * @param logoUrl logo路径
    * @return
    * @throws IOException
    */
   public static BufferedImage writeLogoToQrcode(BitMatrix matrix,InputStream inputStream) throws IOException {
       //二维码矩阵转换为BufferedImage
       BufferedImage image = toBufferedImage(matrix);
       //是否传入了logo地址
       if(inputStream != null && inputStream.available()>0){
           //URL url = new URL(logoUrl);
           //取得二维码图片的画笔
           Graphics2D gs = image.createGraphics();

           int ratioWidth = image.getWidth()*2/10;
           int ratioHeight = image.getHeight()*2/10;
           //读取logo地址
           Image img = ImageIO.read(inputStream);
           int logoWidth = img.getWidth(null)>ratioWidth?ratioWidth:img.getWidth(null);
           int logoHeight = img.getHeight(null)>ratioHeight?ratioHeight:img.getHeight(null);
           //设置logo图片的位置
           int x = (image.getWidth() - logoWidth) / 2;
           int y = (image.getHeight() - logoHeight) / 2;
           //开画
           //设置背景颜色
           //gs.setBackground(Color.WHITE);
           //通过使用当前绘图表面的背景色进行填充来清除指定的矩形
          // gs.clearRect(x,y,logoWidth,logoHeight);

           //设置偏移量，不设置会导致二维码扫不来
           /*int pixOff = 2;
           Qrcode qrCode = new Qrcode();
           qrCode.setQrcodeErrorCorrect('M');
           qrCode.setQrcodeEncodeMode('B');
           qrCode.setQrcodeVersion(7);
           boolean [][] d = qrCode.calQrcode(qrData);
           for(int y=0;y<d.length;y++) {
               for(int x=0;x<d.length;x++) {
                   if(d[x][y]) {
                       gs.fillRect(x*3+pixOff, y*3+pixOff, 3, 3);
                   }
               }
           }*/
           gs.drawImage(img, x, y, WIDTH, HEIGHT, null);
           gs.dispose();
           img.flush();
       }
       return image;
   }
   
   public static BufferedImage toBufferedImage(BitMatrix matrix) {
       int width = matrix.getWidth();
       int height = matrix.getHeight();
       BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
       for (int x = 0; x < width; x++) {
           for (int y = 0; y < height; y++) {
               image.setRGB(x, y, matrix.get(x, y) ? black : white);
           }
       }
       return image;
   }
}
