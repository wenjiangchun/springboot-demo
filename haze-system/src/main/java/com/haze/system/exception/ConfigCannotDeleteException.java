package com.haze.system.exception;


import com.haze.core.service.HazeServiceException;

/**
 * 配置删除失败异常
 * @author sofar
 *
 */
public class ConfigCannotDeleteException extends HazeServiceException {

	private static final long serialVersionUID = 1L;

	public ConfigCannotDeleteException() {
		super();
	}

	public ConfigCannotDeleteException(String message) {
		super(message);
	}
	
}
