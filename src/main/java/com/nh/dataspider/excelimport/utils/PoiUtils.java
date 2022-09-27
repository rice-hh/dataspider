package com.nh.dataspider.excelimport.utils;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.excelimport.entity.ExcelImportResult;

public class PoiUtils {

	private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel
    
    public static Sheet getUploadExcelFile(final HttpServletRequest request) throws Exception {
    	MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
//    	MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
    	MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    	
        // 获取传入文件
        request.setCharacterEncoding("utf-8");
        multipartRequest.setCharacterEncoding("utf-8");
        //PC name属性为 'uploadFile'
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        MultipartFile tempFile = multipartRequest.getFile("uploadFile");
        //移动 name属性为 'file'
        if(tempFile == null) {
        	tempFile = multipartRequest.getFile("files");
        }
        final MultipartFile file = tempFile;
    	InputStream inputStream = file.getInputStream();
		//创建Excel工作薄
        Workbook workBook = getWorkbook(inputStream,file.getOriginalFilename());
        if(null == workBook){
            throw new Exception("创建Excel工作薄为空！");
        }
		Sheet sheet = workBook.getSheetAt(0);
		return sheet;
    }
    
    public static Sheet getUploadExcelFile(final InputStream inputStream, String fileName) throws Exception {
		//创建Excel工作薄
        Workbook workBook = getWorkbook(inputStream,fileName);
        if(null == workBook){
            throw new Exception("创建Excel工作薄为空！");
        }
		Sheet sheet = workBook.getSheetAt(0);
		return sheet;
    }
    
    /**
     * 描述：根据文件后缀，自适应上传文件的版本 
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }
    
    /**
     * 保存操作情况（导入提示信息）
     * @param report
     * @param operationName
     * @param rusult
     * @param remark
     * @return
     */
    public static ExcelImportResult setOperationReport(ExcelImportResult report, String operationName, String remark, String rusult, boolean isImport, String data, String showData) {
    	report.setOperationName(operationName);
		report.setRemark(remark);
		report.setResult(rusult);
		report.setData(data);
		report.setShowData(showData);
		report.setCanImport(isImport);
		return report;
    }
    
    /**
     * 验证Excel每一行数据
     * @param sheet
     * @param row
     * @param j
     * @return
     */
    public static Map<String,Object> validateExcel(Sheet sheet,Row row,int j) {
    	Map<String,Object> retMap = new HashMap<String,Object>();
    	Cell cell =null;
    	
		//遍历当前sheet中的所有行
    	//判断是否是空行
        if(isNotEmptyOrNull(row)) {
        	//获取标题的列数
            int colLength = sheet.getRow(0).getLastCellNum();
            
            //遍历所有的列
            List<Object> li = new ArrayList<Object>();
            //计算为空值的列数
            int countCol =0;
            for (int y = 0; y < colLength; y++) {
                cell = row.getCell(y);
                if(isNotEmptyOrNull(cell)) {
                	if(!isNotEmptyOrNull(getCellValue(cell))){
                		countCol++;
                	}
                	li.add(getCellValue(cell));
                }else {
                	countCol++;
                	li.add("");
                }
            }
            //当为空的列与总列数相等是，不加入list中，因为可能是空值行
            if(countCol == colLength) {
            	retMap.put("retCode", "error");
            	retMap.put("retResult", "导入第" + j + "行出错");
            	retMap.put("retMsg", "原因：该行为空行！");
            }else {
            	retMap.put("retCode", "success");
            	retMap.put("retResult", "检查成功！");
            	retMap.put("retMsg", listToString(li));
            }
        }else {
        	retMap.put("retCode", "error");
        	retMap.put("retResult", "导入第" + j + "行出错");
        	retMap.put("retMsg", "原因：该行为空行！");
        }
    	
        return retMap;
    }
    
