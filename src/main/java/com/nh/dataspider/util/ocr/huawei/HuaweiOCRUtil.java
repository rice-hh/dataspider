package com.nh.dataspider.util.ocr.huawei;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.core.region.Region;
import com.huaweicloud.sdk.ocr.v1.OcrClient;
import com.huaweicloud.sdk.ocr.v1.model.AutoClassificationRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.BankcardRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.BankcardResult;
import com.huaweicloud.sdk.ocr.v1.model.BusinessCardRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.BusinessLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.DriverLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.FlightItineraryRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.GeneralTableRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.GeneralTextRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.GeneralTextResult;
import com.huaweicloud.sdk.ocr.v1.model.HandwritingRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.IdCardRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.IdCardResult;
import com.huaweicloud.sdk.ocr.v1.model.InsurancePolicyRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.LicensePlateRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.MvsInvoiceRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.PassportRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.QualificationCertificateRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.QuotaInvoiceRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeAutoClassificationRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeAutoClassificationResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBankcardRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBankcardResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessCardRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessCardResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeBusinessLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeDriverLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeDriverLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeFlightItineraryRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeFlightItineraryResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeGeneralTableRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeGeneralTableResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeGeneralTextRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeGeneralTextResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeHandwritingRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeHandwritingResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeIdCardRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeIdCardResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeInsurancePolicyRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeInsurancePolicyResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeLicensePlateRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeLicensePlateResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeMvsInvoiceRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeMvsInvoiceResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizePassportRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizePassportResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeQualificationCertificateRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeQualificationCertificateResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeQuotaInvoiceRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeQuotaInvoiceResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTaxiInvoiceRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTaxiInvoiceResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTollInvoiceRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTollInvoiceResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTrainTicketRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTrainTicketResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTransportationLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeTransportationLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVatInvoiceRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVatInvoiceResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVehicleLicenseRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVehicleLicenseResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVinRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeVinResponse;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeWebImageRequest;
import com.huaweicloud.sdk.ocr.v1.model.RecognizeWebImageResponse;
import com.huaweicloud.sdk.ocr.v1.model.TaxiInvoiceRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.TollInvoiceRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.TrainTicketRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.TransportationLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.VatInvoiceRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.VatInvoiceResult;
import com.huaweicloud.sdk.ocr.v1.model.VehicleLicenseRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.VinRequestBody;
import com.huaweicloud.sdk.ocr.v1.model.WebImageRequestBody;
import com.huaweicloud.sdk.ocr.v1.region.OcrRegion;


/**
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司
 * @date 2021/3/16 10:45
 */
@Component
public class HuaweiOCRUtil {
	
	@Autowired
    private HuaweiOCRConfig huaweiOCRConfig;
	
	/**
	 * 初始化认证信息（AK/SK认证：通过AK（Access Key ID）/SK（Secret Access Key)加密调用请求）
	 * 初始化客户端认证信息，使用当前客户端初始化方式可不填 projectId/domainId
	 * AK/SK签名认证方式仅支持消息体大小12M以内，12M以上的请求请使用Token认证。
	 * @param ak 华为云账号Access Key。
	 * @param sk 华为云账号Secret Access Key 。
	 * @return
	 */
	private ICredential getCredential() {
		String ak = huaweiOCRConfig.getAK();
	    return new BasicCredentials().withAk(ak).withSk(huaweiOCRConfig.getSK());
	}

	/**
	 * 初始化指定云服务的客户端 {Service}Client ，以初始化OCR服务的 OcrClient 为例
	 * @param auth
	 * @return
	 */
	private OcrClient getClient(ICredential auth) {
		//服务所在区域，例如：
		//CN_EAST_3 上海一
		Region region = OcrRegion.CN_EAST_3;
        if("cn-north-4".equals(huaweiOCRConfig.getRegionName())) {
        	//CN_NORTH_4 北京四
        	region = OcrRegion.CN_NORTH_4;
        }else if("cn-north-1".equals(huaweiOCRConfig.getRegionName())) {
        	//CN_NORTH_1 北京一
        	region = OcrRegion.CN_NORTH_1;
        }else if("cn-south-1".equals(huaweiOCRConfig.getRegionName())) {
        	//CN_SOUTH_1 华南广州
        	region = OcrRegion.CN_SOUTH_1;
        }else if("la_south_2".equals(huaweiOCRConfig.getRegionName())) {
        	region = OcrRegion.LA_SOUTH_2;
        }else if("af_south_1".equals(huaweiOCRConfig.getRegionName())) {
        	region = OcrRegion.AF_SOUTH_1;
        }else if("ap_southeast_1".equals(huaweiOCRConfig.getRegionName())) {
        	//AP-SOUTHEAST-1 中国-香港
        	region = OcrRegion.AP_SOUTHEAST_1;
        }else if("ap_southeast_2".equals(huaweiOCRConfig.getRegionName())) {
        	region = OcrRegion.AP_SOUTHEAST_2;
        }else if("ap_southeast_3".equals(huaweiOCRConfig.getRegionName())) {
        	region = OcrRegion.AP_SOUTHEAST_3;
        }
	    return OcrClient.newBuilder().withCredential(auth).withRegion(region).build();
	}
	
