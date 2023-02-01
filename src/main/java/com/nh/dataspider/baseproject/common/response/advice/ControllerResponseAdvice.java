package com.nh.dataspider.baseproject.common.response.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nh.dataspider.baseproject.common.exception.APIException;
import com.nh.dataspider.baseproject.common.response.enums.ResultCode;
import com.nh.dataspider.baseproject.common.response.model.ResultVo;

/**
 * 统一包装响应
 * @author nh
 *
 */
//不想controller返回的最后一句，都是return new ResultVo(data)，想简单一点返回一个实体之类的
//以前思路：AOP拦截所有Controller，再@After的时候统一封装一下
//springboot中提供了@RestControllerAdvice注解
//自动扫描了所有指定包下的controller，在Response时进行统一处理
@RestControllerAdvice(basePackages = {"com.nh.dataspider.baseproject"})
public class ControllerResponseAdvice implements ResponseBodyAdvice<Object>{

	/**
	 * 重写supports方法，
	 * 也就是说，当返回类型已经是ResultVo了，那就不需要封装了，当不等与ResultVo时才进行调用beforeBodyWrite方法，跟过滤器的效果是一样的
	 */
	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
		// response是ResultVo类型，或者注释了NotControllerResponseAdvice都不进行包装
		return !methodParameter.getParameterType().isAssignableFrom(ResultVo.class) || methodParameter.hasMethodAnnotation(NotControllerResponseAdvice.class);
	}

	/**
	 * 重写封装方法beforeBodyWrite，
	 * 注意除了String的返回值有点特殊，无法直接封装成json，我们需要进行特殊处理，其他的直接new ResultVo(data);
	 */
	@Override
	public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// String类型不能直接包装
		if(returnType.getParameterType().equals(String.class)) {
			ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 将数据包装在ResultVo里后转换为json串进行返回
                return objectMapper.writeValueAsString(new ResultVo(data));
            } catch (JsonProcessingException e) {
                throw new APIException(ResultCode.RESPONSE_PACK_ERROR, e.getMessage());
            }
		}
		
		return new ResultVo(data);
	}
	
}