    /**
     * 描述：对表格中数值进行格式化
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell){
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字

//        switch (cell.getCellType()) {
//	        case Cell.CELL_TYPE_STRING:
//	            value = cell.getRichStringCellValue().getString();
//	            break;
//	        case Cell.CELL_TYPE_NUMERIC:
//	            if("General".equals(cell.getCellStyle().getDataFormatString())){
//	                value = df.format(cell.getNumericCellValue());
//	            }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
//	                value = sdf.format(cell.getDateCellValue());
//	            }else{
//	                value = df2.format(cell.getNumericCellValue());
//	            }
//	            break;
//	        case Cell.CELL_TYPE_BOOLEAN:
//	            value = cell.getBooleanCellValue();
//	            break;
//	        case Cell.CELL_TYPE_BLANK:
//	            value = "";
//	            break;
//	        default:
//	            break;
//        }
        return value;
    }

    /**
     * 获取导入Excel的标题栏
     * @param sheet
     * @return
     */
    public static Map<String,Object> getColumnName(Sheet sheet,Map<String,String> mapColumn) {
    	Map<String,Object> retMap = new HashMap<String,Object>();
    	List<Object> listName = new ArrayList<Object>();
    	Map<String,Object> colunmName = new HashMap<String,Object>();
    	// 返回值colName
		List listModel = new ArrayList();
     	Map<String, Object> map = null;
    	String colName = "";
    	String alerMsg = "";
    	boolean flag = false;
    	Cell cell =null;
    	Row row1 = sheet.getRow(0);
        int colLength = sheet.getRow(0).getLastCellNum();
        for (int y = 0; y < colLength; y++) {
            cell = row1.getCell(y);
            if(isNotEmptyOrNull(cell) && isNotEmptyOrNull(getCellValue(cell))) {
            	listName.add(getCellValue(cell));
            }
        }
        
        if(listName.size()>0) {
        	for(int i=0;i<listName.size();i++) {
           	   if(mapColumn.containsKey(listName.get(i))) {
           		   colunmName.put(i+"", mapColumn.get(listName.get(i)));
           		   colName += listName.get(i)+",";
           		   continue;
           	   }else {
           		   flag = true;
           		   alerMsg += listName.get(i)+",";
           	   }
              }
        }else {
        	flag = true;
        	retMap.put("retCode", "error");
        	retMap.put("retMsg", "标题行为空！");
        	return retMap;
        }
        
        if(flag) {
      	   	if(alerMsg.lastIndexOf(",")>0) {
      	   		alerMsg = alerMsg.substring(0,alerMsg.length()-1);
      	   	}
	      	retMap.put("retCode", "error");
	     	retMap.put("retMsg", "文件中【"+alerMsg + "】列不属于模板中的列；");
        }else {
        	retMap.put("retCode", "success");
	     	retMap.put("retMsg", mapToString(colunmName));
	     	if(colName.lastIndexOf(",")>0) {
	     		colName = colName.substring(0,colName.length()-1);
	     	}
	     	retMap.put("retColName", colName);
        }
         return retMap;
    }
    
    /**
	 * map 转  String
	 * @param map
	 * @return
	 */
	public static String mapToString(Map map) {
		return JSONObject.toJSONString(map);
	}
	
	/**
	 * list转成string
	 * @param list
	 * @return
	 */
	public static String listToString(List list) {
		return JSONArray.toJSONString(list);
	}
	
	/**
	 * String 转 list
	 * @param str
	 * @return
	 */
	public static List stringToList(String str) {
		List<Object> list = com.alibaba.fastjson.JSONArray.parseArray(str);
		return list;
	}
	
	/**
	 * 给string转成key是Integer类型的map
	 * @param str
	 * @return
	 */
	public static Map stringToIntKeyMap(String str){
		Map<Integer,Object> retMap = new HashMap<Integer,Object>();
		 JSONObject jasonObject = JSONObject.parseObject(str);
		 Map map = (Map)jasonObject;
		 for(Object s:map.keySet()){
			 Integer key = Integer.parseInt(s.toString());
			 retMap.put(key, map.get(key+""));
	     }
		 return retMap;
	}

	public static Map<String, Object> initImportCode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * 返回填充Excel表格的数据
     * @param li 具体的数据
     * @param retExcelMsg 写入Excel中第一列导入情况的说明内容
     * @return
     */
    public final static List<List<String>> getSheetData(List<List<String>> sheetData,List<Object> li,String retExcelMsg) {
    	List<String> listString = new ArrayList<String>();
    	
    	//写入Excel中第一列导入情况的说明内容
		listString.add(retExcelMsg);
		
		if(isNotEmptyOrNull(li)) {
			for(int i=0;i<li.size();i++) {
				listString.add(li.get(i).toString());
			}
		}
		
		sheetData.add(listString);
		return sheetData;
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
}
