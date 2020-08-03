package com.example.schlauefuechse.refugeeconnect.Model.BasicClasses;

import java.io.Serializable;

public class Contact implements Serializable{

	private String name;
	private String type;
	private String mail;
	private String lang;
	private boolean favorit;
	private int id;

	public Contact(String name, String type, String mail, String lang, int id) {
		super();
		this.name = name;
		this.type = type;
		this.mail = mail;
		this.lang = lang;
		this.favorit = false;
		this.id = id;
	}

	public Contact(){

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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFavorit() {
		return favorit;
	}

	public void setFavorit(boolean favorit) {
		this.favorit = favorit;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public String toString() {
		return "Contact{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", mail='" + mail + '\'' +
				", lang='" + lang + '\'' +
				", favorit=" + favorit +
				", id=" + id +
				'}';
	}
}
