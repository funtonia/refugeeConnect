package com.example.schlauefuechse.refugeeconnect.Model.BasicClasses;

public class BonusCard {

	private String name;
	private String address;
	private String tele;
	private String mail;
	private String website;
	private boolean fc;
	private boolean th;
	private boolean bc;
	private String lang;
	private boolean favorit;
	private int id;
	
	public BonusCard(String name, String address, String tele, String mail,
			String website, boolean fc, boolean th, boolean bc, String lang, int id ) {
		super();
		this.name = name;
		this.address = address;
		this.tele = tele;
		this.mail = mail;
		this.website = website;
		this.fc = fc;
		this.th = th;
		this.bc = bc;
		this.lang = lang;
		this.id = id;
		this.favorit = false;
	}
	
	public BonusCard() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public boolean isFc() {
		return fc;
	}

	public void setFc(boolean fc) {
		this.fc = fc;
	}

	public boolean isTh() {
		return th;
	}

	public void setTh(boolean th) {
		this.th = th;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isBc() {
		return bc;
	}

	public void setBc(boolean bc) {
		this.bc = bc;
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

	@Override
	public String toString() {
		return "BonusCard{" +
				"name='" + name + '\'' +
				", address='" + address + '\'' +
				", tele='" + tele + '\'' +
				", mail='" + mail + '\'' +
				", website='" + website + '\'' +
				", fc=" + fc +
				", th=" + th +
				", lang='" + lang + '\'' +
				", favorit=" + favorit +
				", id=" + id +
				'}';
	}
}
