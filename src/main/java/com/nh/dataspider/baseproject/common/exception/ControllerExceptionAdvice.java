package com.nh.dataspider.baseproject.common.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nh.dataspider.baseproject.common.response.enums.ResultCode;
import com.nh.dataspider.baseproject.common.response.model.ResultVo;

/**
 * 异常拦截处理
 * @author nh
 *
 */
//常规思路：AOP拦截所有controller，然后异常的时候统一拦截起来，再@After的时候统一进行封装
//springboot提供了一个@RestControllerAdvice注解，来增强所有@RestController，然后用@ExceptionHandler注解，就可以拦截到对应的异常
@RestControllerAdvice
public class ControllerExceptionAdvice {
	
	/**
	 * 这边举例拦截org.springframework.validation.BindException的绑定异常
	 * 校验参数抛出的异常
	 * @return
	 */
	@ExceptionHandler({BindException.class})
	public ResultVo MethodArgumentNotValidExceptionHandler(BindException e) {
		//从异常对象中拿到ObjectError对象
		ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
		return new ResultVo(ResultCode.VALIDATE_ERROR, objectError.getDefaultMessage());
	}
	
	@ExceptionHandler(APIException.class)
    public ResultVo APIExceptionHandler(APIException e) {
        // log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着，写上TODO
        return new ResultVo(e.getCode(), e.getMsg(), e.getMessage());
    }
}
