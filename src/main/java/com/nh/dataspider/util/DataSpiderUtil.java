package com.nh.dataspider.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.rtf.RtfWriter2;


public class DataSpiderUtil {

	public static String requestUrl(String blogUrl) throws Exception {
		
		String blogContent = "";
		
		CloseableHttpResponse response = null;
        
        try {
			
        	CloseableHttpClient httpClient = HttpClients.createDefault();
        	
            HttpGet httpGet = new HttpGet(blogUrl);
            
            RequestConfig config = RequestConfig.custom().setConnectTimeout(500000).setSocketTimeout(800000).build();
            
            httpGet.setConfig(config);
            
            response = httpClient.execute(httpGet);
            
            if (response == null) {
                throw new Exception("网页无响应！");
            }

            if (response.getStatusLine().getStatusCode() != 200) {
            	throw new Exception("网页请求失败！");
            }
            
            HttpEntity entity = response.getEntity();
            
            blogContent = EntityUtils.toString(entity, "utf-8");
		} finally {
			if (response != null) {
	            response.close();
	        }
		}
        
        return blogContent;
    }
	
	public static void down(String picUrl, String downloadPath, String fileName) throws Exception {
	    HttpURLConnection connection = null;
	    InputStream inputStream = null;
	    FileOutputStream outStream = null;
	    
	    //路径名加上文件名加上后缀名 = 整个文件下载路径
	    try {
	    	//new一个URL对象
	    	URL url = new URL(picUrl);
	    	//打开链接
	    	connection = (HttpURLConnection)url.openConnection();
	    	//设置请求方式为"GET"
	    	connection.setRequestMethod("GET");
	    	//超时响应时间为500秒
	    	connection.setConnectTimeout(5 * 100000);
	    	//通过输入流获取图片数据
	    	inputStream = connection.getInputStream();
	    	//得到图片的二进制数据，以二进制封装得到数据，具有通用性
	    	byte[] data = readInputStream(inputStream);
	    	//new一个文件对象用来保存图片，默认保存当前工程根目录
	    	File file = new File(downloadPath);
	    	if(!file.exists()) {
	    		file.mkdirs();
	    	}
	    	//创建输出流
	    	outStream = new FileOutputStream(downloadPath+fileName);
	    	//写入数据
	    	outStream.write(data);
		} finally {
			//关闭资源、连接
	        connection.disconnect();
	        inputStream.close();
	    	outStream.close();
		}
	}
	
	private static byte[] readInputStream(InputStream inStream) throws Exception{
    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	//创建一个Buffer字符串
    	byte[] buffer = new byte[1024];
    	//每次读取的字符串长度，如果为-1，代表全部读取完毕
    	int len = 0;
    	//使用一个输入流从buffer里把数据读取出来
    	while( (len=inStream.read(buffer)) != -1 ){
	    	//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
	    	outStream.write(buffer, 0, len);
    	}
    	//关闭输入流
    	inStream.close();
    	//把outStream里的数据写入内存
    	return outStream.toByteArray();
	}
	
	public static void write(String content, int c, String downloadPath, String fileName) throws Exception {
		
		File file = new File(downloadPath);
    	if(!file.exists()) {
    		file.mkdirs();
    	}
		
		Document doc = new Document();
		
		RtfWriter2.getInstance(doc,new FileOutputStream(downloadPath+fileName,true));
		
		doc.open();
		
		Paragraph pContent = new Paragraph(content);
		Color color = new Color(c);
		Font font = new Font();
		font.setColor(color.getRed(), color.getGreen(), color.getBlue());
		pContent.setFont(font);
		
		doc.add(pContent);
		doc.close();
	}
	
	public static void write(String content, String downloadPath, String fileName) throws Exception {
    	File file = new File(downloadPath);
    	if(!file.exists()) {
    		file.mkdirs();
    	}
		FileOutputStream fos = new FileOutputStream(downloadPath+fileName,true);
		fos.write(content.getBytes());  
		fos.close();
	}
	
	public static String getConnectionText(String url) {
        String jsonText = null;
        Connection connection = Jsoup.connect(url).timeout(3000).ignoreContentType(true);
        Map<String, String> heads = new HashMap<>();
        heads.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        heads.put("Accept-Encoding", "gzip, deflate, br");
        heads.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8");
        heads.put("Cache-Control", "max-age=0");
        heads.put("Connection", "keep-alive");

        //TODO 请在这里填写自己的cookie,没有cookid将会请求不到
//        heads.put("Cookie", "填写自己的cookie");
        heads.put("Cookie", "bsource=search_baidu; _uuid=EC976535-3448-EB59-CD2F-A09535DD302834843infoc; buvid3=9E095F6B-E8E1-47E6-AF41-02895D431BBC143104infoc; CURRENT_FNVAL=80; blackside_state=1; sid=6jqm6k9i; rpdid=|(u~||uJY)||0J'uY|R~~~mm); PVID=4; bfe_id=fdfaf33a01b88dd4692ca80f00c2de7f");

        heads.put("Host", "api.bilibili.com");
        heads.put("Sec-Fetch-Mode", "navigate");
        heads.put("Sec-Fetch-Site", "none");
        heads.put("Sec-Fetch-User", "?1");
        heads.put("Upgrade-Insecure-Requests", "1");
        heads.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        connection.headers(heads);
        try {
            jsonText = connection.get().text();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("建立获取cid连接失败");
        }
        return jsonText;
    }
	
