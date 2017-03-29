package in.andonsystem.v2.dto;

public class RestError {
	private int status;
	private int code;
	private String message;
	private String devMessage;
	private String moreInfo;
	
	public RestError(int status, int code, String message, String devMessage, String moreInfo) {
		super();
		this.status = status;
		this.code = code;
		this.message = message;
		this.devMessage = devMessage;
		this.moreInfo = moreInfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDevMessage() {
		return devMessage;
	}
	public void setDevMessage(String devMessage) {
		this.devMessage = devMessage;
	}
	public String getMoreInfo() {
		return moreInfo;
	}
	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}
	
}
