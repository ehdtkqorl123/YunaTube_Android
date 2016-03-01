package ca.paulshin.yunatube.data.model.message;

/**
 * Created by paulshin on 14-12-05.
 */
public class Message {
	public String id;
	public String username;
	public String message;
	public String time;
	public String device;
	public String report;

	public Message(String id, String username, String message, String time, String device, String report) {
		this.id = id;
		this.username = username;
		this.message = message;
		this.time = time;
		this.device = device;
		this.report = report;
	}
}
