package com.nut.driver.app.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 文件记录的ID
	 */
	private String id;

	/**
	 * 文件存储路径
	 */
	private String path;

	/**
	 * 文件的完整路径.
	 */
	private String fullPath;

	/**
	 * 文件大小
	 */
	private long size;

	/**
	 * 文件前缀名
	 */
	private String prefix;

	/*
	 * 文件扩展名
	 */
	private String ext_name;

	/**
	 * 主文件ID
	 */
	private String masterId;

	/**
	 * 文件存放的组名
	 */
	private String group_name;

	/**
	 * 上传文件的账户ID
	 */
	private String account;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件操作信息(表示删除标记)
	 */
	private Integer isDelete;

	/**
	 * 是否可用
	 */
	private int is_used;


}
