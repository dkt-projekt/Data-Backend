package de.dkt.eservices.databackend.collectionexploration.stats;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.databackend.common.SparqlService;

@Service
public class StatisticsService {

	@Autowired
	SparqlService sparqlService;

	public enum Types{
		
		Organization,
		TemporalExpression,
		Location,
		Person;
		
		public static String getUri(Types t){
			switch(t){
			case Organization:
				return "http://dbpedia.org/ontology/Organisation";
			case TemporalExpression:
				return "http://www.w3.org/2006/time#TemporalEntity";
			case Location:
				return "http://dbpedia.org/ontology/Location";
			case Person:
				return "http://dbpedia.org/ontology/Person";
			default:
				throw new RuntimeException("Unknown type");
			}
		}
	}
	
	public JSONArray countEntities(String type){
		QueryExecution qe = null;
		try{
			qe = sparqlService.createQueryExecution("count-entities.sparql", type);
			ResultSet res = qe.execSelect();
			ArrayList<JSONObject> list = new ArrayList<>();
			while( res.hasNext() ){
				QuerySolution qs = res.next();
				JSONObject json = new JSONObject();
				json.put("anchorOf", qs.getLiteral("text").getString());
				json.put("count", qs.getLiteral("count").getInt());
				list.add(json);
			}
			return new JSONArray(list);
		} finally{
			if( qe != null){
				qe.close();
			}
		}
	}
	
	public JSONObject getEntityStats(String collectionName){
		sparqlService.setCollectionName(collectionName);
		JSONObject json = new JSONObject();
		for( Types type : Types.values() ){
			String uri = Types.getUri(type);
			json.put(uri, countEntities(uri));
		}
		return json;
	}
	
	public Integer getNumberOfContexts(){
		Query query = QueryFactory.create("SELECT (COUNT(*) as ?count) WHERE { ?s a <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context> . }");
		QueryExecution qexec = null;
		try{
			qexec = sparqlService.createQueryExecution(query);
			ResultSet res = qexec.execSelect();
			if( !res.hasNext() ){
				return null;
			} else{
				return res.next().getLiteral("count").getInt();
			}
		} finally{
			if( qexec != null){
				qexec.close();
			}
		}
	}
	
	public Integer getNumberOfTriples(){
		Query query = QueryFactory.create("SELECT (COUNT(*) as ?count) WHERE { ?s ?p ?o . }");
		QueryExecution qexec = null;
		try{
			qexec = sparqlService.createQueryExecution(query);
			ResultSet res = qexec.execSelect();
			if( !res.hasNext() ){
				return null;
			} else{
				return res.next().getLiteral("count").getInt();
			}
		} finally{
			if( qexec != null){
				qexec.close();
			}
		}
	}
	public JSONObject getGeneralStats(String collectionName){
		sparqlService.setCollectionName(collectionName);
		JSONObject json = new JSONObject();
		json.put("numberOfContexts", getNumberOfContexts());
		json.put("numberOfTriples", getNumberOfTriples());
		return json;
	}
}
