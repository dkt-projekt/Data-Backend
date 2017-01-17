package de.dkt.eservices.databackend.collectionmanagement;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.databackend.common.SparqlService;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 *
 */
@Service
public class CollectionManagementService {

	Logger logger = Logger.getLogger(CollectionManagementService.class);
    
//	@Autowired
//	DocumentStorageWrapper documentStorageWrapper;

	@Autowired
	SparqlService sparqlService;

	@Value("${dkt.services.baseUrl:http://dev.digitale-kuratierung.de/api}")
	String baseURL;

	public boolean createCollection(String collectionName, String description, String user, int analysis) throws ExternalServiceFailedException {
		try{
			HttpResponse<String> response = Unirest.post(baseURL+"/document-storage/collections/"+collectionName).
					queryString("pipeline", analysis).
					asString();
			if(response.getStatus()==200){
//				HttpResponse<String> responseLucene = Unirest.post(baseURL+"/e-lucene/indexes").
//						queryString("indexName",collectionName).
//						queryString("language","en").
//						queryString("fields","all").
//						queryString("analyzers","standard").
//						queryString("overwrite",false).
//						asString();
//				//TODO Create the index in lucene.
//				
//				if(responseLucene.getStatus()==200){
//					return true;
//				}
//				else{
//					return false;
//				}
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception e){
			logger.error("Error at creating collection at document-storage services.");
			return false;
		}
	}
	
	public boolean deleteCollection(String collectionName) throws ExternalServiceFailedException {
		try{
			HttpResponse<String> response = Unirest.delete(baseURL+"/document-storage/collections/"+collectionName).asString();
			if(response.getStatus()==200){
//				HttpResponse<String> responseLucene = Unirest.delete(baseURL+"/e-lucene/indexes/"+collectionName).
//						asString();
//				if(responseLucene.getStatus()==200){
//					return true;
//				}
//				else{
//					return false;
//				}
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception e){
			logger.error("error at deleting collection from document-storage services.");
			return false;
		}
	}
	
	public boolean addDocumentToCollection(String collectionName, String user, String documentName, String fileName, byte[] content, int analysis, String contentType) {
		try{
			int pipelineId = analysis;
			System.out.println();
			HttpResponse<String> response = Unirest.post(baseURL+"/document-storage/collections/"+collectionName+"/documents").
					header("Content-Type", contentType).
					queryString("fileName", fileName).
					queryString("pipeline", pipelineId).
					body(content).
					asString();
			if(response.getStatus()==200){
				//TODO do the othe staff for the creation like lucene creation.
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception e){
			logger.error("Error at creating collection at document-storage services.");
			return false;
		}
	}

	public String getDocument(String documentName, String collectionName, String user, boolean highlighted) {
		sparqlService.setCollectionName(collectionName);
		
		JSONObject document = new JSONObject();
		
		QueryExecution qexec = null;
		String contextUrl = documentName;
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
			qexec = sparqlService.createQueryExecution("fetch-context-entities.txt",contextUrl);
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

		String annotatedContent = NIFReader.model2String(m, RDFSerialization.TURTLE);
		document.put("uri", contextUrl);
		document.put("nifcontent", annotatedContent);
		if(highlighted){
			String highlightedContent = highlighText(m);
			document.put("highcontent", highlightedContent);
		}
		return document.toString();
	}

	public boolean updateDocument(String collectionName, String user, String documentName, String documentDescription, String content, String aContent, String analysis) {

//		content = aContent;
//		String annotatedContent= annotateDocument(aContent,analysis);
////		System.out.println(annotatedContent);
//		String highlightedContent = highlighText(annotatedContent);
////		System.out.println(highlightedContent);
//		
//		boolean doc = documentStorageWrapper.updateDocument(documentName, collectionName, user, documentDescription, analysis, content, annotatedContent, highlightedContent);
//		if(!updateCollection(collectionName, collectionOverviewLimit)){
//			logger.error("The collection has not been updated!!");
//		}
//		return doc;
		
		//TODO do it connecting to DS
		
		return true;
	}
	
	public boolean deleteDocument(String collectionName, String documentName, String user) {
       	try{
			HttpResponse<String> response = Unirest.delete(baseURL+"/document-storage/collections/"+collectionName+"/"+documentName).
					queryString("", "").
					asString();
			if(response.getStatus()==200){
				return true;
			}
			else{
				return false;
			}
       	}
       	catch(Exception e){
       		return false;
       	}
	}

	public String highlighText(Model jena){
		try{
			String high = "";
			String anno = NIFReader.extractIsString(jena);
			//Get all the annotated information that we want to use for highlighting. 
			Map<String,Map<String,String>> map = NIFReader.extractEntitiesExtended(jena);
			
			LinkedList<Map<String,String>> list = new LinkedList<Map<String,String>>();

			String initTag = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#beginIndex";
			String endTag = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#endIndex";
			String typeTag = "http://www.w3.org/2005/11/its/rdf#taClassRef";

			if (map != null && !map.isEmpty()){
				Set<String> keys = map.keySet();
				for (String k : keys) {
					// System.out.println("Key: "+k);
					Map<String, String> internalMap = map.get(k);

					int init = Integer.parseInt(internalMap.get(initTag));
					// int end = Integer.parseInt(internalMap.get(endTag));
					boolean added = false;

					for (int i = 0; i < list.size() && !added; i++) {
						Map<String, String> mapL = list.get(i);
						int auxInit = Integer.parseInt(mapL.get(initTag));
						if (init < auxInit) {
							added = true;
							list.add(i, internalMap);
						}
					}

					if (!added) {
						list.add(internalMap);
					}
					// Set<String> kes2 = internalMap.keySet();
					// for (String k2 : kes2) {
					// System.out.println("\t" + k2 + " <--> " +
					// internalMap.get(k2));
					// }
				}
			}
			
			int offset = 0;
			for (Map<String,String> mm : list) {
				int init = Integer.parseInt(mm.get(initTag));
				int end = Integer.parseInt(mm.get(endTag));
				String type = mm.get(typeTag);
				String label = "";
				
				if(type.contains("Location")){
					label = "label-warning";
				}
				else if(type.contains("Organisation")){
					label = "label-info";
				}
				else if(type.contains("Person")){
					label = "label-success";
				}
				else if(type.contains("TemporalEntity")){
					label = "label-primary";
				}
				else{
					label = "label-default";
				}

//				System.out.println("\toffset: "+offset+" INIT: "+init+" END: "+end+"  type:"+type);
				
				if(offset>init){
					high = high + "(<span class=\"label "+label+"\">";
					high = high + anno.substring(init, end);
					high = high + "</span>)";

					//TODO Consider painting when the ending is longer than the previous clashing endind.
				}
				else{
					high = high + anno.substring(offset, init);
					high = high + "<span class=\"label "+label+"\">";
					high = high + anno.substring(init, end);
					high = high + "</span>";
				}
				
				offset = end;
//				Set<String> kes2 = mm.keySet();
//				for (String k2 : kes2) {
//					System.out.println("\t" + k2 + " <--> " + mm.get(k2));
//				}
			}
			high = high + anno.substring(offset);
			
			String translated = NIFReader.extractITSRDFTarget(jena);
			String language = NIFReader.extractITSRDFTargetLanguage(jena);
			if(translated!=null){
				high = high + "<div class=\"col-lg-1\"></div>";
				high = high + "<div class=\"translateText col-lg-11 col-md-5 alert alert-danger\">";
				high = high + "<span class=\"label label-default\">"+language+"</span>"+translated+"</div>";
			}
			return high;
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			System.out.println("ERROR at generating the highlighted content of the document");
			return null;
		}
	}

}
