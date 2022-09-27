package com.nh.dataspider.excelimport.utils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeansException;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.nh.dataspider.excelimport.annotation.ExcelImport;
import com.nh.dataspider.excelimport.constants.DateConst;
import com.nh.dataspider.excelimport.constants.FieldType;
import com.nh.dataspider.excelimport.constants.OtherFieldType;
import com.nh.dataspider.excelimport.entity.BaseEntity;
import com.nh.dataspider.excelimport.entity.ExcelImportResult;
import com.nh.dataspider.excelimport.entity.UserEntity;
import com.nh.dataspider.excelimport.mapper.ExcelImportMapper;

public class ExcelImportUtils<T extends BaseEntity> extends CommonParse{
	
	private static ExcelImportMapper excelImportMapper;
	public static ExcelImportMapper getExcelImportMapper() {
		if(excelImportMapper==null){
			excelImportMapper = SpringContext.getBean(ExcelImportMapper.class);
		}
		return excelImportMapper;
	}

	//观察是否setentity的标识
	private static boolean flag = false;
	//保存重复值的数据
	private static Map<String,List<String>> valueList = null;
	
	private static Map<String,String> unImportColumn = null;
	
	public static <T> Map<String, Object> imporateData(T t, InputStream in, String fileName, Map<String, String> userMap, Map<String, String> orgMap, Map<String, Map<String, Object>> codeMap, UserEntity user) throws Exception {
		//解析注解
		parseConfig(t);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> files = new ArrayList<Map<String,Object>>();
    	Map<String, Object> fileMap = new HashMap<String, Object>();
    	List<ExcelImportResult> resultList = new ArrayList<ExcelImportResult>();
    	ExcelImportResult report = null;
    	
    	String retMsg = "";
    	//计算未成功导入的数据的数量
    	int errorCount = 0;
    	//计算成功导入的数据的数量
    	int successCount = 0;
    	//计算重复的数据的数量
    	int existCount = 0;
    	
    	valueList = new HashMap<String,List<String>>();
    	//导入的标题列
    	Map<Integer,Object> colunmName =  new HashMap<Integer, Object>();
    	Sheet sheet = PoiUtils.getUploadExcelFile(in, fileName);
    	
    	//1、检查导入文件的标题栏是否正确
    	Map<String,Object> retColumnMap = PoiUtils.getColumnName(sheet,fieldNameAndBeanNameMap);
    	if(retColumnMap.containsKey("retCode") && "error".equals(retColumnMap.get("retCode").toString())) {
    		report = new ExcelImportResult();
    		report = PoiUtils.setOperationReport(report,"导入出错",""+retColumnMap.get("retMsg").toString(),ExcelImportResult.ImportResult.FAILURE.getText(),false,"","");
			resultList.add(report);
			retMsg = "导入出错，"+retColumnMap.get("retMsg").toString();
			fileMap.put("headError", true);
    	}else {
    		//1、循环遍历插入数据之前公共部分的准备
    		//获得标题栏
    		colunmName = PoiUtils.stringToIntKeyMap(retColumnMap.get("retMsg").toString());
    		fileMap.put("colunmName", retColumnMap.get("retColName").toString());
    		fileMap.put("retModelMap", retColumnMap.get("retMsg"));
    		
    		//检查导入的数据的数量，不能超过100条
    		if(sheet.getLastRowNum() > 100) {
    			report = new ExcelImportResult();
        		report = PoiUtils.setOperationReport(report,"导入出错","导入数据量不能超过100条！",ExcelImportResult.ImportResult.FAILURE.getText(),false,"","");
    			resultList.add(report);
    			retMsg = "导入出错，导入数据量不能超过100条！";
    			fileMap.put("headError", true);
    		}else {
    			//2、遍历当前sheet中的所有行
            	//直接从第二行开始，第一行默认作为标题行
        		for (int j = 1; j <= sheet.getLastRowNum(); j++) {
        			t = (T) t.getClass().newInstance();
        			//2、一行行遍历导入之前的检查
            		Map<String,Object> retMap = PoiUtils.validateExcel(sheet,sheet.getRow(j),j);
                	
                	if(retMap.containsKey("retCode")) {
                		if("error".equals(retMap.get("retCode").toString())) {
                			errorCount++;
                			report = new ExcelImportResult();
                    		report = PoiUtils.setOperationReport(report,retMap.get("retResult").toString(),retMap.get("retMsg").toString(),ExcelImportResult.ImportResult.FAILURE.getText(),false,"","");
        					resultList.add(report);
                    	}else {
                    		//获得具体行的数据
                    		List<Object> li = PoiUtils.stringToList(retMap.get("retMsg").toString());
                    		Map<String,Object> impResultMap  = importBusinessData(t, li, colunmName, userMap, orgMap, codeMap, user);

                			if("error".equals(impResultMap.get("retCode").toString())) {
                				errorCount++;
                				report = new ExcelImportResult();
                        		int rowNo = j+1;
                        		report = PoiUtils.setOperationReport(report,"导入第" + rowNo + "行出错",""+impResultMap.get("retMsg").toString(),ExcelImportResult.ImportResult.FAILURE.getText(),false,impResultMap.get("data").toString(),impResultMap.get("showData").toString());
            					resultList.add(report);
                        	}else if("exist".equals(impResultMap.get("retCode").toString())) {
                        		existCount++;
                				report = new ExcelImportResult();
                        		int rowNo = j+1;
                        		report = PoiUtils.setOperationReport(report,"导入第" + rowNo + "行出错",""+impResultMap.get("retMsg").toString(),ExcelImportResult.ImportResult.EXIST.getText(),false,impResultMap.get("data").toString(),impResultMap.get("showData").toString());
            					resultList.add(report);
                        	}else {
                        		successCount++;
                        		report = new ExcelImportResult();
                        		report = PoiUtils.setOperationReport(report,"","",ExcelImportResult.ImportResult.SUCCESS.getText(),true,impResultMap.get("data").toString(),impResultMap.get("showData").toString());
            					resultList.add(report);
                        	}
                    	}
                	}
        		}
    		}
    	}
        
        fileMap = saveFileMap(fileMap, errorCount, existCount, successCount, resultList, retMsg);
        files.add(fileMap);
        resultMap.put("files", files);
        return resultMap;
	}
	