	/**
	 * 通用文字识别
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public GeneralTextResult generalText(String imgPath) throws Exception {
		GeneralTextResult result = null;
		// create ocrClient
        OcrClient ocrClient = getClient(getCredential());
	    RecognizeGeneralTextRequest recognizeGeneralTextRequest = new RecognizeGeneralTextRequest();
	    GeneralTextRequestBody requestBody = new GeneralTextRequestBody();
	    if (imgPath.indexOf("http://") != -1 || imgPath.indexOf("https://") != -1) {
	    	requestBody.setUrl(imgPath);
		} else {
			byte[] fileData = FileUtils.readFileToByteArray(new File(imgPath));
			String fileBase64Str = Base64.encodeBase64String(fileData);
			requestBody.setImage(fileBase64Str);
		}
	    recognizeGeneralTextRequest.setBody(requestBody);
	    try {
	        RecognizeGeneralTextResponse response = ocrClient.recognizeGeneralText(recognizeGeneralTextRequest);
	        if(response.getHttpStatusCode() == 200) {
	        	result = response.getResult();
	        }
	    } catch (ConnectionException | RequestTimeoutException e) {
	        e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	    return result;
	}
	
	/**
	 * 通用表格识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void generalTable(OcrClient ocrClient, String image) throws Exception {
        RecognizeGeneralTableRequest request = new RecognizeGeneralTableRequest();
        GeneralTableRequestBody requestBody = new GeneralTableRequestBody();
        requestBody.withImage(image);
        request.withBody(requestBody);
        try {
            RecognizeGeneralTableResponse response = ocrClient.recognizeGeneralTable(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	        e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }
	
	/**
	 * 身份证识别
	 * @param ocrClient
	 * @param image
	 * @param side front：身份证人像面；back：身份证国徽面
	 * @throws Exception
	 */
	public IdCardResult idCard(String imgPath, String side) throws Exception {
		IdCardResult result = null;
        // create ocrClient
        OcrClient ocrClient = getClient(getCredential());
		RecognizeIdCardRequest request = new RecognizeIdCardRequest();
	    IdCardRequestBody body = new IdCardRequestBody();
	    body.withReturnVerification(true);
	    body.withSide(side);
	    if (imgPath.indexOf("http://") != -1 || imgPath.indexOf("https://") != -1) {
	    	body.withUrl(imgPath);
		} else {
			byte[] fileData = FileUtils.readFileToByteArray(new File(imgPath));
			String fileBase64Str = Base64.encodeBase64String(fileData);
			body.withImage(fileBase64Str);
		}
	    request.withBody(body);
	    try {
	        RecognizeIdCardResponse response = ocrClient.recognizeIdCard(request);
	        if(response.getHttpStatusCode() == 200) {
	        	result = response.getResult();
	        }
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	    return result;
	}
	
	/**
	 * 银行卡识别
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public BankcardResult bankcard(String imgPath) throws Exception {
		BankcardResult result = null;
		// create ocrClient
        OcrClient ocrClient = getClient(getCredential());
	    RecognizeBankcardRequest recognizeBankcardRequest = new RecognizeBankcardRequest();
	    BankcardRequestBody bankcardRequestBody = new BankcardRequestBody();
	    if (imgPath.indexOf("http://") != -1 || imgPath.indexOf("https://") != -1) {
	    	bankcardRequestBody.setUrl(imgPath);
		} else {
			byte[] fileData = FileUtils.readFileToByteArray(new File(imgPath));
			String fileBase64Str = Base64.encodeBase64String(fileData);
			bankcardRequestBody.setImage(fileBase64Str);
		}
	    recognizeBankcardRequest.setBody(bankcardRequestBody);
	    try {
	        RecognizeBankcardResponse response = ocrClient.recognizeBankcard(recognizeBankcardRequest);
	        result = response.getResult();
	        System.out.println(response);
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	    return result;
	}
	
	/**
	 * 智能分类识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void autoClassification(OcrClient ocrClient, String image) throws Exception {
        RecognizeAutoClassificationRequest request = new RecognizeAutoClassificationRequest();
        AutoClassificationRequestBody requestBody = new AutoClassificationRequestBody();
        requestBody.withImage(image);
        request.setBody(requestBody);
        try {
            RecognizeAutoClassificationResponse response = ocrClient.recognizeAutoClassification(request);
            System.out.println(response);
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

	/**
	 * 增值税发票识别
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public VatInvoiceResult vatInvoice(String imgPath) throws Exception {
		VatInvoiceResult result = null;
		// create ocrClient
        OcrClient ocrClient = getClient(getCredential());
	    RecognizeVatInvoiceRequest request = new RecognizeVatInvoiceRequest();
	    VatInvoiceRequestBody body = new VatInvoiceRequestBody();
	    if (imgPath.indexOf("http://") != -1 || imgPath.indexOf("https://") != -1) {
	    	body.setUrl(imgPath);
		} else {
			byte[] fileData = FileUtils.readFileToByteArray(new File(imgPath));
			String fileBase64Str = Base64.encodeBase64String(fileData);
			body.setImage(fileBase64Str);
		}
	    //默认为false，如果传参为true，则返回更多字段，具体请参见表5。
	    body.setAdvancedMode(true);
	    //默认为false，如果传参为true，则返回text_location字段表示文本框在原图位置。
//	    body.setReturnTextLocation(true);
	    request.withBody(body);
	    try {
	        RecognizeVatInvoiceResponse response = ocrClient.recognizeVatInvoice(request);
	        result = response.getResult();
	        System.out.println(response.toString());
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	    return result;
	}
	
	/**
	 * 定额发票识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void quotaInvoice(OcrClient ocrClient, String image) throws Exception {
        RecognizeQuotaInvoiceRequest request = new RecognizeQuotaInvoiceRequest();
        QuotaInvoiceRequestBody body = new QuotaInvoiceRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeQuotaInvoiceResponse response = ocrClient.recognizeQuotaInvoice(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }
	
	/**
	 * 手写文字识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void handwriting(OcrClient ocrClient, String image) throws Exception {
        RecognizeHandwritingRequest request = new RecognizeHandwritingRequest();
        HandwritingRequestBody body = new HandwritingRequestBody();
        body.withDetectDirection(true);
        body.withQuickMode(true);
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeHandwritingResponse response = ocrClient.recognizeHandwriting(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

	/**
	 * 行驶证识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void vehicleLicense(OcrClient ocrClient, String image) throws Exception {
	    RecognizeVehicleLicenseRequest request = new RecognizeVehicleLicenseRequest();
	    VehicleLicenseRequestBody body = new VehicleLicenseRequestBody();
	    body.withReturnIssuingAuthority(true);
	    body.withImage(image);
	    request.withBody(body);
	    try {
	        RecognizeVehicleLicenseResponse response = ocrClient.recognizeVehicleLicense(request);
	        System.out.println(response.toString());
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	}
	
	/**
	 * 道路运输证识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void transportationLicense(OcrClient ocrClient, String image) throws Exception {
        RecognizeTransportationLicenseRequest request = new RecognizeTransportationLicenseRequest();
        TransportationLicenseRequestBody body = new TransportationLicenseRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeTransportationLicenseResponse response = ocrClient.recognizeTransportationLicense(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }
	
	/**
	 * 出租车发票识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void taxiInvoice(OcrClient ocrClient, String image) throws Exception {
        RecognizeTaxiInvoiceRequest request = new RecognizeTaxiInvoiceRequest();
        TaxiInvoiceRequestBody body = new TaxiInvoiceRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeTaxiInvoiceResponse response = ocrClient.recognizeTaxiInvoice(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }
	
	/**
	 *  车辆通行费发票识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void tollInvoice(OcrClient ocrClient, String image) throws Exception {
        RecognizeTollInvoiceRequest request = new RecognizeTollInvoiceRequest();
        TollInvoiceRequestBody body = new TollInvoiceRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeTollInvoiceResponse response = ocrClient.recognizeTollInvoice(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

	/**
	 * 驾驶证识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void driverLicense(OcrClient ocrClient, String image) throws Exception {
	    RecognizeDriverLicenseRequest request = new RecognizeDriverLicenseRequest();
	    DriverLicenseRequestBody body = new DriverLicenseRequestBody();
	    body.withReturnIssuingAuthority(true);
	    body.withSide("front");
	    body.withImage(image);
	    request.withBody(body);
	    try {
	        RecognizeDriverLicenseResponse response = ocrClient.recognizeDriverLicense(request);
	        System.out.println(response.toString());
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	}

	/**
	 * 营业执照识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void businessLicense(OcrClient ocrClient, String image) throws Exception {
	    RecognizeBusinessLicenseRequest request = new RecognizeBusinessLicenseRequest();
	    BusinessLicenseRequestBody body = new BusinessLicenseRequestBody();
	    body.withImage(image);
	    request.withBody(body);
	    try {
	        RecognizeBusinessLicenseResponse response = ocrClient.recognizeBusinessLicense(request);
	        System.out.println(response.toString());
	    } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
	}

	/**
	 * 保险单识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
	private static void insurancePolicy(OcrClient ocrClient, String image) throws Exception {
        RecognizeInsurancePolicyRequest request = new RecognizeInsurancePolicyRequest();
        InsurancePolicyRequestBody body = new InsurancePolicyRequestBody();
        body.withDetectDirection(true);
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeInsurancePolicyResponse response = ocrClient.recognizeInsurancePolicy(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

	/**
	 * 道路运输从业资格证识别
	 * @param ocrClient
	 * @param image
	 * @throws Exception 
	 */
    private static void qualificationCertificate(OcrClient ocrClient, String image) throws Exception {
        RecognizeQualificationCertificateRequest request = new RecognizeQualificationCertificateRequest();
        QualificationCertificateRequestBody body = new QualificationCertificateRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeQualificationCertificateResponse response = ocrClient.recognizeQualificationCertificate(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 护照识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void passport(OcrClient ocrClient, String image) throws Exception {
        RecognizePassportRequest request = new RecognizePassportRequest();
        PassportRequestBody body = new PassportRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizePassportResponse response = ocrClient.recognizePassport(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * VIN码识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void vin(OcrClient ocrClient, String image) throws Exception {
        RecognizeVinRequest request = new RecognizeVinRequest();
        VinRequestBody body = new VinRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeVinResponse response = ocrClient.recognizeVin(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 火车票识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void trainTicket(OcrClient ocrClient, String image) throws Exception {
        RecognizeTrainTicketRequest request = new RecognizeTrainTicketRequest();
        TrainTicketRequestBody body = new TrainTicketRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeTrainTicketResponse response = ocrClient.recognizeTrainTicket(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 名片识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void businessCard(OcrClient ocrClient, String image) throws Exception {
        RecognizeBusinessCardRequest request = new RecognizeBusinessCardRequest();
        BusinessCardRequestBody body = new BusinessCardRequestBody();
        body.withDetectDirection(true);
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeBusinessCardResponse response = ocrClient.recognizeBusinessCard(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 网络图片识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void webImage(OcrClient ocrClient, String image) throws Exception {
        RecognizeWebImageRequest request = new RecognizeWebImageRequest();
        WebImageRequestBody body = new WebImageRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeWebImageResponse response = ocrClient.recognizeWebImage(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 飞机行程单识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void flightItinerary(OcrClient ocrClient, String image) throws Exception {
        RecognizeFlightItineraryRequest request = new RecognizeFlightItineraryRequest();
        FlightItineraryRequestBody body = new FlightItineraryRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeFlightItineraryResponse response = ocrClient.recognizeFlightItinerary(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 车牌识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void licensePlate(OcrClient ocrClient, String image) throws Exception {
        RecognizeLicensePlateRequest request = new RecognizeLicensePlateRequest();
        LicensePlateRequestBody body = new LicensePlateRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeLicensePlateResponse response = ocrClient.recognizeLicensePlate(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

    /**
     * 机动车销售发票识别
     * @param ocrClient
     * @param image
     * @throws Exception 
     */
    private static void mvsInvoice(OcrClient ocrClient, String image) throws Exception {
        RecognizeMvsInvoiceRequest request = new RecognizeMvsInvoiceRequest();
        MvsInvoiceRequestBody body = new MvsInvoiceRequestBody();
        body.withImage(image);
        request.withBody(body);
        try {
            RecognizeMvsInvoiceResponse response = ocrClient.recognizeMvsInvoice(request);
            System.out.println(response.toString());
        } catch (ConnectionException | RequestTimeoutException e) {
	    	e.printStackTrace();
	    } catch (ServiceResponseException e) {
	    	System.out.println("HttpStatusCode="+String.valueOf(e.getHttpStatusCode())+"；ErrorCode="+e.getErrorCode()+"；ErrorMsg="+e.getErrorMsg());
	    	e.printStackTrace();
	    	throw new Exception(e.getErrorMsg());
	    }
    }

}
