package com.nh.dataspider.mytest;

import java.util.List;

import lombok.Data;

/**
 * 解析引擎
 *
 * @author YlSaaS产品组
 * @version V3.1.0
 * @copyright 上海云令智享信息技术有限公司（https://www.ylsaas.com.cn）
 * @date 2021/3/15 9:10
 */
@Data
public class ProperCond implements Cloneable {
	
	//左侧匹配的字段（目标字段）
	private String field;
	//运算关系（==，>=，<=...）
	private String symbol;
	//类型（1：匹配字段；2：自定义；3：子表配置；4：函数表达式）
    private int valueType;
    //被匹配字段的值（或者选择的字段的fielName）
    private String fieldValue;
    //筛选条件选择了子表字段的话，需要声明父节点字段
    private String parentField;
    /**
     * 数据源（本表：default，其他webhook的：对应节点的nodeId）
     */
    private String valueSource;
    
    //表达式右边是否是子表（0 否；1 是）
    private Integer rightIsSubTable;
    //表达式左边是否是子表（0 否；1 是）
    private Integer leftIsSubTable;
    
    //条件连接符（&&、||）
    private String logic;
    //前一个条件的连接符，主要用于左侧（目标表）选择了子字段的缓存
    private String preLogic;
    //条件值
    //触发条件 范围最大值
    private String fieldMaxValue;
    //触发条件 范围最小值
    private String fieldMinValue;
    
    //子表的配置集合
    private List<ProperCond> ruleList;
    //子表配置转换后结果集
    private List<List<ProperCond>> ruleResList;
    
    //函数表达式用到
    //表达式中被选中的字段的表
    private String fieldTable;
	
	private String fieldName;
    private String symbolName;
    private String logicName;
    
    //以下属性是为了方便增加的特殊标识
    private int isFunStr;
    private String toDatabaseField;
    //添加 条件筛选时 今天的默认值
    private String dateDefault;
    
    @Override
	public ProperCond clone() {
    	ProperCond p = null;
        try {
        	p = (ProperCond) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
    }

    public ProperCond() {}

}
