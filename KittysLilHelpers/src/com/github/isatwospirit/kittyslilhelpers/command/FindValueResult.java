package com.github.isatwospirit.kittyslilhelpers.command;

public class FindValueResult<ResultType> {
	enum ResultCode{
		NOT_CHECKED,
		SUCCESS,
		SUCCESS_USE_DEFAULT,
		FAILED_NOT_FOUND,
		FAILED_ARGUMENT_MISMATCH,
		FAILED_ARGUMENT_MISSING
	}
	
	private ResultCode resultcode = ResultCode.NOT_CHECKED;
	private String message = "";
	private ResultType result = null;
	
	public ResultCode getResultCode(){
		return this.resultcode;
	}
	
	public ResultType getResult(){
		return this.result;
	}
	
	public String getMessage(){
		if(this.message!=null)
			return this.message;
		else
			switch(this.resultcode){
			case NOT_CHECKED: return "Not checked yet.";
			case SUCCESS: return "Found.";
			case FAILED_NOT_FOUND: return "Unable to find an object matching given criteria";
			case FAILED_ARGUMENT_MISSING: return "Missing argument.";
			default: return "Invalid result code.";
			}
			
	}
	
	public FindValueResult(ResultCode resultcode, String message, ResultType result){
		this.resultcode = resultcode;
		this.message = message;
		this.result = result;
	}
}
