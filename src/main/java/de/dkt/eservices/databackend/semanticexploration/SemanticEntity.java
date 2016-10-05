package de.dkt.eservices.databackend.semanticexploration;

public class SemanticEntity {

	public String text;
	public String uri;
	
	public SemanticEntity(String text, String uri) {
		super();
		this.text = text;
		this.uri = uri;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SemanticEntity){
			SemanticEntity o = (SemanticEntity) obj;
			if(text.equals(o.getText())){
//				if(uri==null && o.getUri()==null){
//					return true;
//				}
//				if(uri==null || o.getUri()==null){
//					return false;
//				}
//				if(uri.equals(o.getUri())){
//					return true;
//				}
				return true;
			}
		}
		return false;
	}
}
