package ca.paulshin.yunatube.data.model.video;

/**
 * Created by paulshin on 14-11-29.
 */
public class Comment {
	public String id;
	public String report;
	public String username;
	public String message;
	public String time;
	public String device;
	public String isfirst;

	public Comment(String id, String report, String username, String message, String time, String device, String isfirst) {
		this.id = id;
		this.report = report;
		this.username = username;
		this.message = message;
		this.time = time;
		this.device = device;
		this.isfirst = isfirst;
	}
}
