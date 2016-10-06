package de.dkt.eservices.databackend.collectionmanagement;

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
			HttpResponse<String> response = Unirest.post(baseURL+"/document-storage/collections/"+collectionName+"/documents").
					header("Content-Type", contentType).
					queryString("filename", fileName).
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

	public String getDocument(String documentName, String collectionName, String user) {
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

		document.put("uri", contextUrl);
		document.put("nifcontent", NIFReader.model2String(m, RDFSerialization.TURTLE));
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

}
