package com.example.schlauefuechse.refugeeconnect.Model.BasicClasses;


import java.io.Serializable;

public class Doctor implements Serializable {

	private String name;
	private String type;
	private String gender;
	private String languages;
	private String address;
	private String tele;
	private String lang;
	private boolean favorit;
	private int id;
	
	public Doctor(String name, String type, String gender,
				  String languages, String address, String tele, String lang, int id) {
		super();
		this.name = name;
		this.type = type;
		this.gender = gender;
		this.languages = languages;
		this.address = address;
		this.tele = tele;
		this.lang = lang;
		this.id = id;
	}
	
	public Doctor() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
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
		return "Doctor{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", gender='" + gender + '\'' +
				", languages='" + languages + '\'' +
				", address='" + address + '\'' +
				", tele='" + tele + '\'' +
				", lang='" + lang + '\'' +
				", favorit=" + favorit +
				", id=" + id +
				'}';
	}
}
