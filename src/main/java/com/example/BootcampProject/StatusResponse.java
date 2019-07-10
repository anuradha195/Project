package com.example.BootcampProject;

public class StatusResponse {

	    Integer code;
	    String message;

	    public StatusResponse(Integer code, String message) {
	        this.code = code;
	        this.message = message;
	    }

	    public StatusResponse() {
	    }

	    public Integer getCode() {
	        return code;
	    }

	    public void setCode(Integer code) {
	        this.code = code;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    @Override
	    public String toString() {
	        return "StatusResponse{" +
	                "code=" + code +
	                ", message='" + message + '\'' +
	                '}';
	    }

}

