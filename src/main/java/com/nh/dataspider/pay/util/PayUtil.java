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
 * @author YlSaaS?????????
 * @version V3.1.0
 * @copyright ??????????????????????????????????????????
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
	 * @param apiV3Key ??????apiv3??????
	 * @param privateKeyUrl
	 * @return
	 * @throws IOException
	 * @throws NotFoundException
	 */
	public Verifier getVerifier(String merchantId, String merchantSerialNumber, String apiV3Key, String privateKeyUrl) throws IOException, NotFoundException {
        //??????????????????
        PrivateKey merchantPrivateKey = getPrivateKey(privateKeyUrl);
        // ?????????????????????????????????
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        
//        Security.setProperty("crypto.policy", "unlimited");
 
        // ?????????????????????????????????????????????????????????????????????
        try {
            // ????????????????????????????????????????????????
            // ??????beginScheduleUpdate()??????
            certificatesManager.putMerchant(merchantId, new WechatPay2Credentials(merchantId,
                    new PrivateKeySigner(merchantSerialNumber, merchantPrivateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException | HttpCodeException e) {
            e.printStackTrace();
        }
 
        //????????????????????????????????????
        return certificatesManager.getVerifier(merchantId);
    }
	
	/**
	 * ??????????????????
	 * @param privateKeyUrl ???????????????????????????apiclient_key.pem?????????
	 * @return
	 */
	public PrivateKey getPrivateKey(String privateKeyUrl){
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(privateKeyUrl));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("?????????????????????",e);
        }
    }

	/**
	 * ?????????????????????httpClient
	 * @param merchantId ?????????
	 * @param merchantSerialNumber ??????API???????????????
	 * @param privateKeyUrl ???????????????????????????apiclient_key.pem?????????
	 * @param apiV3Key ????????????????????????url
	 * @return
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	private CloseableHttpClient getWechatPayHttpClient (String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key) throws NotFoundException, IOException {
		PrivateKey merchantPrivateKey = getPrivateKey(privateKeyUrl);
		
		//??????verifier
        Verifier verifier = getVerifier(merchantId, merchantSerialNumber, apiV3Key, privateKeyUrl);
		
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
		        .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
		        .withValidator(new WechatPay2Validator(verifier));
		
		httpClient = builder.build();
		
		return httpClient;
	}
	
	/**
	 * ??????httppost
	 * @param url ??????url
	 * @param paramJson ????????????
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
	 * ?????????????????????native??????????????????
	 * @param wechatTradeModel ????????????
	 * @param merchantId ?????????
	 * @param merchantSerialNumber ?????????????????????
	 * @param privateKey ????????????
	 * @param apiV3Key ??????apiv3??????
	 * @param url ??????id+visualDevId?????????????????????????????????????????????????????????apiv3????????????????????????????????????????????????????????????????????????
	 * @return ?????????url
	 * @throws Exception 
	 */
	public String getWeChatPayQRCode(WechatTradeRequestModel wechatTradeModel, String merchantId, String merchantSerialNumber, String privateKey, String apiV3Key, String url) throws Exception{
		String codeUrl = "";
		if(wechatTradeModel != null) {
			//??????????????????
//			wechatTradeModel.setNotify_url(notifyUrl+"/api/visualdev/OnlineDev/public/pay/wechatpay/businessNotify/"+url);
			wechatTradeModel.setNotify_url(paySysConfig.getWechatTradeNotifyUrl()+"/"+url);
			
			//????????????
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
				throw new Exception((retJson.containsKey("message") && !"".equals(retJson.getString("message")))?retJson.getString("message"):"????????????????????????");
			}
		}
		if("".equals(codeUrl)) {
			throw new Exception("????????????????????????");
		}
		ClassPathResource classPathResource = new ClassPathResource(WECHAT_LOGO_URL);
        codeUrl = this.generateQRCode(codeUrl, classPathResource.getInputStream());
		return codeUrl;
	}
	
	/**
	 * ??????????????? ????????????????????? ??????
	 * @param model ????????????
	 * @param appAuthToken ???????????????token
	 * @return
	 * @throws Exception
	 */
	public String getAliPayQRCode(AlipayTradePrecreateModel model, String appAuthToken) throws Exception {
		//??????????????? ??????????????? ????????????
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		
		//??????API?????????request
		AlipayTradePrecreateRequest alipayRequest =  new  AlipayTradePrecreateRequest(); 
	    alipayRequest.setReturnUrl(paySysConfig.getAliReturnUrl());
	    alipayRequest.setNotifyUrl(paySysConfig.getAliNotifyUrl()); //?????????????????????????????????????????????
	    //??????token??????????????????
	    //????????????????????????????????????????????????????????????????????????????????????????????????????????? ???????????? ?????? app_auth_token???
	    //???????????????????????????????????????????????????????????????app_auth_token???????????? ????????????????????????
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
	 * ??????????????????????????????
	 * @param request 
	 * @param merchantId ?????????
	 * @param merchantSerialNumber ?????????????????????
	 * @param privateKeyUrl ???????????????????????????apiclient_key.pem?????????
	 * @param apiV3Key ??????apiv3??????
	 * @return
	 * @throws HttpCodeException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws NotFoundException
	 * @throws ValidationException
	 * @throws ParseException
	 */
	public String wechatNotifyVerfiyAndSign(HttpServletRequest request, String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key) throws HttpCodeException, IOException, GeneralSecurityException, NotFoundException, ValidationException, ParseException {
		//????????????
        String body = getRequestBody(request);
        System.out.println("body="+body);
		//?????????
        String nonce = request.getHeader("Wechatpay-Nonce");
        System.out.println("Wechatpay-Nonce="+nonce);
        //???????????????????????????
        String signature = request.getHeader("Wechatpay-Signature");
        System.out.println("Wechatpay-Signature"+signature);
        //?????????
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        System.out.println("Wechatpay-Timestamp"+timestamp);
        //?????????????????????????????????
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        System.out.println("Wechatpay-Serial"+wechatPaySerial);
        
        //??????verifier
        Verifier verifier = getVerifier(merchantId, merchantSerialNumber, apiV3Key, privateKeyUrl);
		
		// ??????request?????????????????????
		NotificationRequest notificationRequest = new NotificationRequest.Builder().withSerialNumber(wechatPaySerial)
		        .withNonce(nonce)
		        .withTimestamp(timestamp)
		        .withSignature(signature)
		        .withBody(body)
		        .build();
		NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));
		// ????????????????????????
		Notification notification = handler.parse(notificationRequest);
		System.out.println("notificatio="+JSONObject.toJSONString(notification));
		System.out.println("DecryptData="+notification.getDecryptData());
		// ???notification?????????????????????
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
	 * ????????????
	 * @param wechatTradeModel ????????????
	 * @param merchantId ?????????
	 * @param merchantSerialNumber ?????????????????????
	 * @param privateKeyUrl ???????????????????????????apiclient_key.pem?????????
	 * @param apiV3Key ??????apiv3??????
	 * @param url ??????id+visualDevId?????????????????????????????????????????????????????????apiv3????????????????????????????????????????????????????????????????????????
	 * @return
	 * @throws Exception
	 */
	public JSONObject weChatPayRefund(WechatTradeRequestModel wechatTradeModel, String merchantId, String merchantSerialNumber, String privateKeyUrl, String apiV3Key, String url) throws Exception{
		JSONObject retJson = null;
		if(wechatTradeModel != null) {
			//??????????????????
//			wechatTradeModel.setNotify_url(notifyUrl+"/api/visualdev/OnlineDev/public/pay/wechatpay/refundNotify/"+url);
			wechatTradeModel.setNotify_url(paySysConfig.getWechatRefundNotifyUrl()+"/"+url);
			
			//????????????
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
			throw new Exception("?????????????????????");
		}
		return retJson;
	}

	
	/**
	 * ???????????????
	 * @param model ????????????
	 * @param appAuthToken ???????????????token
	 * @return
	 * @throws AlipayApiException
	 */
	public JSONObject aliPayRefund(AlipayTradeRefundModel model, String appAuthToken) throws AlipayApiException {
		//??????????????? ??????????????? ????????????
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		//??????token??????????????????
	    //????????????????????????????????????????????????????????????????????????????????????????????????????????? ???????????? ?????? app_auth_token???
	    //???????????????????????????????????????????????????????????????app_auth_token???????????? ????????????????????????
		request.putOtherTextParam("app_auth_token", appAuthToken);
		request.setBizModel(model);
		
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			JSONObject jsonObject = JSONObject.parseObject(response.getBody());
			JSONObject responseJson = jsonObject.getJSONObject("alipay_trade_refund_response");
			//?????????code=10000???????????????????????????????????????????????????????????????
			//????????????fund_change=Y??????????????????fund_change=N?????????????????????????????????????????????????????????????????????????????????
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
	 * ??????????????????????????????????????? app_auth_token ????????????????????????????????????????????????????????????????????????????????????
	 * ?????????????????????https://opendocs.alipay.com/isv/03l9c0???
	 * @param appAuthCode ???????????????????????????
	 * @return
	 * @throws AlipayApiException
	 */
	public JSONObject getAppAuthTokenInfo(String appAuthCode) throws AlipayApiException {
		
		AlipayRequestModel requestModel = AlipayRequestModel.builder().grant_type("authorization_code").code(appAuthCode).build();
		
		//??????????????? ??????????????? ????????????
		AlipayClient alipayClient = new DefaultAlipayClient(AliConfig.URL, AliConfig.APPID, AliConfig.RSA_PRIVATE_KEY, AliConfig.FORMAT,
		        		AliConfig.CHARSET, AliConfig.ALIPAY_PUBLIC_KEY, AliConfig.SIGNTYPE);
		//??????API?????????request
		AlipayOpenAuthTokenAppRequest  request =  new  AlipayOpenAuthTokenAppRequest ();
		request.setBizContent(JSONObject.toJSONString(requestModel));
		AlipayOpenAuthTokenAppResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println("getAppAuthTokenInfo="+JSONObject.parseObject(response.getBody()));
			//??????????????????
			JSONObject jsonObject = JSONObject.parseObject(response.getBody()).getJSONObject("alipay_open_auth_token_app_response");
			System.out.println("alipay_open_auth_token_app_response="+jsonObject);
			//code=10000???????????????
			if("10000".equals(jsonObject.getString("code"))) {
				return jsonObject;
			}else {
				System.out.println(jsonObject);
			}
		}
		return null;
	}
	
	/**
     * ??????????????????????????????(0-4) 0????????????
     */
    private static final int QRCODE_MARGIN = 2;
    // ???????????????
    private static final int QRCODE_SIZE = 300;
    // LOGO??????
    private static final int WIDTH = 55;
    // LOGO??????
    private static final int HEIGHT = 55;
    
    private static final int black = 0xFF000000;

    private static final int white  = 0xFFFFFFFF;
	
	/**
	 * @Decription: ?????????logo???????????????????????????Base64??????
	 * @param content ?????????????????????
	 * @param logoPath logo????????????
	 * @return ?????????base64????????????????????????(png??????)
	 */
	public String generateQRCode(String content, InputStream inputStream) throws Exception {
	    String binary = "";
	    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
	    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
	    hints.put(EncodeHintType.MARGIN, QRCODE_MARGIN);  //????????????
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
    * @param matrix ?????????????????????
    * @param logoUrl logo??????
    * @return
    * @throws IOException
    */
   public static BufferedImage writeLogoToQrcode(BitMatrix matrix,InputStream inputStream) throws IOException {
       //????????????????????????BufferedImage
       BufferedImage image = toBufferedImage(matrix);
       //???????????????logo??????
       if(inputStream != null && inputStream.available()>0){
           //URL url = new URL(logoUrl);
           //??????????????????????????????
           Graphics2D gs = image.createGraphics();

           int ratioWidth = image.getWidth()*2/10;
           int ratioHeight = image.getHeight()*2/10;
           //??????logo??????
           Image img = ImageIO.read(inputStream);
           int logoWidth = img.getWidth(null)>ratioWidth?ratioWidth:img.getWidth(null);
           int logoHeight = img.getHeight(null)>ratioHeight?ratioHeight:img.getHeight(null);
           //??????logo???????????????
           int x = (image.getWidth() - logoWidth) / 2;
           int y = (image.getHeight() - logoHeight) / 2;
           //??????
           //??????????????????
           //gs.setBackground(Color.WHITE);
           //??????????????????????????????????????????????????????????????????????????????
          // gs.clearRect(x,y,logoWidth,logoHeight);

           //??????????????????????????????????????????????????????
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
