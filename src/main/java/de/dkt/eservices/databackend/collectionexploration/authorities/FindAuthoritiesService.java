package de.dkt.eservices.databackend.collectionexploration.authorities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.databackend.collectionexploration.authorities.model.Document;
import de.dkt.eservices.databackend.collectionexploration.authorities.model.Entity;
import de.dkt.eservices.databackend.common.SparqlService;

@Component
public class FindAuthoritiesService {

	@Autowired
	SparqlService sparqlService;

	public List<Document> getAllDocuments() throws IOException {

		QueryExecution qexec = null;
		List<Document> list = new ArrayList<>();
		try {
			qexec = sparqlService.createQueryExecution("fetch-all-contexts.txt");
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				Document doc = new Document(qs.get("uri").toString(), qs.get(
						"text").toString());
				list.add(doc);
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}

		return list;
	}

	public List<Entity> fetchEntities(String docUri) throws IOException {
		QueryExecution qexec = null;
		List<Entity> list = new ArrayList<>();
		try {
			qexec = sparqlService.createQueryExecution("fetch-entities-of-document.txt", docUri);
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.nextSolution();
				Entity entity = new Entity(qs.get("text").asLiteral().getString(), qs.get(
						"class").toString());
				list.add(entity);
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}
		return list;
	}
	
	public JSONArray fetchAuthorativeDocuments(String text) throws IOException{
		QueryExecution qexec = null;
		List<JSONObject> list = new ArrayList<>();
		try {
			qexec = sparqlService.createQueryExecution("fetch-authorative-documents.txt", text);
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				JSONObject obj = new JSONObject();
				obj.put("count", qs.get("count").asLiteral().getString());
				obj.put("doc", qs.get("doc"));
				list.add(obj);
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}

		return new JSONArray(list);
	}
	


	public String documentsToJson(List<Document> list) {
		List<JSONObject> json = new ArrayList<>();
		for (Document doc : list) {
			JSONObject obj = new JSONObject();
			obj.put("uri", doc.getUri());
			obj.put("text", doc.getText());
			json.add(obj);
		}

		JSONArray array = new JSONArray(json);
		return array.toString();
	}
	
	public String fetchText(String uri) throws IOException{
		QueryExecution qexec = null;
		try {
			qexec = sparqlService.createQueryExecution("fetch-text.txt", uri);
			ResultSet res = qexec.execSelect();
			if( res.hasNext() ){
				QuerySolution qs = res.next();
				return qs.get("text").asLiteral().getString();
			} else{
				return null;
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}
	}
	
	public void setCollection(String collectionName){
		sparqlService.setCollectionName(collectionName);
	}
}
