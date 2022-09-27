package com.nh.dataspider.excelimport.entity;

public class ExcelImportResult {

	public static enum ImportResult {
		SUCCESS("SUCCESS","新建"),EXIST("EXIST","覆盖"),FAILURE("FAILURE","失败");
    	String value;
    	String text;
    	ImportResult(String value, String text) {
    		this.value = value;
    		this.text = text;
    	}
    	
    	public String getValue() {
    		return this.value;
    	}
    	public String getText() {
    		return this.text;
    	}
	}
	
	private String operationName;
	
	private String result;
	
	private String importType;
	
	private String remark;
	
	private String data;
	
	private String showData;
	
	private boolean canImport;

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getImportType() {
		return result;
	}

	public void setImportType(String importType) {
		this.importType = result;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getShowData() {
		return showData;
	}

	public void setShowData(String showData) {
		this.showData = showData;
	}

	public boolean isCanImport() {
		return canImport;
	}

	public void setCanImport(boolean canImport) {
		this.canImport = canImport;
	}

}
