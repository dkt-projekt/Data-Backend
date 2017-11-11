package de.dkt.eservices.databackend.collectionmanagement;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.conversion.rdf.RDFConversionService;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.rest.BaseRestController;
import eu.freme.common.rest.NIFParameterSet;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de
 *
 *
 */
@RestController
public class CollectionManagementRest extends BaseRestController{

	Logger logger = Logger.getLogger(CollectionManagementRest.class);

	@Autowired
	CollectionManagementService cms;
	
	@Autowired
	RDFConversionService rdfConversionService;
	
	@Value("${dkt.services.baseUrl:https://dev.digitale-kuratierung.de/api}")
	String baseURL;

	@Value("${dkt.services.temporal-document-storage:http:/opt/data/tmp/}")
	String filesFolder;

	@RequestMapping(value = "/data-backend/listCollections", method = { RequestMethod.GET })
 	public ResponseEntity<String> listCollections(
			HttpServletRequest request, 
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "limit", required = false, defaultValue="0") int limit,
			@RequestBody(required = false) String postBody) throws Exception {
		try {
			HttpResponse<String> response = Unirest.get(baseURL+"/document-storage/collections").asString();
			return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getStatus()));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/data-backend/createCollection", method = { RequestMethod.POST })
	public ResponseEntity<String> createCollection(
			HttpServletRequest request, 
			@RequestParam(value = "collectionName", required = false) String collectionName,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "pipeline", required = false) int pipeline,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String result = "";
			boolean collectionId = cms.createCollection(collectionName, description, user, pipeline);
			if(collectionId){//priv, sUsers, sPasswords)){
				result = "The collection "+collectionName+" [with Id="+collectionId+"] has been successfully created!!";
			}
			else{
				result = "The collection "+collectionName+" has NOT been created. The process has failed!!!!";
			}
			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.PLAINTEXT.name());
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@RequestMapping(value = "/data-backend/{collectionName}", method = { RequestMethod.DELETE })
	public ResponseEntity<String> deleteCollection(
			HttpServletRequest request, 
			@PathVariable(value = "collectionName") String collectionName,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String result = "";
			if(cms.deleteCollection(collectionName)){//priv, sUsers, sPasswords)){
				result = "The collection "+collectionName+" has been successfully deleted!!";
			}
			else{
				result = "The collection "+collectionName+" has NOT been deleted. The process has failed!!!!";
			}
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	

	@RequestMapping(value = "/data-backend/{collection}/documents", method = { RequestMethod.POST })
	public ResponseEntity<String> addDocumentToCollection(
			HttpServletRequest request, 
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@PathVariable(value = "collection") String collectionName,
			@RequestParam(value = "documentName", required = false) String documentName,
			@RequestParam(value = "fileName", required = false) String fileName,
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "pipeline", required = false, defaultValue="1") int pipeline,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
//			InputStream is = new FileInputStream(filesFolder + fileName);
			
			//System.out.println(fileName);
			logger.info("FILENAME: "+fileName);
			logger.info("DOCNAME: "+documentName);
			
			InputStream is = new FileInputStream(fileName);
			byte[] contentArray = IOUtils.toByteArray(is);

			String result = "";
			boolean documentId = cms.addDocumentToCollection(collectionName, user, documentName, fileName, contentArray, pipeline, contentTypeHeader);
			if(documentId){
				result = "The document "+documentName+" has been successfully created!!";
			}
			else{
				result = "The document "+documentName+" has NOT been created. The process has failed!!!!";
			}
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	
	@RequestMapping(value = "/data-backend/{collection}/documents", method = { RequestMethod.GET })
	public ResponseEntity<String> getDocument(
			HttpServletRequest request, 
			@PathVariable(value = "collection") String collectionName,
			@RequestParam(value = "documentName") String documentName,
			@RequestParam(value = "highlightedContent", defaultValue="false") boolean highlighted,
			@RequestParam(value = "user", required=false) String user,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String jsonString = cms.getDocument(documentName, collectionName, user, highlighted);
			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.JSON.name());
			return new ResponseEntity<String>(jsonString, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}	

	
	@RequestMapping(value = "/data-backend/{collection}/documents", method = { RequestMethod.PUT })
	public ResponseEntity<String> updateDocument(
			HttpServletRequest request, 
			@RequestParam(value = "input", required = false) String input,
			@RequestParam(value = "i", required = false) String i,
			@RequestParam(value = "informat", required = false) String informat,
			@RequestParam(value = "f", required = false) String f,
			@RequestParam(value = "outformat", required = false) String outformat,
			@RequestParam(value = "o", required = false) String o,
			@RequestParam(value = "prefix", required = false) String prefix,
			@RequestParam(value = "p", required = false) String p,
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,

			@RequestParam(value = "language", required = false) String language,

			@PathVariable(value = "collection") String collectionName,
			@RequestParam(value = "document") String documentName,
			@RequestParam(value = "documentDescription", required = false) String documentDescription,
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "path", required = false) String path,
			@RequestParam(value = "analysis", required = false) String analysis,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			if(input==null){
				input=postBody;
				if(input==null){
	            	logger.error("No text to process.");
	                throw new BadRequestException("No text to process.");
				}
			}
	        NIFParameterSet nifParameters = this.normalizeNif(input, informat, outformat, postBody, acceptHeader, contentTypeHeader, prefix);
	        Model inModel = ModelFactory.createDefaultModel();

	        if (nifParameters.getInformat().equals(RDFConstants.RDFSerialization.PLAINTEXT)) {
	            rdfConversionService.plaintextToRDF(inModel, nifParameters.getInput(),language, nifParameters.getPrefix());
	        } else {
	            inModel = rdfConversionService.unserializeRDF(nifParameters.getInput(), nifParameters.getInformat());
	        }
	        
			String result = "";
			boolean done = cms.updateDocument(collectionName, user, documentName, documentDescription, 
					contentTypeHeader, rdfConversionService.serializeRDF(inModel, RDFSerialization.TURTLE), analysis);
			if(done){
				result = "The document "+documentName+" has been successfully updated!!";
			}
			else{
				result = "The document "+documentName+" has NOT been updated. The process has failed!!!!";
			}
			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.PLAINTEXT.name());
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/data-backend/{collectionName}/documents", method = { RequestMethod.DELETE })
	public ResponseEntity<String> deleteDocument(
			HttpServletRequest request, 
			@RequestHeader(value = "Accept", required = false) String acceptHeader,
			@RequestHeader(value = "Content-Type", required = false) String contentTypeHeader,
			@PathVariable(value = "collectionName") String collectionName,
			@RequestParam(value = "documentName") String documentName,
			@RequestParam(value = "user", required = false) String user,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String result = null;
			cms.deleteDocument(collectionName,documentName,user);
			result = "The document "+documentName+" has been successfully deleted.";
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		HttpResponse<String> response = Unirest.get("https://dev.digitale-kuratierung.de/api/data-backend/listCollections").
				header("Content-Type", "application/zip").
				queryString("fileName", "randomrandom.zip").
				queryString("pipeline", 4).
				asString();
		System.out.println("BBBBOOOODDDDDYYYY: "+response.getBody());
//		String expectedDocuments = "{\"models\":{\"model4\":{\"modelName\":\"temp_en\",\"models\":\"englishDates\",\"informat\":\"turtle\",\"modelId\":4,\"language\":\"en\",\"modelType\":\"timex\",\"analysis\":\"temp\",\"outformat\":\"turtle\",\"url\":\"/e-nlp/namedEntityRecognition\"},\"model3\":{\"mode\":\"all\",\"modelName\":\"ner_PER_ORG_LOC_en_all\",\"models\":\"ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC\",\"informat\":\"turtle\",\"modelId\":3,\"language\":\"en\",\"modelType\":\"ner\",\"analysis\":\"ner\",\"outformat\":\"turtle\",\"url\":\"/e-nlp/namedEntityRecognition\"},\"model2\":{\"mode\":\"link\",\"modelName\":\"ner_PER_ORG_LOC_en_link\",\"models\":\"ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC\",\"informat\":\"turtle\",\"modelId\":2,\"language\":\"en\",\"modelType\":\"ner\",\"analysis\":\"ner\",\"outformat\":\"turtle\",\"url\":\"/e-nlp/namedEntityRecognition\"},\"model1\":{\"mode\":\"spot\",\"modelName\":\"ner_PER_ORG_LOC_en_spot\",\"models\":\"ner-wikinerEn_PER;ner-wikinerEn_ORG;ner-wikinerEn_LOC\",\"informat\":\"turtle\",\"modelId\":1,\"language\":\"en\",\"modelType\":\"ner\",\"analysis\":\"ner\",\"outformat\":\"turtle\",\"url\":\"/e-nlp/namedEntityRecognition\"}}}";
//		Assert.assertEquals(expectedDocuments,response.getBody());

	}
	
}
