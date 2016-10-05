package de.dkt.eservices.databackend.collectionexploration.authorities.model;

public class Document {

	String uri;
	String text;
	
	public Document(String uri, String text) {
		super();
		this.uri = uri;
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
