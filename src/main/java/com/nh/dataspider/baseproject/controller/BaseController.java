package com.nh.dataspider.baseproject.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nh.dataspider.baseproject.common.response.advice.NotControllerResponseAdvice;
import com.nh.dataspider.baseproject.entity.BaseEntity;

//@RestController = @Controller + ResponseBody。加上这个注解，springboot就会吧这个类当成controller进行处理，然后把所有返回的参数放到ResponseBody中
@RestController
//请求的前缀，也就是所有该Controller下的请求都需要加上/base/test的前缀
@RequestMapping("/base/test")
public class BaseController {
	
	@PostMapping("/findByVo")
	public BaseEntity findByVo(@Validated BaseEntity vo) {
		BaseEntity baseEntity = new BaseEntity();
	    BeanUtils.copyProperties(vo, baseEntity);
	    return baseEntity;
	}
	
	/**
	 * 不想返回ResultVo封装模式的，加上@NotControllerResponseAdvice注解
	 * @return
	 */
	@GetMapping("/health")
	@NotControllerResponseAdvice
	public String health() {
	   return "success";
	}
}
