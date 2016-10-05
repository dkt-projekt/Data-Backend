package de.dkt.eservices.databackend.semanticexploration;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.eservices.databackend.lists.ListsService;
import eu.freme.common.exception.ExternalServiceFailedException;

@Service
public class SemanticExplorationService {

	@Autowired
	ListsService listService;
	
	public String getCollectionSemanticExploration(String collectionName,String userName, int limit){
		if(limit==0){
			limit=Integer.MAX_VALUE;
		}
		
		String documents = listService.listDocuments(collectionName, userName, limit);
		JSONObject joDocuments = new JSONObject(documents);
		
		JSONArray documentsArray = joDocuments.getJSONArray("documents");
		
		try{
			List<JSONObject> joAll = new LinkedList<JSONObject>();
			int cnt = 0;
			for (int i = 0; i < documentsArray.length() && cnt<limit; i++) {
				JSONObject indObject = (JSONObject) documentsArray.get(i);
				//System.out.println("DEBUGGING progress:" + Integer.toString(cnt) + " of " + Integer.toString(docsList.size()));
				String annModel = annotateDocument(indObject.getString("nifcontent"), "coref_en");//, relationextraction_en
				//Model m = NIFReader.extractModelFromFormatString(annModel, RDFSerialization.TURTLE);
				String jsonOutput = annotateDocument(annModel, "relationextraction_en");
				//System.out.println("DEBUGGIGN jsonOutput:" + jsonOutput);
				joAll.add(new JSONObject(jsonOutput));
				cnt++;
			}
			JSONObject jsonMap = SemanticExploration.aggregateRelations(joAll);
			return jsonMap.toString(2);
		}
		catch(Exception e){
			e.printStackTrace();
			String msg = "Error at generating timelinig for collection: "+collectionName;
			return msg;
		}
	}
	
	public String annotateDocument(String aContent, String analysis) {
		String annotatedContent = aContent;
		try{
			Unirest.setTimeouts(10000, 10000000);
			HttpResponse<String> response =null;
			String type = analysis;
			if(type.equalsIgnoreCase("coref")){
				response = Unirest.post("http://dev.digitale-kuratierung.de/api/e-nlp/CoreferenceResolution")
						.queryString("informat", "turtle")
						.queryString("outformat", "turtle")
						.queryString("language", "en")
						.body(annotatedContent)
						.asString();
			}
			else if(type.equalsIgnoreCase("relExtract")){
				response = Unirest.post("http://dev.digitale-kuratierung.de/api/e-nlp/extractRelations")
						.queryString("informat", "turtle")
						.queryString("outformat", "turtle")
						.queryString("language", "en")
						.body(annotatedContent)
						.asString();
			}
			if(response.getStatus() == 200){
				annotatedContent = response.getBody();
			}
			else{
				String msg = "Error at processing the content of the document.";
				return msg;
			}
		}
		catch(Exception e){
			String msg = "Error at processing the content of the document.";
			throw new ExternalServiceFailedException(msg);
		}
		return annotatedContent;
	}

}
