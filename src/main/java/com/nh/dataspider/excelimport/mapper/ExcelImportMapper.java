package com.nh.dataspider.excelimport.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nh.dataspider.excelimport.entity.BaseEntity;
import com.nh.dataspider.excelimport.entity.UserEntity;

public interface ExcelImportMapper extends BaseMapper<BaseEntity> {
	List<String> selectByColum(@Param("tableName") String tableName, @Param("resultCol") String resultCol, @Param("compareCol") String compareCol, @Param("value") String value, @Param("user") UserEntity user);
	
	List<String> selectIdByColum(@Param("tableName") String tableName, @Param("columName") String columName, @Param("value") String value, @Param("user") UserEntity user);
    
    int selectCountByColum(@Param("tableName") String tableName, @Param("columName") String columName, @Param("value") String value, @Param("user") UserEntity user);
    
    int selectUnDelCountByColum(@Param("tableName") String tableName, @Param("columName") String columName, @Param("value") String value, @Param("user") UserEntity user);
    
    int selectListByColum(@Param("tableName") String tableName, @Param("compareCol") String compareCol, @Param("value") String value, @Param("user") UserEntity user);
    
}