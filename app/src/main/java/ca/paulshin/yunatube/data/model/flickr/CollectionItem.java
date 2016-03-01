package ca.paulshin.yunatube.data.model.flickr;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulshin on 14-12-22.
 */
public class CollectionItem implements Parcelable {
	public String id;
	public String title;
	public String iconlarge;
	public String iconsmall;
	public List<Set> set = new ArrayList<Set>();

	public CollectionItem(String id, String title) {
		this.id = id;
		this.title = title;
	}

	protected CollectionItem(Parcel in) {
		id = in.readString();
		title = in.readString();
		iconlarge = in.readString();
		iconsmall = in.readString();
		in.readTypedList(set, Set.CREATOR);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(iconlarge);
		dest.writeString(iconsmall);
		dest.writeTypedList(set);
	}

	@SuppressWarnings("unused")
	public static final Creator<CollectionItem> CREATOR = new Creator<CollectionItem>() {
		@Override
		public CollectionItem createFromParcel(Parcel in) {
			return new CollectionItem(in);
		}

		@Override
		public CollectionItem[] newArray(int size) {
			return new CollectionItem[size];
		}
	};
}