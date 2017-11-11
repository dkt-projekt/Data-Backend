package de.dkt.eservices.databackend.entitymap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.databackend.common.SparqlService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;

@Service
public class EntityMapService {

	Logger logger = Logger.getLogger(EntityMapService.class);

	@Autowired
	SparqlService sparqlService;

	public String getCollectionEntityMap(String collectionName, String userName, int limit){
		sparqlService.setCollectionName(collectionName);
		JSONObject document = new JSONObject();

		ArrayList<String> documentsUriList = new ArrayList<String>();
		ArrayList<String> documentsList = new ArrayList<String>();
		ArrayList<String> entitiesList = new ArrayList<String>();
		ArrayList<String> temporalExpressionsList = new ArrayList<String>();
		
		HashMap<String, ArrayList<String>> documents2Entities= new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> documents2Timex= new HashMap<String, ArrayList<String>>();
//		HashMap<String, HashMap<String,ArrayList<Integer>>> partners2Technologien= new HashMap<String, HashMap<String,ArrayList<Integer>>>();
//		HashMap<String, HashMap<String,ArrayList<Integer>>> partners2Demos = new HashMap<String, HashMap<String,ArrayList<Integer>>>();

		documentsUriList = listDocuments(collectionName, userName, limit);
		for (String documentContext : documentsUriList) {
			QueryExecution qexec = null;
			String contextUrl = documentContext;
			Model m = NIFWriter.initializeOutputModel();
			try {
				qexec = sparqlService.createQueryExecution("fetch-context-properties.txt",contextUrl);
				Resource r = m.createResource(contextUrl);
				ResultSet res2 = qexec.execSelect();
				while (res2.hasNext()) {
					QuerySolution qs2 = res2.next();
					Property pro = m.createProperty(qs2.get("p").toString());
					m.add(r, pro, qs2.get("o"));
				}
			} finally {
				if (qexec != null) {
					qexec.close();
				}
			}
			try {
				qexec = sparqlService.createQueryExecution("fetch-context-entities2.txt",contextUrl);
				ResultSet res3 = qexec.execSelect();
				while (res3.hasNext()) {
					QuerySolution qs3 = res3.next();
					Resource resource = m.createResource(qs3.getResource("s").toString());
					Property pro = m.createProperty(qs3.get("p").toString());
					m.add(resource, pro, qs3.get("o"));
				}
			} finally {
				if (qexec != null) {
					qexec.close();
				}
			}
			//String annotatedContent = NIFReader.model2String(m, RDFSerialization.TURTLE);

			Map<String, Map<String,String>> map = NIFReader.extractEntitiesExtended(m);
			Set<String> mapkeys = map.keySet();
			for (String mapkey : mapkeys) {
//				System.out.println(mapkey);
				Set<String> entitykeys = map.get(mapkey).keySet();
				String type = "";
				String anchor = "";
				//	http://www.w3.org/2006/time#intervalStarts  1929-01-03T00:00:00+01:00
				for (String entitykey : entitykeys) {
//					System.out.println("\t"+entitykey + "  " + map.get(mapkey).get(entitykey));
					if(entitykey.equalsIgnoreCase("http://www.w3.org/2005/11/its/rdf#taClassRef")){
						type = map.get(mapkey).get(entitykey);
					}
					if(entitykey.equalsIgnoreCase("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf")){
						anchor = map.get(mapkey).get(entitykey);
					}
				}
				if(!type.contains("TemporalEntity")){
					if(!entitiesList.contains(anchor)){
						entitiesList.add(anchor);
					}
					if(!documents2Entities.containsKey(documentContext)){
						documents2Entities.put(documentContext, new ArrayList<String>());
					}
					if(!documents2Entities.get(documentContext).contains(anchor)){
						documents2Entities.get(documentContext).add(anchor);
					}
				}
				else{
					if(!temporalExpressionsList.contains(anchor)){
						temporalExpressionsList.add(anchor);
					}
					if(!documents2Timex.containsKey(documentContext)){
						documents2Timex.put(documentContext, new ArrayList<String>());
					}
					if(!documents2Timex.get(documentContext).contains(anchor)){
						documents2Timex.get(documentContext).add(anchor);
					}
				}				
			}
		}

		System.out.println("-------------------");
		System.out.println("-------------------");
		System.out.println("-------------------");

		ArrayList<String> elements = new ArrayList<String>();
		elements.addAll(documentsUriList);
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		elements.addAll(entitiesList);
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		elements.addAll(temporalExpressionsList);
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		elements.add("––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		String packageNames = "";
		for (String string : elements) {
			if(string.contains("/") && string.contains("#")){
				string = string.substring(string.lastIndexOf('/')+1, string.indexOf('#'));
			}
			packageNames += ", '"+string.replace("\r\n", " ")+"'"; 
		}
		packageNames = packageNames.substring(1); 

		int matrix[][] = new int[elements.size()][elements.size()];

//		System.out.println("Documents: ");
//		for (String string : documentsUriList) {
//			System.out.println("\t"+string); 
//		}
//		System.out.println("Entities: ");
//		for (String string : entitiesList) {
//			System.out.println("\t"+string); 
//		}
//		System.out.println("Timex: ");
//		for (String string : temporalExpressionsList) {
//			System.out.println("\t"+string); 
//		}
//		System.out.println("-------------------");
//		System.out.println("-------------------");
//		System.out.println("-------------------");
//
//		System.out.println("Documents2Entities: ");
//		Set<String> d2e = documents2Entities.keySet();
//		for (String string : d2e) {
//			System.out.println("\t"+string);
//			for (String string2 : documents2Entities.get(string)) {
//				System.out.println("\t\t"+string2);
//			}
//		}
//		System.out.println("Documents2Timex: ");
//		Set<String> d2t = documents2Timex.keySet();
//		for (String string : d2t) {
//			System.out.println("\t"+string);
//			for (String string2 : documents2Timex.get(string)) {
//				System.out.println("\t\t"+string2);
//			}
//		}
//		
//		
//		System.out.println("-------------------");
//		System.out.println("-------------------");
//		System.out.println("-------------------");
		Set<String> keysDocuments = documents2Entities.keySet();
		int counter = 0;
		for (String key : keysDocuments) {
			System.out.println(key);
			counter++;
			ArrayList<String> entities = documents2Entities.get(key);
			if(entities!=null){
				for (int i = 0; i < entities.size(); i++) {
					System.out.println(entities.get(i));
					int position1 = elements.indexOf(entities.get(i));
					int position2 = elements.indexOf(key);
					matrix[position1][position2] = 1;
				}
			}
			ArrayList<String> timexes = documents2Timex.get(key);
			if(timexes!=null){
				for (int i = 0; i < timexes.size(); i++) {
					int position1 = elements.indexOf(timexes.get(i));
					int position2 = elements.indexOf(key);
					matrix[position1][position2] = 1;
				}
			}
		}
		
		String sMatrix = "";
		for (int i = 0; i < matrix.length; i++) {
			sMatrix += "[";
			for (int j = 0; j < matrix[i].length; j++) {
				sMatrix += matrix[i][j];
				if(j+1<matrix[i].length){
					sMatrix += ", ";
				}
			}
			sMatrix += "]";
			if(i+1<matrix.length){
				sMatrix += ",";
			}
			sMatrix += "\n";
		}
		String output = "{packageNames: ["+packageNames+"],matrix: ["+sMatrix+"]}";	
		return output;

//		if(limit==0){
//			limit = Integer.MAX_VALUE;
//		}
//		int counter=0;
//		sparqlService.setCollectionName(collectionName);
//
//		QueryExecution qexec = null;
//		try {
//			float meanLat = 0;
//			float meanLong = 0;
//			String output2 = "";
//			qexec = sparqlService.createQueryExecution("fetch-context-geolocations.txt");
//			ResultSet res = qexec.execSelect();
//			while (res.hasNext()) {
//				QuerySolution qs = res.next();
//				String uri = qs.get("uri").toString();
//				float latitude = qs.get("latitude").asLiteral().getFloat();
//				float longitude = qs.get("longitude").asLiteral().getFloat();
//
//				System.out.println(latitude + "--- " + longitude);
//
//				if( counter<limit ){
//					meanLat += latitude;
//					meanLong += longitude;
//					counter++;
//					output2 += "L.marker(["+latitude+", "+longitude+"]).addTo(mymap)";
//					output2 += "    .bindPopup('"+uri+"')";
//					//			            output += "    //.openPopup();";
//					output2 += "        ;";
//				}
//			}
//			String output="";
//			if(counter==0){
//				output = "There are no entities with geographical information, therefore no map can be shown.";
//			}
//			else{
//				meanLat = meanLat/counter;
//				meanLong = meanLong/counter;
//				output = ""
//						+ "<div class=\"container2\">"
//						+ "<div id=\"map-place\" style=\"width: 100%;height: 400px;margin: 0;padding: 1px;\"></div>"
//						+ "</div>"
//						+ "<script type=\"text/javascript\">"
//						+ "var mymap = L.map('map-place').setView(["+meanLat+", "+meanLong+"], 8);"
//
//		            + "L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {"
//		            + "    attribution: '&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors'"
//		            + "}).addTo(mymap);";
//
//				output += output2;
//				output += "</script>";
//			}
//			return output;
//		}
//		catch(Exception e){
//			logger.error(e.getMessage(), e);
//			return e.getMessage();
//		}	
	}

	public ArrayList<String> listDocuments(String collectionName,String user,int limit){
		if(limit==0){
			limit=Integer.MAX_VALUE;
		}
		sparqlService.setCollectionName(collectionName);
		
		ArrayList<String> docs = new ArrayList<String>();
		
		QueryExecution qexec = null;
		try {
			qexec = sparqlService.createQueryExecution("fetch-all-contexts.txt");
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				String contextUrl = qs.get("uri").toString();
				System.out.println("URI: "+contextUrl);
				Model m = NIFWriter.initializeOutputModel();
				try {
					qexec = sparqlService.createQueryExecution("fetch-context-properties.txt",contextUrl);
					Resource r = m.createResource(contextUrl);
					ResultSet res2 = qexec.execSelect();
					while (res2.hasNext()) {
						QuerySolution qs2 = res2.next();
						Property pro = m.createProperty(qs2.get("p").toString());
						m.add(r, pro, qs2.get("o"));
					}
				} finally {
					if (qexec != null) {
						qexec.close();
					}
				}
				try {
					qexec = sparqlService.createQueryExecution("fetch-context-entities2.txt",contextUrl);
					ResultSet res3 = qexec.execSelect();
					while (res3.hasNext()) {
						QuerySolution qs3 = res3.next();
						Resource resource = m.createResource(qs3.getResource("s").toString());
						Property pro = m.createProperty(qs3.get("p").toString());
						m.add(resource, pro, qs3.get("o"));
					}
				} finally {
					if (qexec != null) {
						qexec.close();
					}
				}
				docs.add(contextUrl);
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}			
		return docs;
	}

}
