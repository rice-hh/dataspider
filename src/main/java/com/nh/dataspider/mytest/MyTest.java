package com.nh.dataspider.mytest;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.nh.dataspider.audio.ConvertingAnyAudioToMp3_Example1;
import com.nh.dataspider.pay.model.AlipayResponseModel;
import com.nh.dataspider.pay.model.AlipayResponseModel.PassbackParams;
import com.nh.dataspider.util.Base64Util;
import com.nh.dataspider.util.DateUtil;
import com.nh.dataspider.util.FileUtil;
import com.nh.dataspider.util.NumberUtil;

import cn.hutool.json.JSONArray;
import lombok.Data;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

/**
 * @author ：nh
 * @date ：2021/6/18
 * @description：
 */
public class MyTest {
	
	public static void main(String[] args) {
		String filePath = "C:\\Users\\CES\\Downloads\\test";
		String transPath = "C:\\Users\\CES\\Downloads\\test\\trans";
		String album = "危险人格 第二季";
		String titlePrefix = "江湖那么大";
		String albumArtist = "晋江文学城 木瓜黄原著，猫耳FM出品，玉苍红独家制作，广播剧《危险人格》";
		String artist = "景向谁依&倒霉死勒";
		String comment = "我相信你。解临你记住，不管发生什么，我永远相信你。";
		String coverType = "jpg";
		reNameTitle(filePath, titlePrefix);
//		reName(filePath); 
//		modifyProperty(filePath, album, albumArtist, artist, comment, coverType);
//		fillProperty(filePath, transPath, album, titlePrefix, albumArtist, artist, comment);
//		reSetProperty(filePath, album, titlePrefix, albumArtist, artist, comment);
		
//		try {
//			testOut();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("33", 33);
//		m.put("22", 22);
//		System.out.println(JSONObject.toJSONString(m));
		
//		List list = Arrays.asList("123", "1234", "12345", "123456", "1234567", "122222223", "123", "1234", "2422"); 
//		
//		Map<String, Long> collect = (Map<String, Long>) list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); 
//		System.out.println(collect);
//		
//		List<String> excelL = new ArrayList<String>();
//		excelL.add("1");
//		excelL.add("1");
//		excelL.add("2");
//		excelL = excelL.stream().distinct().collect(Collectors.toList());
//		excelL.remove("2");
//		System.out.println(excelL);
//		
//		String s = ",";
//		System.out.println(s.split(",").length);
//		s = "";
//		System.out.println(s.split(",").length);
//		s = "1,";
//		System.out.println(s.split(",").length);
//		s = ",1";
//		System.out.println(s.split(",").length);
//		s = "【aa,dd】【cc,dd】";
//		String[] split = s.split("】");
//		System.out.println(split);
		
//		String s = "1#2#3";
//		System.out.println(s.split("#").length);
//		System.out.println(s.split("#")[0]);
//		System.out.println(s.split("#")[1]);
//		s = "#2#3";
//		System.out.println(s.split("#").length);
//		System.out.println(s.split("#")[0]);
//		System.out.println(s.split("#")[1]);
		
//		 String regex = "^[a-zA-Z0-9]{18}$";
//		 String leave = "11111111111111111q";
//		 System.out.println(Pattern.matches(regex,leave));
		 
//		 copy("G:\\nh\\mymissevandown\\2021\\1013\\六爻第一季", "G:\\nh\\mymissevandown\\2021\\1013\\六爻\\第1季");
		
//		String[] sts = new String[3];
//		sts[0] = "0";
//		sts[1] = "1";
//		sts[2] = "2";
//		String s = "['2','3']";
//		List<String> parseArray = JSONObject.parseArray(s, String.class);
//		System.out.println(parseArray);
//		String s1 = "[['2','3'],['4','5']]";
//		System.out.println(s1);
//		List<String> parseArray1 = JSONObject.parseArray(s1, String.class);
//		System.out.println(parseArray1);
//		System.out.println(sts.toString());
//		String string = ArrayUtils.toString(sts);
//		StringBuffer str5 = new StringBuffer();
//		str5.append("[");
//		for (String s : sts) {
//		    str5.append(s);
//		}
//		JSONObject j = new JSONObject();
//		j.put("1", sts);
//		System.out.println(j.toJSONString());
//		System.out.println(JSONObject.toJSONString(sts));
//		str5.append("]");
//		System.out.println(str5);
//		String[][] s = new String[1][];
//		s[0] = sts;
//		System.out.println(string);
//		String ss = "2";
//		List<String> formValueL = Arrays.asList(ss.split(","));
//		System.out.println(formValueL);
//		long l = 1631548800000l;
//		String d = "10";
//		System.out.println("d="+NumberUtil.checkNumeric(d));
//		String d1 = "1.1";
//		System.out.println("d1="+NumberUtil.checkNumeric(d1));
//		String d2 = "0.1";
//		System.out.println("d2="+NumberUtil.checkNumeric(d2));
//		String d3 = "1q";
//		System.out.println("d3="+NumberUtil.checkNumeric(d3));
//		String d4 = "aa";
//		System.out.println("d4="+NumberUtil.checkNumeric(d4));
		
//		System.out.println(10/4);
//		System.out.println(10%4);
//		tesrtArray();
//		String s = null;
//		System.out.println(String.valueOf(s));
//		System.out.println(s.toString());
		
//		String s = "22";
//		String s1 = "";
//		System.out.println(s.indexOf(s1));
//		System.out.println(s1.indexOf(s));
		
//		List<String> s = Lists.newArrayList();
//		s.add("1");
//		s.add("2");
//		s.add("11");
//		s.add("22");
//		System.out.println(s.indexOf("11"));
		
//		DecimalFormat df = new DecimalFormat("0.00");
//
//		String str = df.format(1).toString();
//		System.out.println(str);
//		System.out.println(Float.parseFloat(str));
//		System.out.println(Convert.toDouble(str));
		
//		String[] arr = new String[2];
//		System.out.println(Arrays.toString(arr));
		
//		String s = "[选项一, 选项二, 选项三]";
//		List<String> parseArray = JSONObject.parseArray(s, String.class);
//		System.out.println(parseArray);
//		s="100";
//		parseArray = JSONObject.parseArray(s, String.class);
//		System.out.println(parseArray);
		
//		String s = "[\"2\",\"3\"]";
//		String sql = "select replace(json_array('"+s+"'),'\"','') from dual;";
//		System.out.println(sql);
		
//		String s1 ="[[\"2\",\"3\"],[\"2\",\"3\"]]";
//		String s = "";
//		List<String> ss = JSONObject.parseArray(s1, String.class);
//		for(String va : ss) {
//			s += "'"+va.replaceAll("\"", "")+"',";
//		}
//		System.out.println("json_array("+s+")");
		
//		JSONArray jsonArr = new JSONArray();
//		JSONObject json = new JSONObject();
//		json.put("1", 1);
//		jsonArr.add(json);
//		json = new JSONObject();
//		json.put("2", 2);
//		jsonArr.add(json);
//		System.out.println(jsonArr);
//		
//		String[] value = new String[0];
//		System.out.println(JSONObject.toJSONString(value));
//		String[] values = new String[1];
//		System.out.println(JSONObject.toJSONString(values));
		
//		String sql ="),'%') and";
//		if(sql.lastIndexOf("and")>0) {
//			sql = sql.substring(0, sql.lastIndexOf("and"));
//		}
//		System.out.println(sql);
		
//		String[][] arr = new String[2][];
//		String[] a = new String[2];
//		a[0] = "1";
//		a[1] = "2";
//		arr[0] = a;
//		a = new String[2];
//		a[0] = "3";
//		a[1] = "4";
//		arr[1] = a;
//		String fieldValue = JSONObject.toJSONString(arr);
//		
//		String sql = "";
//		if(isArrayString(fieldValue)) {
//			List<String> formValueL = JSONObject.parseArray(fieldValue, String.class);
//			if(formValueL != null && formValueL.size()>0) {
//				for(String v : formValueL) {
//					if(isArrayString(v)) {
//						List<String> vL = JSONObject.parseArray(v, String.class);
//						if(vL != null && vL.size()>0) {
//							sql += " json_extract(f_data, '$.cascaderField140') like concat('%',json_array(";
//							for(String va : vL) {
//								sql += "'"+va+"',";
//							}
//							if(sql.lastIndexOf(",")>0) {
//								sql = sql.substring(0, sql.length()-1);
//							}
//							sql += "),'%') and";
//						}
//					}
//				}
//				if(sql.lastIndexOf("and")>0) {
//					sql = sql.substring(0, sql.lastIndexOf("and"));
//					sql += " and ((length(json_extract(f_data, '$.cascaderField140'))-length(replace(json_extract(f_data, '$.cascaderField140'),'],','')))/2)="+(formValueL.size()-1);
//				}
//			}
//		}
//		System.out.println(sql);
		
//		List<String> l1 = Lists.newArrayList();
//		l1.add("1");
//		l1.add("11");
//		l1.add("2");
//		l1.add("22");
//		List<String> l2 = Lists.newArrayList();
//		l2.add("1");
//		l2.add("11");
//		l2.add("222");
////		l2.add("22");
//		System.out.println(l1.containsAll(l2));
		
		//设置线程名字
//        Thread.currentThread().setName("main thread");
//		//创建线程
//        MyThread myThread = new MyThread();
//        myThread.setName("子线程:");
//        //开启线程
//        myThread.start();
//        for(int i = 0;i<5;i++){
//            System.out.println(Thread.currentThread().getName() + i);
//        }
//        
//        String str = "'2021-12-30','2022-1-4',['2022-1-3','2022-1-4'],['2022-1-1']";
//        List<String> list = funSplit(str, "[");
//        for(String s : list) {
//        	List<String> parseArray = JSONObject.parseArray(s, String.class);
//        	System.out.println(s);
//        	str = str.replace(s, "");
//        	System.out.println(str);
//        }
        
//		String startDate = "2022-1-4";
//		System.out.println(DateUtil.fillDateWithZero(startDate));
//		startDate = "2022-1-04";
//		System.out.println(DateUtil.fillDateWithZero(startDate));
//		startDate = "2022/1/04";
//		String startDateT = startDate.replaceAll("-", "");
//		if(startDate.length()-startDateT.length()==2) {
//			System.out.println(DateUtil.fillDateWithZero(startDate));
//		}
//		System.out.println(DateUtil.fillDateWithZero(startDate));
		
		System.out.println(11111);
		new Thread((new Runnable() {
    		@Override
    		public void run() {
    			System.out.println(22222);
    		}
    	})).start();
		System.out.println(33333);
		
		List<Integer> l1 = Lists.newArrayList();
		List<Integer> l2 = Lists.newArrayList();
		l1.add(1);
		l1.add(2);
		l1.add(3);
		l1.add(4);
		l2.add(2);
		l2.add(4);
		l2.add(1);
//		l2.addAll(l1);
//		l2 = l2.stream().distinct().collect(Collectors.toList());
//		System.out.println(l2);
		System.out.println(l1.containsAll(l2));
		
		Integer[] i= {1,3};
		System.out.println(Arrays.toString(i));
		
		JSONObject resultJosn = new JSONObject();
		resultJosn.put("suite_access_token", "puQ9H8xdQiz_LtWOi2bE43YNp4Z3qg-0Y_Lq9ZF5dp9DkvoaSG_WY7gY_PTnDVZnofFhjGQ4sxf2Bal9bpO7ZXFpSv18xKN18PmZ7EJyLQNQeyffsitvMpuzpBb7fW_O");
		resultJosn.put("expires_in", "7200");
		if(resultJosn.containsKey("errcode") && !"0".equals(resultJosn.getString("errcode"))) {
			throw new RuntimeException("第三方应用凭证获取失败！" + resultJosn.getString("errmsg"));
		}
		
		System.out.println("d="+NumberUtil.checkNumeric("2022-03-05"));
		
		String startDate = "2022-05-01";
		String endDate = "2022-05-10";
		String holidays = "['2022-05-01','2022-05-02','2022-05-03','2022-05-16']";
		String workdays = "['2022-05-07','2022-05-14']";
		List<String> holidayL = JSONObject.parseArray(holidays, String.class);
		List<String> workDayL = JSONObject.parseArray(workdays, String.class);
		System.out.println(caculateWorkDay(startDate, endDate, holidayL, workDayL));
		
		JSONArray array = new JSONArray();
		JSONObject josn = new JSONObject();
		josn.put("suite_access_token", "puQ9H8xdQiz_LtWOi2bE43YNp4Z3qg-0Y_Lq9ZF5dp9DkvoaSG_WY7gY_PTnDVZnofFhjGQ4sxf2Bal9bpO7ZXFpSv18xKN18PmZ7EJyLQNQeyffsitvMpuzpBb7fW_O");
		josn.put("expires_in", "7200");
		array.add(josn);
		josn = new JSONObject();
		josn.put("suite_access_token", "puQ9H8xdQiz_LtWOi2bE43YNp4Z3qg-0Y_Lq9ZF5dp9DkvoaSG_WY7gY_PTnDVZnofFhjGQ4sxf2Bal9bpO7ZXFpSv18xKN18PmZ7EJyLQNQeyffsitvMpuzpBb7fW_O");
		josn.put("expires_in", "7200");
		array.add(josn);
		System.out.println(array.toString());
		System.out.println(isArrayString(array.toString()));
		
		Double d = new Double(Math.floor(1.1));
		System.out.println(DateUtil.dateAddDays(new Date(), d.intValue()));
		
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 "));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 33"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 3:2:2"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 3:2:02"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 3:2:76"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-6-07 3:2:02:77"));
//		System.out.println(DateUtil.fillDateTimeWithZero("2022-06-07 13:12:02"));
		
		System.out.println(testDateAdd("2022-06-07 13:12:02,+13H,1"));
		
		
		
		List<ProperCond> ll1 = Lists.newArrayList();
		List<ProperCond> ll2 = Lists.newArrayList();
		
		ProperCond p = new ProperCond();
		p.setField("f1");
		ll1.add(p);
		p = new ProperCond();
		p.setField("f2");
		ll1.add(p);
		
		OrderConfig o = new OrderConfig();
		o.setOrderField(ll1);
		
		ll2.addAll(o.getOrderField());
		
		List<ProperCond> ll4 = Lists.newArrayList();
		org.springframework.util.CollectionUtils.mergeArrayIntoCollection(new Object[ll1.size()], ll4);
		Collections.copy(ll4, ll1);
		System.out.println(ll4.toString());
		
		List<ProperCond> ll3 = ll1.stream().collect(Collectors.toList());
		System.out.println(ll3.toString());
		
		List<ProperCond> dtoList = ll1.stream()
		        .map(e -> {
		        	ProperCond pr = new ProperCond();
		            BeanUtils.copyProperties(e, pr);
		            return pr;
		        })
		        .collect(Collectors.toList());
		System.out.println(dtoList.toString());
		
		for(ProperCond pp : ll2) {
			pp.setField("kk");
		}
		System.out.println(o.getOrderField().toString());
		System.out.println(ll2.toString());
		System.out.println(ll3.toString());
		System.out.println(ll4.toString());
		System.out.println(dtoList.toString());
		
		LinkedList<String> orderL = Lists.newLinkedList();
		orderL.add(0, "0");
		orderL.add(1, "1");
		System.out.println(orderL.toString());
		orderL.add(0, "2");
		System.out.println(orderL.toString());
		
		List<TestOrder> dpL = Lists.newArrayList();
		TestOrder dp1 = new TestOrder("dp1", "a2");
		dpL.add(dp1);
		TestOrder dp2 = new TestOrder("dp2", "a1");
		dpL.add(dp2);
		TestOrder dp3 = new TestOrder("dp3", "w1");
		dpL.add(dp3);
		
		List<TestOrder> auToL = Lists.newArrayList();
		TestOrder a1 = new TestOrder("a1", "w1");
		auToL.add(a1);
		TestOrder a2 = new TestOrder("a2", "df");
		auToL.add(a2);
		TestOrder a3 = new TestOrder("a3", "dp1");
		auToL.add(a3);
		
		LinkedList<TestOrder> retOrderL = Lists.newLinkedList();
		testOrder(retOrderL, dpL, auToL);
		System.out.println(retOrderL.toString());
		System.out.println(JSONObject.toJSONString(retOrderL));
		
		System.out.println(1000%100);
		
		writeObject();
		
		System.out.println(DateUtil.getDateByRFC3339("2022-08-05T13:14:52+08:00").getTime());
		System.out.println(DateUtil.stringToDate("2022-02-02 02:02:02").getTime());
		
		
		String s = "{\"gmt_create\":\"2022-08-10 15:18:27\",\"charset\":\"UTF-8\",\"seller_email\":\"qianwanxiang2022@126.com\",\"subject\":\"20220810001\",\"sign\":\"dAWjE3tVEcPDe+Z7VEOd2UyCycEEkkEAxL9vGKdwCIeuzf2k20tajtYyGUJBQ6U/nQD61lnS5JhcWx6UMhEMTH2KAa7zI1tmGqn37ZryWhi2DoL5qUvZ4cRhOySZFjk4TOdCHXTEsX4UPrTcFXMxvkvoExIoGHZe2THR2s7gSlSXDgkyvBghBuN32cQs+faSFMW9wSSX7z/MVazHv849RiHnAdeOZZiQ0wfwgByRqP0v7YAPJRMRz6yw0WIqlhpm37/YyXXJB7/Qo0l6YPuTn1tHB4QKxhpYpPvqswDZav52IK+Wji/b+CV79nKNgiJSPd/H+VO9Vby+cNjpkWLAww==\",\"buyer_id\":\"2088702613726752\",\"invoice_amount\":\"0.01\",\"notify_id\":\"2022081000222151836026751445616262\",\"fund_bill_list\":\"[{\\\"amount\\\":\\\"0.01\\\",\\\"fundChannel\\\":\\\"PCREDIT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"0.01\",\"buyer_pay_amount\":\"0.01\",\"app_id\":\"2021003141610273\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088441761858042\",\"gmt_payment\":\"2022-08-10 15:18:35\",\"notify_time\":\"2022-08-10 15:18:36\",\"passback_params\":\"{\\\"tenantId\\\":\\\"a7960755ade243bdad38b95d43aa8312\\\",\\\"userId\\\":\\\"admin\\\",\\\"visualDataId\\\":\\\"9696a61fd6f14d3b88f223ccc9f6f6d7\\\",\\\"visualDevId\\\":\\\"31c4aeb5393f4de3b3d724a8cae57dac\\\"}\",\"version\":\"1.0\",\"out_trade_no\":\"20220810001\",\"total_amount\":\"0.01\",\"trade_no\":\"2022081022001426751434711992\",\"auth_app_id\":\"2021003140680839\",\"buyer_logon_id\":\"124***@qq.com\",\"point_amount\":\"0.00\"}";
		AlipayResponseModel responseModel = JSONObject.parseObject(s, AlipayResponseModel.class);
		System.out.println("responseModel="+JSONObject.toJSONString(responseModel));
		PassbackParams passbackParams = JSONObject.parseObject(responseModel.getPassbackParams(), PassbackParams.class);
		System.out.println("passbackParams="+JSONObject.toJSONString(passbackParams));
		JSONObject requestParamsJson = JSONObject.parseObject(responseModel.getPassbackParams()) ;
		System.out.println("passbackParams="+JSONObject.toJSONString(requestParamsJson));
		System.out.println(1%100+"");
		System.out.println(1/100);
		double total_amount = new BigDecimal(1).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("total_amount="+total_amount);
		
		System.out.println("checkNumeric="+NumberUtil.checkNumeric("01"));
		
		List<String> ls1 = Lists.newArrayList();
		ls1.add("1");
		ls1.add("2");
		ls1.add("3");
		ls1.add("4");
		System.out.println("listToString="+listToString(ls1));
	}
	
	public static String listToString(List<String> strL) {
    	StringBuffer sb = new StringBuffer();
    	if(strL != null && strL.size()>0) {
            for (int i = 0; i < strL.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(strL.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(strL.get(i)).append("'");
                }
            }
        }
        return sb.toString();
    }
	
	public static void writeObject() {
		TestOrder u = new TestOrder("9龙", "23");
		final String s = "1";
		String jsonString = JSONObject.toJSONString(u);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream("F:\\Resources\\test\\test.txt"));
			//将对象序列化到文件s
			oos.writeObject(jsonString);
			
			oos = new ObjectOutputStream(new FileOutputStream("F:\\Resources\\test\\text.txt"));
			//将对象序列化到文件s
			oos.writeObject(u);
			TestOrder u1 = new TestOrder("9龙", "22");
			oos.writeObject(u);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static LinkedList<TestOrder> testOrder(LinkedList<TestOrder> retOrderL, List<TestOrder> dpL, List<TestOrder> auToL) {
		int maxIndex = 0;
    	if(dpL != null && dpL.size()>0) {
    		for(TestOrder ch : dpL) {
    			retOrderL.add(maxIndex, ch);
//    			List<String> valueSourceL = dpL.stream().map(TestOrder::getLinkField).collect(Collectors.toList());
    			String linkField = ch.getLinkField();
				//筛选出需要提前处理好数据的自动化新增节点
				List<TestOrder> toPrepareAutoNodeL = auToL.stream().filter(l -> linkField.equals(l.getName())).collect(Collectors.toList());
				if(toPrepareAutoNodeL != null && toPrepareAutoNodeL.size()>0) {
					//如果存在，则这些auto节点的执行顺序要在该数据处理节点之前
					for(TestOrder chI : toPrepareAutoNodeL) {
						if(!retOrderL.contains(chI)) {
							retOrderL.add(maxIndex, chI);
							maxIndex++;
							auToL.remove(chI);
						}
					}
					maxIndex = retOrderL.size();
//					retOrderL.add(maxIndex, ch);
				}else {
					maxIndex++;
				}
    		}
    	}
    	if(auToL != null && auToL.size()>0) {
    		for(TestOrder ch : auToL) {
    			String linkField = ch.getLinkField();
    			List<TestOrder> toPrepareAutoNodeL = dpL.stream().filter(l -> linkField.equals(l.getName())).collect(Collectors.toList());
    			if(toPrepareAutoNodeL != null && toPrepareAutoNodeL.size()>0) {
    				for(TestOrder chI : toPrepareAutoNodeL) {
    					if(retOrderL.contains(chI)) {
    						retOrderL.add(maxIndex, ch);
    						continue;
    					}else {
    						retOrderL.add(maxIndex, ch);
    						maxIndex++;
    					}
    				}
    			}
    		}
    	}
		return retOrderL;
	}
	
	@Data
    public static class OrderConfig{
    	/**
    	 * 排序的字段
    	 */
    	private List<ProperCond> orderField;
    	
    }
	
	@Data
    public static class TestOrder implements Serializable{
		private String name;
    	private String linkField;
    	private String linkFields;
    	
    	public TestOrder() {
    		
    	}
    	public TestOrder(String name, String f) {
    		this.name = name;
    		this.linkField = f;
    	}
    	public TestOrder(String name, String f, String fs) {
    		this.name = name;
    		this.linkField = f;
    		this.linkFields = fs;
    	}
    }
	
	private static String testDateAdd(String str) {
		String retStr ="";
		str = str.replaceAll("##", "").replaceAll("null", "");
		String[] strs = str.split(",");
		if(strs.length>1) {
			int outType = 1;
			if(strs.length>2) {
				outType = Integer.parseInt(strs[2]);
			}
			String startDate = DateUtil.fillDateTimeWithZero(strs[0]);
			String addNum = strs[1];
			
			String addType = addNum.substring(addNum.length()-1, addNum.length());
			int num = 0;
			String numStr = addNum.substring(1, addNum.length()-1);
			if(NumberUtil.checkNumeric(numStr)) {
				Double d = new Double(Math.floor(Double.parseDouble(numStr)));
				num = d.intValue();
			}
			if("-".equals(addNum.substring(0, 1))) {
				num = 0-num;
			}
			Date date = DateUtil.stringToDate(startDate);
			if("y".equals(addType)) {
				date = DateUtil.dateAddYears(date, num);
			}else if("M".equals(addType)) {
				date = DateUtil.dateAddMonths(date, num);
			}else if("d".equals(addType)) {
				date = DateUtil.dateAddDays(date, num);
			}else if("H".equals(addType)) {
				date = DateUtil.dateAddHours(date, num);
			}else if("m".equals(addType)) {
				date = DateUtil.dateAddMinutes(date, num);
			}
			
			if(outType == 1) {
				retStr = DateUtil.daFormat(date);
			}else {
				retStr = DateUtil.dateFormat(date);
			}
		}
		return retStr;
	}
	
	private static String caculateWorkDay(String startDate, String endDate, List<String> holidayL, List<String> workDayL) {
		//获取休息日
		List<String> weekDays = DateUtil.getWeekDays(startDate, endDate);
		//获取工作日
		List<String> workDays = DateUtil.getWorkDays(startDate, endDate);
		
		for(String date : holidayL) {
			if(DateUtil.isEffectiveDate(DateUtil.stringToDates(date), DateUtil.stringToDates(startDate), DateUtil.stringToDates(endDate))) {
				workDays.remove(date);
				if(!weekDays.contains(date)) {
					weekDays.add(date);
				}
			}
		}
		for(String date : workDayL) {
			if(DateUtil.isEffectiveDate(DateUtil.stringToDates(date), DateUtil.stringToDates(startDate), DateUtil.stringToDates(endDate))) {
				weekDays.remove(date);
				if(!workDays.contains(date)) {
					workDays.add(date);
				}
			}
		}
		startDate =DateUtil.daFormat(DateUtil.dateAddDays(DateUtil.stringToDates(endDate), 1));
		String newEndDate = DateUtil.daFormat(DateUtil.dateAddDays(DateUtil.stringToDates(endDate), weekDays.size()));
		
		//获取休息日
		List<String> weekDaysT = DateUtil.getWeekDays(startDate, newEndDate);
		List<String> workDaysT = DateUtil.getWorkDays(startDate, newEndDate);
		
		if(weekDaysT.size()>0 || !Collections.disjoint(workDaysT, holidayL)) {
			return caculateWorkDay(startDate, newEndDate, holidayL, workDayL);
		}else {
			return newEndDate;
		}
	}
	
	private static List<String> funSplit(String functionStr, String symbol){
        List<String> list = new ArrayList<>();
        int startFlag = 0;
        int endFlag = 0;
        int subStart = 0;
        int len = functionStr.length();
        String rightCharacter="";
        if(symbol.equals("(")) {
        	rightCharacter = ")";
        } else if(symbol.equals("{")) {
        	rightCharacter = "}";
        } else if(symbol.equals("[")) {
        	rightCharacter = "]";
        }
        for (int i = 0; i < len; i++) {
            if (symbol.equals(functionStr.charAt(i)+"")) {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    subStart = i;
                }
            } else if (rightCharacter.equals(functionStr.charAt(i)+"")) {
                endFlag++;
                if (endFlag == startFlag) {
                    list.add(functionStr.substring(subStart, i + 1));
                }
            }
        }
        return list;
    }
	
	public static boolean isArrayString(String str) {
    	try {
    		List<String> parseArray = JSONObject.parseArray(str, String.class);
    		return true;
		} catch (Exception e) {
			return false;
		}
    }
	
	public static boolean isArray(Object object)
    {
        return isNotNull(object) && object.getClass().isArray();
    }
	
	public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }
	
	public static boolean isNull(Object object)
    {
        return object == null;
    }
	
	private static String fileToBase64(String filePath) {
        return Base64Util.fileToBase64(filePath);
    }
	
	public static void tesrtArray() {
		String[] sts = new String[10];
		sts[0] = "0";
		sts[1] = "1";
		sts[2] = "2";
		sts[3] = "3";
		sts[4] = "4";
		sts[5] = "5";
		sts[6] = "6";
		sts[7] = "7";
		sts[8] = "8";
		sts[9] = "9";
		
		int length = sts.length%4>0?sts.length%4+1:sts.length%4;
		String[][] resSts = new String[length][];
		String[] stsTemp = new String[4];
		int j = 0;
		for(int i=0; i<sts.length; i++) {
			if(i != 0 && i%4 == 0) {
				resSts[j] = stsTemp;
				stsTemp = new String[4];
				j++;
			}
			stsTemp[i-4*j] = sts[i];
			if(i==sts.length-1) {
				resSts[j] = stsTemp;
			}
		}
		System.out.println(resSts);
	}
	
	/**
     * 复制文件
     *
     * @param fromFile 要复制的文件目录
     * @param toFile   要粘贴的文件目录
     * @return 是否复制成功
     */
	private static boolean copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return false;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();
        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory()) {
                //如果当前项为子目录 进行递归
                copy(currentFiles[i].getPath() + "/", toFile);
            } else {
                //如果当前项为文件则进行文件拷贝
            	if(currentFiles[i].getName().indexOf(".lrc")>0) {
            		copyFile(currentFiles[i].getPath(), toFile +File.separator+ currentFiles[i].getName());
            	}
            }
        }
        return true;
    }
	
	/**
     * 文件拷贝
     * 要复制的目录下的所有非子目录(文件夹)文件拷贝
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copyFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
	
	private static void reName(String filePath) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	            for (File f : fileArray) {

	            	if(f.isFile()) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		             		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("公众号")>0) {
		            		name = name.replace("微信公众号FM小屋", "");
		            		name = name.replace("微信公众号  FM小屋", "");
		            		name = name.replace("微信公众号 FM小屋", "");
		            		name = name.replace("微信 公众号 FM小屋", "");
		            		name = name.replace("第三季", "");
		            	}
		            	
	                	File rf = new File(filePath+"\\"+name);
		            	f.renameTo(rf);
	                    
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void reNameTitle(String filePath, String album) {

		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
	                    mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
	                    	
	                    }
	                    String title = id3v2Tag.getTitle();
	                    if(title != null) {
	                    	if(title.indexOf("【")>0) {
		                    	title = title.substring(0, title.indexOf("【")).trim();
		                    }
		                    
		                    if(title.indexOf("[")>0) {
		                    	title = title.substring(0, title.indexOf("[")).trim();
		                    }
		                    if(NumberUtil.checkNumeric(name.substring(0, name.lastIndexOf("."))+"")) {
	                    		title = album+" "+"第"+name.substring(0, name.lastIndexOf("."))+""+"集";
	                    	}else {
	                    		title = album+" "+name.substring(0, name.lastIndexOf("."));
	                    	}
		                    
		                    id3v2Tag.setTitle(title);
	                    }
	                    
	                    String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		            	//检查目标路径下，name是否已经存在
		            	if(FileUtil.fileIsFile(targetPath+"\\"+name)) {
		            		name = album+"-"+name;
		            	}
	                	File targetF = new File(targetPath);
	                    if (!targetF.exists()) {
	                    	targetF.mkdir();
	                    }
	                	mp3file.save(targetPath+"\\"+name);
	                    
//	                	File rf = new File(filePath+"\\"+name);
//		            	rf.renameTo(nf);
	                    
	                	if (f.exists() && f.isFile()) {
	                        f.delete();
	                    }
	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void modifyProperty(String filePath, String album, String albumArtist, String artist, String comment, String coverType) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
		            	if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
	                    mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
	                    	
	                    }
	                    String title = id3v2Tag.getTitle();
	                    if(title == null) {
	                    	title = album;
	                    	id3v2Tag.setTitle(album+" "+ name.substring(0, name.lastIndexOf("."))+"");
		                	id3v2Tag.setAlbum(album);
	                    }else {
	                    	if(title.indexOf("【")>0) {
		                    	title = title.substring(0, title.indexOf("【")).trim();
		                    }
		                    
		                    if(title.indexOf("[")>0) {
		                    	title = title.substring(0, title.indexOf("[")).trim();
		                    }
		                    
		                    id3v2Tag.setTitle(title);
		                    id3v2Tag.setAlbum(album);
	                    }
	                    
	                	id3v2Tag.setAlbumArtist(albumArtist);
	                	id3v2Tag.setArtist(artist);
	                	id3v2Tag.setComment(comment);
//	                	RandomAccessFile cover = new RandomAccessFile(filePath+File.separator+"cover"+File.separator+"cover."+coverType, "r");
//	                	byte[] bytes = new byte[(int) file.length()];
//	                	cover.read(bytes);
//	                	cover.close();
//	                	id3v2Tag.setAlbumImage(bytes, "image/"+coverType);
	                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
	                	File targetF = new File(targetPath);
	                    if (!targetF.exists()) {
	                    	targetF.mkdir();
	                    }
	                	mp3file.save(targetPath+"\\"+name);
	                    
//	                	File rf = new File(filePath+"\\"+name);
//		            	rf.renameTo(nf);
	                    
	                	if (f.exists() && f.isFile()) {
	                        f.delete();
	                    }
	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean changeToMp3(String sourcePath, String targetPath) {
		File source = new File(sourcePath);
		File target = new File(targetPath);
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();
 
		audio.setCodec("libmp3lame");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setOutputFormat("mp3");
		attrs.setAudioAttributes(audio);
 
		try {
			encoder.encode(new MultimediaObject(source), target, attrs);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (InputFormatException e) {
			e.printStackTrace();
			return false;
		} catch (EncoderException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void fillProperty(String filePath, String transPath, String album, String titlePrefix, String albumArtist, String artist, String comment) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	            for (File f : fileArray) {
	            	if(f.isFile()&&(f.getName().indexOf("mp3")>0 || f.getName().indexOf("m4a")>0)) {
	            		String name = f.getName();
	            		if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("微信公众号")>0) {
		            		name = name.replace("微信公众号FM小屋", "");
		            		name = name.replace("微信公众号  FM小屋", "");
		            		name = name.replace("微信公众号 FM小屋", "");
		            	}
		            	
	            		String targetPath = transPath + "\\" +name.substring(0, name.lastIndexOf("."))+".mp3";
//	            		if(changeToMp3(f.getAbsolutePath(), targetPath)) {
//	            			if (f.exists() && f.isFile()) {
//		                        f.delete();
//		                    }
//	            		}
	            		ConvertingAnyAudioToMp3_Example1.convertingAnyAudioToMp3(f.getAbsolutePath(), targetPath);
	            		if (f.exists() && f.isFile()) {
	                        f.delete();
	                    }
	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        
	        file = new File(transPath);
			fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {

	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
	            		mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
	                    	String title = titlePrefix+" "+ name.substring(0, name.lastIndexOf("."))+"";
	                    	if(NumberUtil.checkNumeric(name.substring(0, name.lastIndexOf("."))+"")) {
	                    		title = titlePrefix+" "+ "第"+name.substring(0, name.lastIndexOf("."))+""+"集";
	                    	}
	                    	id3v2Tag.setTitle(title);
		                	id3v2Tag.setAlbum(album);
		                	id3v2Tag.setAlbumArtist(albumArtist);
		                	id3v2Tag.setArtist(artist);
		                	id3v2Tag.setComment(comment);
		                	
		                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		                	File targetF = new File(targetPath);
		                    if (!targetF.exists()) {
		                    	targetF.mkdir();
		                    }
		                	mp3file.save(targetPath+"\\"+name);
		                	
		                	if (f.exists() && f.isFile()) {
		                        f.delete();
		                    }
	                    }

	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void reSetProperty(String filePath, String album, String titlePrefix, String albumArtist, String artist, String comment) {
		try {
			File file = new File(filePath);
			File[] fileArray = file.listFiles();
	        if (fileArray == null) {
	            System.out.println("no file");
	        } else {
	        	Mp3File mp3file = null;
	            for (File f : fileArray) {
	            	
	            	if(f.isFile()&&f.getName().indexOf("mp3")>0) {
	            		String name = f.getName();
	            		
		            	if(name.indexOf("【")>0) {
		            		name = name.substring(0, name.indexOf("【"))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("[")>0) {
		            		name = name.substring(0, name.indexOf("["))+name.substring(name.lastIndexOf("."),name.length());
		            	}
		            	
		            	if(name.indexOf("微信公众号")>0) {
		            		name = name.replace("微信公众号FM小屋", "");
		            		name = name.replace("微信公众号  FM小屋", "");
		            		name = name.replace("微信公众号 FM小屋", "");
		            	}
	            		
	            		mp3file = new Mp3File(f);
	                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	                    if(id3v2Tag != null) {
	                    	String title = titlePrefix+" "+ name.substring(0, name.lastIndexOf("."))+"";
	                    	if(NumberUtil.checkNumeric(name.substring(0, name.lastIndexOf("."))+"")) {
	                    		title = titlePrefix+" "+ "第"+name.substring(0, name.lastIndexOf("."))+""+"集";
	                    	}
	                    	id3v2Tag.setTitle(title);
		                	id3v2Tag.setAlbum(album);
		                	id3v2Tag.setAlbumArtist(albumArtist);
		                	id3v2Tag.setArtist(artist);
		                	id3v2Tag.setComment(comment);
		                	
		                	String targetPath = filePath+filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		                	File targetF = new File(targetPath);
		                    if (!targetF.exists()) {
		                    	targetF.mkdir();
		                    }
		                	mp3file.save(targetPath+"\\"+name);
		                	
		                	if (f.exists() && f.isFile()) {
		                        f.delete();
		                    }
	                    }

	            	} else if(f.isFile()&&f.getName().indexOf("mp4")>0) {
	            		reName(filePath);
	            	}
	            }
	        }
	        System.out.println("rename success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void testOut() throws IOException {
		
		//1
//		OutputStream out = new FileOutputStream("G:\\nh\\test\\r.txt");
//		OutputStreamWriter osw = new OutputStreamWriter(out);
//		osw.write("hello");
//		osw.write("world");
//		osw.close();
		
		//2
		FileWriter fw = new FileWriter("G:\\nh\\test\\r.txt");
		fw.write("hello");
		fw.write("world");
		fw.close();
		
		//3
		FileWriter fw1 = new FileWriter("G:\\nh\\test\\r.txt");
		BufferedWriter bfw = new BufferedWriter(fw1);
		bfw.write("hello");
		//写入一个换行符。BufferedWriter比FileWriter多出的函数
		bfw.newLine();
		bfw.write("world");
		bfw.close();
		fw1.close();
		
		Writer out1 = new BufferedWriter(new OutputStreamWriter(System.out));
		
		System.out.println("write success");
	}
}

//继承Thread类
class MyThread extends Thread{
    //重写run()方法
    public void run(){
        for(int i = 0;i < 10; i++){
            System.out.println(Thread.currentThread().getName() + i);
        }
    }
}
