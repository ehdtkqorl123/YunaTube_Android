package ca.paulshin.yunatube.data.firebase;

/**
 * Created by paulshin on 2016-06-05.
 */

public class AcrosticPoem {

	private String name;
	private String text;
	private String first;
	private String second;
	private String third;
	private String fourth;
	private String fifth;
	private String sixth;
	private String photoUrl;

	public AcrosticPoem() {
	}

	public AcrosticPoem(String name, String text, String first, String second, String third, String fourth, String fifth, String sixth, String photoUrl) {
		this.name = name;
		this.text = text;
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
		this.sixth = sixth;
		this.photoUrl = photoUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getThird() {
		return third;
	}

	public void setThird(String third) {
		this.third = third;
	}

	public String getFourth() {
		return fourth;
	}

	public void setFourth(String fourth) {
		this.fourth = fourth;
	}

	public String getFifth() {
		return fifth;
	}

	public void setFifth(String fifth) {
		this.fifth = fifth;
	}

	public String getSixth() {
		return sixth;
	}

	public void setSixth(String sixth) {
		this.sixth = sixth;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}