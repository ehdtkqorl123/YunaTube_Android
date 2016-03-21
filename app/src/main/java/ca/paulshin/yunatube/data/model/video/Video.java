package ca.paulshin.yunatube.data.model.video;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paulshin on 14-11-29.
 */
public class Video {
	public long id;
	@SerializedName("new_order")
	public String newOrder;
	public String cid;
	public String ctitle;
	public String sid;
	public String yid;
	public String stitle;
	public String ytitle;
	public String ytid;

	public Video() {
	}

	public Video(Long id, String stitle, String ytitle, String ytid) {
		this.id = id;
		this.stitle = stitle;
		this.ytitle = ytitle;
		this.ytid = ytid;
	}
}
