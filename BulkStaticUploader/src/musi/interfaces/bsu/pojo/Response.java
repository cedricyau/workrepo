package musi.interfaces.bsu.pojo;

public class Response {
	String status;
	String description;
	
	public Response(String status, String description) {
		this.status = status;
		this.description = description;
	}	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return status;
	}
}