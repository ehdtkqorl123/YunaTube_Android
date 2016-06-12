package ca.paulshin.yunatube.data.model.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paulshin on 14-11-26.
 */
public class Notice {
	@SerializedName("notice_android")
	public String notice;
	public String fact;
	@SerializedName("acrostic_text")
	public String acrosticText;
}