	/**
	 * 导入数据
	 * @param t 实体
	 * @param lo excel中数据
	 * @param colunmName
	 * @param userMap 用户map
	 * @param orgMap 部门map
	 * @param codeMap 编码
	 * @param user 当前用户
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<String,Object> importBusinessData(T t, List<Object> lo,Map<Integer,Object> colunmName, Map<String, String> userMap, Map<String, String> orgMap, Map<String, Map<String, Object>> codeMap, UserEntity user) throws Exception {
		Map<String,Object> retMap = new HashMap<String,Object>();
		
		Set<String> fieldNames = fieldNameAndFieldConfigMap.keySet();
		
		String result = "";
		//将colunmName的key，value值转换一下，方便后续取值
		Map<String, Integer> idCloNameMap = new HashMap<String, Integer>();
		
		unImportColumn = new HashMap<String, String>();
		boolean  isExist = false;
		boolean  isError = false;
		//将excel中的内容存到实体中
		for(int j=0;j<colunmName.size();j++) {
			//给setentity的标识标为false，代表需要setentity
			flag = false;
			String colName = String.valueOf(colunmName.get(j));
			idCloNameMap.put(colName, j);
	     	String value = lo.get(j).toString();
	     	
	     	if(fieldNames.contains(colName)) {
	     		ExcelImport fieldConfig = fieldNameAndFieldConfigMap.get(colName);
	     		if(!isNotEmptyOrNull(value)) {
		     		if(fieldConfig.isRequired()) {
		     			//如果是必输项，但是值为空的给出提示
		     			result += fieldConfig.name()+"不能为空！"+"；";
		     		}else {
		     			//如果非必输，判断有没有默认值
		     			if(!"".equals(fieldConfig.defaultValue())) {
		     				//如果有默认值的话，存入默认值
		     				prepareImportData(t, fieldConfig.fieldType(), colName, fieldConfig.defaultValue());
		     			}
		     		}
		     	}else {
		     		//判断是否外键字段
		     		if(fieldConfig.isForeignExist()) {
		     			//检查外表中关联字段是否存在
		     			String res = checkForeignIsExsit(t, fieldConfig, colName, value, user);
		     			if(res.length()>0) {
		     				isError = true;
		     				result += res;
		     			}
		     		}else if(fieldConfig.isForeignCol()) {
		     			String res = setForeignColum(t, fieldConfig, idCloNameMap, lo, colName, value, user);
		     			if(res.length()>0) {
		     				isError = true;
		     				result += res;
		     			}
		     		}else if(fieldConfig.isGroup()) {
		     			//判断是否是编码类型
		     			String res = setGroupValue(t, fieldConfig, value, colName, codeMap);
		     			if(res.length()>0) {
		     				isError = true;
		     				result += res;
		     			}
		     		}else if(fieldConfig.isOtherType()) {
		     			//判断是否是其他类型
		     			//如果是默认
						if(fieldConfig.isDefault()) {
							setDefaultType(t, fieldConfig, value, colName, user);
						}else {
							String res = setOtherType(t, fieldConfig, null, colName, value, userMap, orgMap, user);
							if(res.length()>0) {
			     				isExist = true;
			     				result += res;
			     			}
						}
		     		}
		     		//判断是否是唯一
		     		if(fieldConfig.isUnique()) {
		     			//方便后续唯一性判断
		     			String res = checkIsExsit(t, fieldConfig, colName, value, user);
		     			if(res.length()>0) {
		     				isExist = true;
		     				result += res;
		     			}
		     		}else {
		     			if(!flag) {
		     				prepareImportData(t, fieldConfig.fieldType(), colName, value);
		     			}
					}
		     		
		     		
		     		
		     	}
	     	}else {
	     		//不在导入的字段范围内（在执行这个导入之前应该检查是否存在无法导入的字段）
	     	}
		}
		
		//补充实体类中其他默认字段
		for(String fieldName : fieldNames) {
			ExcelImport fieldConfig = fieldNameAndFieldConfigMap.get(fieldName);
			//如果是excel中已经有的字段，则继续循环
			if(idCloNameMap.containsKey(fieldName)) {
				continue;
			}
			if(fieldConfig.isRequired()) {
     			//如果是必输项，但是值为空的给出提示
     			result += fieldConfig.name()+"不能为空！"+"；";
     		}else {
     			//如果非必输，判断有没有默认值
     			if(!"".equals(fieldConfig.defaultValue())) {
     				//如果有默认值的话，存入默认值
     				prepareImportData(t, fieldConfig.fieldType(), fieldName, fieldConfig.defaultValue());
     			}
     		}
			//补充excel中没有的字段
			if(fieldConfig.isOtherType()) {
				if(fieldConfig.isDefault()) {
					setDefaultType(t, fieldConfig, "", fieldName, user);
				}else {
					String sourceField = fieldConfig.sourceCol();
					//比如人员id，实际是根据excel中的人员名称导入的
					if(idCloNameMap.containsKey(sourceField)) {
						int index = idCloNameMap.get(sourceField);
						String value = lo.get(index).toString();
						String res = setOtherType(t, fieldConfig, fieldNameAndFieldConfigMap.get(sourceField), fieldName, value, userMap, orgMap, user);
		     			if(res.length()>0) {
		     				isError = true;
		     				result += res;
		     			}
					}else {
						//如果不存在，说明关联字段不在excel用户导入的数据中（目前没有想到相应的场景）
					}
				}
			}else {
				//如果是外部关联的字段
				if(fieldConfig.isForeignCol()) {
					String res = setForeignColum(t, fieldConfig, idCloNameMap, lo, fieldName, "", user);
	     			if(res.length()>0) {
	     				isError = true;
	     				result += res;
	     			}
	     			
				}
				//如果不是特殊类型的（目前没有想到应用场景）
			}

		}
		
		
		if(unImportColumn !=null && unImportColumn.size()>0) {
			Set<String> keySet = unImportColumn.keySet();
			for(String col : keySet) {
				ExcelImport fieldConfig = fieldNameAndFieldConfigMap.get(col);
				if(fieldConfig.isGroup()) {
	     			//判断是否是编码类型
	     			String res = setGroupValue(t, fieldConfig, unImportColumn.get(col), col, codeMap);
	     			if(res.length()>0) {
	     				isError = true;
	     				result += res;
	     			}
	     		}
			}
		}
		
		retMap.put("data", JSON.toJSONString(t));
		retMap.put("showData", lo);
		if(result.length()<=0) {
			retMap.put("retCode", "success");
        }else {
        	if(isExist && !isError) {
				retMap.put("retCode", "exist");
				retMap.put("retMsg", result.substring(0, result.length()-1));
			}else {
				retMap.put("retCode", "error");
	        	retMap.put("retMsg", result.substring(0, result.length()-1));
			}
        }
 	   	return retMap;
	}
	
	/**
	 * set外键关联的字段
	 * @param t
	 * @param fieldConfig
	 * @param colName
	 * @param value
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private static <T> String setForeignColum(final T t, ExcelImport fieldConfig, Map<String, Integer> idCloNameMap, List<Object> lo, String colName, String value, UserEntity user) throws Exception {
		String result = "";
		if(fieldConfig.isForeignCol()) {
			String tableName = fieldConfig.foreignTable();
			if(!isNotEmptyOrNull(tableName)) {
				throw new RuntimeException(colName+"：未指定关联表名！");
			}
			//外表中用于比较的字段
			String compareCol = fieldConfig.compareCol();
			if(!isNotEmptyOrNull(compareCol)) {
				throw new RuntimeException(colName+"：未指定比较字段！");
			}
			compareCol = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, compareCol);
			//要查询的字段
			String resultCol = fieldConfig.searchCol();
			if(!isNotEmptyOrNull(resultCol)) {
				throw new RuntimeException(colName+"：未指定返回字段！");
			}
			resultCol = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, resultCol);
			//源字段
			String sourceField = fieldConfig.sourceCol();
			/*if(!isNotEmptyOrNull(sourceField)) {
				throw new RuntimeException(colName+"：未指定源字段！");
			}*/
			//如果存在源字段，说明要通过去源字段的值，作为value值比较，如果不是，说明是当前字段的value值
			if(sourceField.length() > 0 && idCloNameMap.containsKey(sourceField)) {
				int index = idCloNameMap.get(sourceField);
				value = lo.get(index).toString();
			}
			