	/**
	 * 
	 * @param downPath
	 * @param name
	 * @param xmlPath
	 * @return ass的完整路径（路径+ass文件名）
	 * @throws Exception
	 */
	public static String transXmlToAss (String downPath, String xmlPath) throws Exception {
		System.out.println("--------------开始将xml转换成ass--------------");
        String danmaku2assPath = "D:\\soft\\danmaku2ass\\danmaku2ass";
        List<String> commend = new ArrayList<>();
        commend.add(danmaku2assPath);
        commend.add(xmlPath);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        builder.inheritIO().start().waitFor();
        System.out.println("--------------xml转换成ass完成--------------");
        
        String fullAssPath = xmlPath.replace(".xml", ".ass");
        return fullAssPath;
	}
	
	public static void mergeVideoAndAss(String downPath, String name, String videoUrl, String assUrl) throws Exception {
        System.out.println("--------------开始合并弹幕视频--------------");
        String ffmpegPath = "D:\\soft\\ffmpeg\\bin\\ffmpeg";
        String outFile = downPath + name + "-弹幕版.mp4";
        List<String> commend = new ArrayList<>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoUrl);
        commend.add("-vf");
        commend.add("subtitles='"+assUrl.substring(0,1)+"\\"+assUrl.substring(1, assUrl.length())+"'");
        commend.add("-vcodec");
        commend.add("libx264");
        commend.add(outFile);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        builder.inheritIO().start().waitFor();
        System.out.println("--------------弹幕视频合并完成--------------");
        
        deleteFile(assUrl);
    }
	
	/**
	 * 合并音频和视频
	 * @param downPath 合并后的文件路径
	 * @param name 合并后的文件名
	 * @param videoUrl 合并前的视频完整路径
	 * @param audioUrl 合并前的音频完整路径
	 * @return 合并后视频的完整路径
	 * @throws Exception
	 */
	public static String mergeVidoeAndAudio(String downPath, String name, String videoUrl, String audioUrl) throws Exception {
        System.out.println("--------------开始合并音视频--------------");
        String ffmpegPath = "D:\\soft\\ffmpeg\\bin\\ffmpeg";
        String outFile = downPath + name + ".mp4";
        List<String> commend = new ArrayList<>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoUrl);
        commend.add("-i");
        commend.add(audioUrl);
        commend.add("-vcodec");
        commend.add("copy");
        commend.add("-acodec");
        commend.add("copy");
        commend.add(outFile);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        builder.inheritIO().start().waitFor();
        System.out.println("--------------音视频合并完成--------------");
        
        deleteFile(videoUrl);
        
        return outFile;
    }
	
	/**
	 * 下载弹幕ass版本的文件
	 * @param downloadPath 下载路径
	 * @param fileName 文件名
	 * @param url 下载弹幕的网络地址
	 * @return 返回ass下载后的完整路径（路径+ass文件名）
	 * @throws Exception
	 */
	public static String downDMAss (String downloadPath, String fileName, String url) throws Exception {
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 write(retStr, downloadPath, fileName+".xml");
		 
		 String assPath = transXmlToAss(downloadPath, downloadPath+fileName+".xml");
		 
		 deleteFile(downloadPath + fileName+ ".xml");
		 
		 return assPath;
	}
	
	private static void deleteFile (String fullPath) {
		
		File tempFile = new File(fullPath);
		 if(tempFile.isFile() && tempFile.exists()) {
			 tempFile.delete();
		 }
	}
	
	public static boolean checkFileExistByExcel(String id, String lrcName, String excelFilePath) {
		boolean exist = true;
		try {
			Workbook workbook = FileUtil.getUploadExcelWorkbook(excelFilePath);
			FormulaEvaluator evaluator=workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(0);
			
			int rows = sheet.getLastRowNum();
			if(rows>0) {
				for(int i=1; i<=rows; i++) {
					Row row = sheet.getRow(i);
					if(row != null) {
						Cell cell = row.getCell(0);
						String excelId = FileUtil.readCellValue(cell, evaluator);
						if(excelId.equals(id)) {
							cell = row.getCell(1);
							String excelPath = FileUtil.readCellValue(cell, evaluator);
							if(FileUtil.fileIsExists(excelPath+"\\"+lrcName+".lrc")) {
								exist = true;
							}else {
								exist = false;
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return exist;
	}
}
