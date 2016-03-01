package ca.paulshin.yunatube.data.model.flickr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by paulshin on 14-12-22.
 */
public class Set implements Parcelable {
	public String id;
	public String title;
	public String description;

	protected Set(Parcel in) {
		id = in.readString();
		title = in.readString();
		description = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(description);
	}

	@SuppressWarnings("unused")
	public static final Creator<Set> CREATOR = new Creator<Set>() {
		@Override
		public Set createFromParcel(Parcel in) {
			return new Set(in);
		}

		@Override
		public Set[] newArray(int size) {
			return new Set[size];
		}
	};
}
