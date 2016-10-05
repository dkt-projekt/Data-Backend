package de.dkt.eservices.databackend.semanticexploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.freme.common.exception.ExternalServiceFailedException;

public class SemanticExploration {

	static Logger logger = Logger.getLogger(SemanticExploration.class);


	
	public static JSONObject aggregateRelations(List<JSONObject> listJSONs) throws ExternalServiceFailedException {
		try{
			ArrayList<EntityRelationTriple> collectionRelationList = new ArrayList<EntityRelationTriple>();
			for (JSONObject j : listJSONs){
				JSONArray relationsArray = (JSONArray) j.get("relations");
				for (int i = 0; i < relationsArray.length(); i++) {
					JSONObject rel = (JSONObject) relationsArray.get(i);
					String subject = rel.getString("subject");
					String relation = rel.getString("relation");
					String object = rel.getString("object");
					EntityRelationTriple t = new EntityRelationTriple(subject,relation,object);
					collectionRelationList.add(t);
				}
			}
			HashMap<String,HashMap<String,HashMap<String,Integer>>> m = new HashMap<String,HashMap<String,HashMap<String,Integer>>>();
			for (EntityRelationTriple t : collectionRelationList){
				String subjectElem = t.getSubject() ;
				String relation = t.getRelation();
				String objectElem = t.getObject();
				if (m.containsKey(subjectElem)){
					HashMap<String, HashMap<String, Integer>> relMap = m.get(subjectElem);
					if (relMap.containsKey(relation)) {
						HashMap<String, Integer> objectMap = relMap.get(relation);
						if (objectMap.containsKey(objectElem)) {
							Integer currentObjectCount = objectMap.get(objectElem);
							objectMap.put(objectElem, currentObjectCount + 1);
						} else {
							objectMap.put(objectElem, 1);
						}
						relMap.put(relation, objectMap);
					} else {
						HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
						objectMap.put(objectElem, 1);
						relMap.put(relation, objectMap);
					}
					m.put(subjectElem, relMap);
				}
				else{
					HashMap<String, HashMap<String, Integer>> relMap = new HashMap<String, HashMap<String, Integer>>();
					HashMap<String, Integer> objectMap = new HashMap<String, Integer>();
					objectMap.put(objectElem, 1);
					relMap.put(relation, objectMap);
					m.put(subjectElem, relMap);
				}
			}
			
			JSONObject jsonMap = new JSONObject(m);
			return jsonMap;
			
		}
		catch(Exception e){
			logger.error(e.getMessage());
			throw new ExternalServiceFailedException(e.getMessage());
		}
	}

	

	public static void main(String[] args) throws Exception {

		
	}
}
