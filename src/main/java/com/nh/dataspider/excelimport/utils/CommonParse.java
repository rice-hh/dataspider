package com.nh.dataspider.excelimport.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

//import javax.persistence.Table;

import com.nh.dataspider.excelimport.annotation.ExcelImport;
import com.nh.dataspider.excelimport.entity.BaseEntity;

public abstract class CommonParse<T extends BaseEntity> {
	/**
	 * excel导入导出Sheet规则配置描述
	 */
	protected static ExcelImport sheetConfig;
	
	/**
	 * excel导入导出table
	 */
//	protected static Table tableConfig;
	
	/**
	 * excel导入导出字段规则配置
	 */
 	protected static Map<Field,ExcelImport> fieldConfigAndFieldMap;
 	
 	/**
 	 * 字段名称map（key：中文名，value:英文名（bean））
 	 */
 	protected static Map<String,String> fieldNameAndBeanNameMap;
 	
 	/**
	 * excel导入导出字段规则配置
	 */
 	protected static Map<String,ExcelImport> fieldNameAndFieldConfigMap;
 	
 	/**
 	 * @param <T> 泛型
 	 * @param clazz excel行记录抽象对象
 	 * @throws ExcelException 
 	 */
	public static <T> void parseConfig(T clazz) throws Exception {

		// 1.excel导入Sheet解析规则配置描述
//		fieldConfigAndFieldMap = new LinkedHashMap<Field,ExcelFieldConfig>();
		fieldNameAndFieldConfigMap = new LinkedHashMap<String,ExcelImport>();
		fieldNameAndBeanNameMap = new LinkedHashMap<String,String>();
		/*tableConfig = clazz.getClass().getAnnotation(Table.class);
		if (null == sheetConfig) {
			throw new Exception("未配置sheet解析规则,无法继续解析");
		}*/

		// 2.excel导入字段解析规则配置描述
		Field[] fields = clazz.getClass().getDeclaredFields();
		Field[] superFields = clazz.getClass().getSuperclass().getDeclaredFields();
		if (null == fields || fields.length == 0) {
			throw new Exception("未配置字段列映射规则,无法继续解析");
		}

 		for (Field field : fields) {
			field.setAccessible(true);
			ExcelImport config = field.getAnnotation(ExcelImport.class);
			if (null == config) {
				continue;
			}
// 			fieldConfigAndFieldMap.put(field, config);
 			fieldNameAndFieldConfigMap.put(field.getName(), config);
 			fieldNameAndBeanNameMap.put(config.name(), field.getName());
		}
 		
 		for (Field field : superFields) {
			field.setAccessible(true);
			ExcelImport config = field.getAnnotation(ExcelImport.class);
			if (null == config) {
				continue;
			}
// 			fieldConfigAndFieldMap.put(field, config);
 			fieldNameAndFieldConfigMap.put(field.getName(), config);
 			fieldNameAndBeanNameMap.put(config.name(), field.getName());
		}
	}
}