			List<String> list = getExcelImportMapper().selectByColum(tableName, resultCol, compareCol, value, user);
			if(list != null && list.size()>0) {
				if(list.size()>1) {
					result = fieldConfig.name()+"："+value+"存在多条主记录！"+"；";
				}else {
					value = list.get(0);
				}
			}else {
				value = "";
			}
			prepareImportData(t, fieldConfig.fieldType(), colName, value);
		}
		return result;
	}
	
	/**
	 * 唯一性检查
	 * @param t
	 * @param fieldConfig
	 * @param colName
	 * @param value
	 * @param valueList
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private static <T> String checkIsExsit(final T t, ExcelImport fieldConfig, String colName, String value, UserEntity user) throws Exception {
		String result = "";
		if(fieldConfig.isUnique()) {
			if(valueList != null && valueList.containsKey(colName) && valueList.get(colName).contains(value)) {
				result = fieldConfig.name()+"："+value+"已经存在！"+"；";
			}else {
				String tableName = t.getClass().getAnnotation(TableName.class).value();
				//判断表名是否存在
				if(!isNotEmptyOrNull(tableName)) {
					throw new RuntimeException("未知表名！");
				}else {
					String beanName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, colName);
					int count = 0;
					if(catchNoSuchMethodException(t.getClass(), "DeleteUser", String.class)) {
						count = getExcelImportMapper().selectUnDelCountByColum(tableName, beanName, value, user);
					}else {
						count = getExcelImportMapper().selectCountByColum(tableName, beanName, value, user);
					}
					if(count > 0) {
						result = fieldConfig.name()+"："+value+"已经存在！"+"；";
						//给数据库存在的那条记录的id获取到
						List<String> idList = getExcelImportMapper().selectIdByColum(tableName, beanName, value, user);
						//已经存在的情况下，也要set到实体中，为后续覆盖更新准备
						prepareImportData(t, FieldType.TYPE_STRING, "id", idList.get(0));
					}
					if(valueList.containsKey(colName)) {
						valueList.get(colName).add(value);
					}else {
						List<String> list = Lists.newArrayList();
						list.add(value);
						valueList.put(colName, list);
					}
					//已经存在的情况下，也要set到实体中，为后续覆盖更新准备
					prepareImportData(t, fieldConfig.fieldType(), colName, value);
				}
			}
		}
		return result;
	}
	
	private static <T> String checkForeignIsExsit(final T t, ExcelImport fieldConfig, String colName, String value, UserEntity user) throws Exception {
		String result = "";
		if(fieldConfig.isForeignExist()) {
			//检查外表的字段是否存在
			String tableName = fieldConfig.foreignTable();
			//比较字段
			String compareCol = fieldConfig.compareCol();
			if(!isNotEmptyOrNull(compareCol)) {
				throw new RuntimeException(colName+"：未指定比较字段！");
			}else {
				compareCol = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, compareCol);
				
				int count = getExcelImportMapper().selectListByColum(tableName, compareCol, value, user);
				if(count > 0) {
					//prepareImportData(t, fieldConfig.fieldType(), colName, value);
				}else {
					result = fieldConfig.name()+"："+value+"不存在！"+"；";
				}
			}
		}
		return result;
	}
	
	/**
	 * 特殊类型set值
	 * @param t
	 * @param fieldConfig
	 * @param sourceFieldConfig
	 * @param colName
	 * @param value
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private static <T> String setOtherType(final T t, ExcelImport fieldConfig, ExcelImport sourceFieldConfig, String colName, String value, Map<String, String> userMap, Map<String, String> orgMap, UserEntity user) throws Exception {
		String result = "";
		if(fieldConfig.isOtherType()) {
			if(fieldConfig.isDefault()) {
				setDefaultType(t, fieldConfig, "", colName, user);
			}else {
				int type = -2;
				if(sourceFieldConfig == null) {
					type = fieldConfig.otherFieldType();
				}else {
					type = sourceFieldConfig.otherFieldType();
				}
				
				//如果源字段的类型是用户名
				if(type == OtherFieldType.USER_TYPE.USER_NAME || type == OtherFieldType.USER_TYPE.USER_ID) {
					if(sourceFieldConfig != null && !userMap.containsKey(value)) {
						result += sourceFieldConfig.name()+"【"+value+"】不存在；"+"；";
				   	}else {
				   		Map u = JSON.parseObject(JSON.toJSONString(userMap.get(value)), Map.class);
						switch (fieldConfig.otherFieldType()) {
							case OtherFieldType.USER_TYPE.USER_ID:
								prepareImportData(t, fieldConfig.fieldType(), colName, u.get("userId").toString());
								break;
							case OtherFieldType.DEPT_TYPE.DEPT_ID:
								prepareImportData(t, fieldConfig.fieldType(), colName, u.get("orgId").toString());
								break;
							case OtherFieldType.DEPT_TYPE.DEPT_NAME:
								prepareImportData(t, fieldConfig.fieldType(), colName, u.get("orgName").toString());
								break;
							default:
								prepareImportData(t, fieldConfig.fieldType(), colName, value);
								break;
						}
				   	}
				}else if(type == OtherFieldType.DEPT_TYPE.DEPT_NAME || type == OtherFieldType.DEPT_TYPE.DEPT_ID) {
					if(sourceFieldConfig != null && !orgMap.containsKey(value)) {
						result += sourceFieldConfig.name()+"【"+value+"】不存在；"+"；";
				   	}else {
				   		switch (fieldConfig.otherFieldType()) {
							case OtherFieldType.DEPT_TYPE.DEPT_ID:
								//存入部门id
								prepareImportData(t, FieldType.TYPE_STRING, colName, orgMap.get(value));
								break;
							default:
								prepareImportData(t, FieldType.TYPE_STRING, colName, value);
								break;
						}
				   	}
				}
			}
		}
		return result;
	}
	
	/**
	 * 默认特殊类型set值
	 * @param t
	 * @param fieldConfig
	 * @param colName
	 * @param user
	 * @throws Exception
	 */
	private static <T> void setDefaultType(final T t, ExcelImport fieldConfig, String value, String colName, UserEntity user) throws Exception {
		if(fieldConfig.isOtherType()) {
			//如果是默认
			if(fieldConfig.isDefault()) {
				switch (fieldConfig.otherFieldType()) {
					case OtherFieldType.ID:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, createId());
						break;
					case OtherFieldType.USER_TYPE.USER_ID:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, user.getUserId());
						break;
					case OtherFieldType.USER_TYPE.USER_NAME:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, user.getUserName());
						break;
					case OtherFieldType.DEPT_TYPE.DEPT_ID:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, user.getOrgId());
						break;
					case OtherFieldType.DEPT_TYPE.DEPT_NAME:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, user.getOrgName());
						break;
					case OtherFieldType.TENANT_ID:
						//根据当前的用户
						prepareImportData(t, fieldConfig.fieldType(), colName, user.getTenantId());
						break;
					case OtherFieldType.CURRENT_TIME:
						//获取当前时间
						prepareImportData(t, fieldConfig.fieldType(), colName, datetoString(new Date(), DateConst.DATE_MODEL_5));
						break;
					default:
						prepareImportData(t, fieldConfig.fieldType(), colName, value);
						break;
				}
			}
		}
	}
	
	/**
	 * set编码类型
	 * @param t
	 * @param fieldConfig
	 * @param value
	 * @param colName
	 * @param codeMap
	 * @return
	 * @throws Exception
	 */
	private static <T> String setGroupValue(final T t, ExcelImport fieldConfig, String value, String colName, Map<String, Map<String, Object>> codeMap) throws Exception {
		String result = "";
		if(codeMap == null) {
			result = fieldConfig.name()+"【"+value+"】不存在"+"；";
		}else {
			String groupKey = "";
			//指定特定的groupkey
			if(fieldConfig.groupKey().length()>0) {
				groupKey = fieldConfig.groupKey();
			}else {
				//关联其他字段
				String sourceField = fieldConfig.sourceCol();
				String setName = sourceField.substring(0, 1).toUpperCase()+sourceField.substring(1,sourceField.length());
				groupKey = t.getClass().getDeclaredMethod("get" + setName).invoke(t) ==null ?"":t.getClass().getDeclaredMethod("get" + setName).invoke(t).toString();
				if(!isNotEmptyOrNull(groupKey) && !unImportColumn.containsKey(colName)) {
					unImportColumn.put(colName, value);
				}
			}
			if(isNotEmptyOrNull(groupKey)) {
				Map<String, Object> groupMap = codeMap.get(groupKey);
				if(groupMap == null) {
					result = fieldConfig.name()+"【"+value+"】不存在"+"；";
				}else {
					groupMap = codeMap.get(groupKey);
				}
				if(groupMap ==null || !isNotEmptyOrNull(groupMap.get(value))) {
					result = fieldConfig.name()+"【"+value+"】不存在"+"；";
				}else {
					if(groupMap.containsKey(value)) {
						prepareImportData(t, fieldConfig.fieldType(), colName, groupMap.get(value).toString());
					}else {
						result = fieldConfig.name()+"【"+value+"】不存在"+"；";
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * set编码类型
	 * @param t
	 * @param fieldConfig
	 * @param value
	 * @param colName
	 * @param codeMap
	 * @return
	 * @throws Exception
	 */
	private static <T> String setEspecially(final T t, ExcelImport fieldConfig, String value, String colName, Map<String, Map<String, Object>> codeMap) throws Exception {
		String result = "";
		if(codeMap == null) {
			result = fieldConfig.name()+"【"+value+"】不存在"+"；";
		}else {
			String groupKey = "";
			//指定特定的groupkey
			if(fieldConfig.groupKey().length()>0) {
				groupKey = fieldConfig.groupKey();
			}else {
				//关联其他字段
				String sourceField = fieldConfig.sourceCol();
				String setName = sourceField.substring(0, 1).toUpperCase()+sourceField.substring(1,sourceField.length());
				groupKey = t.getClass().getDeclaredMethod("get" + setName).invoke(t) ==null ?"":t.getClass().getDeclaredMethod("get" + setName).invoke(t).toString();
				if(!isNotEmptyOrNull(groupKey) && !unImportColumn.containsKey(colName)) {
					unImportColumn.put(colName, value);
				}
			}
			if(isNotEmptyOrNull(groupKey)) {
				Map<String, Object> groupMap = codeMap.get(groupKey);
				if(groupMap == null) {
					result = fieldConfig.name()+"【"+value+"】不存在"+"；";
				}else {
					groupMap = codeMap.get(groupKey);
				}
				if(groupMap ==null || !isNotEmptyOrNull(groupMap.get(value))) {
					result = fieldConfig.name()+"【"+value+"】不存在"+"；";
				}else {
					if(groupMap.containsKey(value)) {
						prepareImportData(t, fieldConfig.fieldType(), colName, groupMap.get(value).toString());
					}else {
						result = fieldConfig.name()+"【"+value+"】不存在"+"；";
					}
				}
			}
		}
		return result;
	}
	
	private static <T>boolean catchNoSuchMethodException(Class classt, String setName, Class<?>... parameterTypes) {
		boolean flag = true;
		try {
			classt.getDeclaredMethod("set" + setName, parameterTypes);
        }catch (NoSuchMethodException e) {
        	flag = false;
        }
		return flag;
	}
	
	private static <T> void prepareImportData(final T t,String paramType,String colName,String value) throws Exception{
		String setName = setName = colName.substring(0, 1).toUpperCase()+colName.substring(1,colName.length());
		Class classt = t.getClass();
    	Constructor[] conArray = classt.getDeclaredConstructors();
    	
    	flag = true;
    	if(FieldType.TYPE_STRING.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, String.class)) {
    				classt.getDeclaredMethod("set" + setName, String.class).invoke(t, value);
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, String.class).invoke(t, value);
    			}
    		}
    	}else if(FieldType.TYPE_DATE.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, Date.class)) {
    				classt.getDeclaredMethod("set" + setName, Date.class).invoke(t, stringParseDate(value, DateConst.DATE_MODEL_5));
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, Date.class).invoke(t, stringParseDate(value, DateConst.DATE_MODEL_5));
    			}
    		}
    	}else if(FieldType.TYPE_INTEGER.equals(paramType)) {
    		if(catchNoSuchMethodException(classt, setName, Integer.class)) {
    			classt.getDeclaredMethod("set" + setName, Integer.class).invoke(t, Integer.parseInt(value));
			}else {
				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, Integer.class).invoke(t, Integer.parseInt(value));
			}
    	}else if(FieldType.TYPE_INT.equals(paramType)) {
    		if(catchNoSuchMethodException(classt, setName, Integer.class)) {
    			classt.getDeclaredMethod("set" + setName, Integer.class).invoke(t, Integer.parseInt(value));
			}else {
				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, Integer.class).invoke(t, Integer.parseInt(value));
			}
    	}else if(FieldType.TYPE_DOUBLE.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, Double.class)) {
    				classt.getDeclaredMethod("set" + setName, Double.class).invoke(t, Double.parseDouble(value));
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, Double.class).invoke(t, Double.parseDouble(value));
    			}
    		}
    	}else if(FieldType.TYPE_FLOAT.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, Float.class)) {
    				classt.getDeclaredMethod("set" + setName, Float.class).invoke(t, Float.parseFloat(value));
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, Float.class).invoke(t, Float.parseFloat(value));
    			}
    		}
    	}else if(FieldType.TYPE_LONG.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, long.class)) {
    				classt.getDeclaredMethod("set" + setName, long.class).invoke(t, Long.parseLong(value));
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, long.class).invoke(t, Long.parseLong(value));
    			}
    		}
    	}else if(FieldType.TYPE_BIGDECIMAL.equals(paramType)) {
    		if(isNotEmptyOrNull(value)) {
    			if(catchNoSuchMethodException(classt, setName, BigDecimal.class)) {
    				classt.getDeclaredMethod("set" + setName, BigDecimal.class).invoke(t, new BigDecimal(value));
    			}else {
    				t.getClass().getSuperclass().getDeclaredMethod("set" + setName, BigDecimal.class).invoke(t, new BigDecimal(value));
    			}
    		}
    	}
    }
	
	/**
     * 验证对象不为空也不为NULL
     * @param str 验证对象
     * @return 处理结果 空/Null:true,否则:false
     */
    private static boolean isNotEmptyOrNull(Object str) {
        if (!"".equals(str) && null != str) {
            return true;
        }
        return false;
    }
	
	/**
     * 字符串转换为指定格式的日期,当字符串格式不确定的时候必须使用此方法
     *
     * @param str
     * @param format
     * @return
     * @throws ParseException
     */
    private static Date stringParseDate(String str, String format) throws ParseException {
        String strFormat = DateConst.getType(str);

        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        SimpleDateFormat sdf1 = new SimpleDateFormat(format);
        if (str == null || "".equals(str)) {
            return null;
        } else {
            Date date = sdf.parse(str);
            String date1 = datetoString(date, format);
            return sdf1.parse(date1);
        }
    }
    
    /**
     * 日期转换为指定格式的字符串
     */
    private static String datetoString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date == null) {
            return null;
        } else {
            return sdf.format(date);
        }
    }
    
    /**
     * 创建唯一ID
     * @return
     */
    private static String createId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
	
    public static Map<String, Object> saveFileMap(Map<String, Object> fileMap,int errorCount, int existCount, int successCount, List<ExcelImportResult> resultList, String retMsg) {
        //用于删除文件；如果此属性存在，界面删除需要用户自行处理，组件库上传的"remove"方法无效。
        fileMap.put("type", "xls");
        fileMap.put("errorCount",errorCount);
        fileMap.put("existCount",existCount);
        fileMap.put("successCount",successCount);
        int sumCount = errorCount+existCount+successCount;
        if(retMsg.length() <= 0) {
        	retMsg = "共"+sumCount+"条记录："+successCount+"条新建，"+existCount+"条重复，"+errorCount+"条错误！";
        }
        fileMap.put("resultList", resultList);
        fileMap.put("retMsg",retMsg);
        return fileMap;
    }
    
    /*
	 * 获取指定service
	 */
	public static <T> T getService(Class<T> classtype) {
		T service = (T) SpringContext.getBean(classtype);
		return service;
	}
}
