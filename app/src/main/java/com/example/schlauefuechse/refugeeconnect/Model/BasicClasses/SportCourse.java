package com.example.schlauefuechse.refugeeconnect.Model.BasicClasses;


import java.io.Serializable;

public class SportCourse implements Serializable {

	private String type;
	private String group;
	private String tele;
	private String mail;
	private String address;
	private String days;
	private String times;
	private String lang;
	private boolean favorit;
	private int id;

	public SportCourse(String type, String group, String tele,
			String mail, String address, String days, String times, String lang, int id) {
		super();
		this.type = type;
		this.group = group;
		this.tele = tele;
		this.mail = mail;
		this.address = address;
		this.days = days;
		this.times = times;
		this.lang = lang;
		this.favorit = false;
		this.id = id;
	}
	
	public SportCourse () {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isFavorit() {
		return favorit;
	}

	public void setFavorit(boolean favorit) {
		this.favorit = favorit;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SportCourse{" +
				"type='" + type + '\'' +
				", group='" + group + '\'' +
				", tele='" + tele + '\'' +
				", mail='" + mail + '\'' +
				", address='" + address + '\'' +
				", days='" + days + '\'' +
				", times='" + times + '\'' +
				", lang='" + lang + '\'' +
				", favorit=" + favorit +
				", id=" + id +
				'}';
	}
}
