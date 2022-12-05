package com.nh.dataspider.wxwork.model;

import lombok.Data;

@Data
public class DepartmentInfo {
	
	//创建的部门id
    private Integer id;
    
    //部门名称，代开发自建应用需要管理员授权才返回；此字段从2019年12月30日起，对新创建第三方应用不再返回，2020年6月30日起，对所有历史第三方应用不再返回name，返回的name字段使用id代替，后续第三方仅通讯录应用可获取，未返回名称的情况需要通过通讯录展示组件来展示部门名称
    private String name;
    
    //英文名称，此字段从2019年12月30日起，对新创建第三方应用不再返回，2020年6月30日起，对所有历史第三方应用不再返回该字段
    private String name_en;
    
    //部门负责人的UserID；第三方仅通讯录应用可获取
    private String[] department_leader;
    
    //父部门id。根部门为1
    private Integer parentid;
    
    //在父部门中的次序值。order值大的排序靠前。值范围是[0, 2^32)
    private Integer order;
}
