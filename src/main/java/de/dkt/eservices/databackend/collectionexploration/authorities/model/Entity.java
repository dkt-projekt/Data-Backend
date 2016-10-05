package de.dkt.eservices.databackend.collectionexploration.authorities.model;

public class Entity {

	String text;
	String cls;
	
	public Entity(String text, String cls) {
		super();
		this.text = text;
		this.cls = cls;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
}
