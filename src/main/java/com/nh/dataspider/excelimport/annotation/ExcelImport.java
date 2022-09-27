package com.nh.dataspider.excelimport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nh.dataspider.excelimport.constants.FieldType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelImport {
	
	/**
     * 名称
     */
    String name();
    
    /**
     * 默认值
     */
    String defaultValue() default "";
	
    /**
     * 字段类型
     */
    String fieldType() default FieldType.TYPE_STRING;
    
    /**
     * 特殊字段类型
     */
    int otherFieldType() default -1;
    
    /**
     * 编码key
     */
    String groupKey() default "";
    
    /**
     * 关联字段类型(比如人员id，关联字段是人员姓名)
     */
    String sourceCol() default "";
    
    /**
     * 关联表名
     */
    String foreignTable() default "";
    
    /**
     * 关联表比较的字段
     */
    String compareCol() default "";
    
    /**
     * 关联表要查询的字段
     */
    String searchCol() default "";
    
    /**
     * 是否必输
     */
    boolean	isRequired() default false;
    
    /**
     * 是否默认
     */
    boolean	isDefault() default false;
    
    /**
     * 是否其他类型
     */
    boolean	isOtherType() default false;
    
    /**
     * 是否编码
     */
    boolean	isGroup() default false;
    
    /**
     * 是否外键
     */
    boolean	isForeignCol() default false;
    
    /**
     * 是否唯一
     */
    boolean	isUnique() default false;
    
    /**
     * 检查外表是否存在关联
     */
    boolean	isForeignExist() default false;
}
