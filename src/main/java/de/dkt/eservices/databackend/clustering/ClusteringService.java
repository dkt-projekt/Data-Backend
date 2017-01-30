package de.dkt.eservices.databackend.clustering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import de.dkt.eservices.databackend.common.SparqlService;
import de.dkt.eservices.databackend.semanticexploration.SemanticEntity;

@Service
public class ClusteringService {

	@Autowired
	SparqlService sparqlService;

	public String getCollectionClustering(String collectionName,String userName, int limit){
		sparqlService.setCollectionName(collectionName);
		if(limit==0){
			limit=Integer.MAX_VALUE;
		}

		HashMap<String, HashMap<SemanticEntity,Integer>> docsMap = new HashMap<String, HashMap<SemanticEntity,Integer>>();
		List<SemanticEntity> entities = new LinkedList<SemanticEntity>();

		QueryExecution qexec = null;
		try {
			qexec = sparqlService.createQueryExecution("fetch-context-entities.txt");
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				String uri = qs.get("uri").toString();
				String anchorOf = qs.get("anchorof").toString();
				String taIdentRef = qs.get("taclassref").toString();
//				String text = qs.get("text").toString();
				String anchorText = anchorOf./*replace('\n', ' ').*/replace('\'', ' ').replace('"', ' ').replace(',', '_').replaceAll("\\s+", " ");

				anchorText = anchorText.substring(0, anchorText.indexOf('^')).trim();
				HashMap<SemanticEntity,Integer> entitiesMap;
				if(docsMap.containsKey(uri)){
					entitiesMap = docsMap.get(uri);
				}
				else{
					entitiesMap = new HashMap<SemanticEntity,Integer>();
				}

				SemanticEntity se1 = new SemanticEntity(anchorText,taIdentRef);
				if (entitiesMap.containsKey(se1)) {
					entitiesMap.put(se1, entitiesMap.get(se1) + 1);
				} else {
					entitiesMap.put(se1, 1);
				}

				docsMap.put(uri, entitiesMap);

				if(!entities.contains(se1)){
					entities.add(se1);
				}
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}			
		
		if(entities.isEmpty() || docsMap.isEmpty()){
			String msg = "<div><p>There are no entities for collection ["+collectionName+"] to be clustered.</p></div>";
			return msg;
		}

		//Generate headers for the file.
		String arff = "@RELATION "+collectionName+"_Clustering\n";

		arff += "@ATTRIBUTE DOCUMENT_NAME  STRING\n";
		for (SemanticEntity se : entities) {
			arff += "@ATTRIBUTE "+se.text.replace(' ', '_')+"  NUMERIC\n";
		}
		arff += "\n";
		arff += "@DATA\n";
		Set<String> docsKeys = docsMap.keySet();
		for (String dk : docsKeys) {
			String line = dk+"";
			HashMap<SemanticEntity, Integer> eMaps = docsMap.get(dk);
			for (SemanticEntity se : entities) {
				if(eMaps.containsKey(se)){
					line += ","+eMaps.get(se);
				}
				else{
					line += ",0";
				}
			}
			arff += line+"\n";
		}
		//			System.out.println("-------------------------------");
		//			System.out.println(arff);
		//			System.out.println("-------------------------------");

		System.out.println(arff);
		HttpResponse<String> response = null;
		try {
			response = Unirest.post("https://dev.digitale-kuratierung.de/api/e-clustering/generateClusters")
					.queryString("algorithm", "kmeans")
					.queryString("language", "en")
					//.field("file", new File("/tmp/file"))
					.body(arff).asString();
		} catch (Exception e) {
			String msg = "Error at calling the clustering service for collection: "+collectionName;
			return msg;
		}
		//			System.out.println(response.getStatus());
		//			System.out.println(response.getBody());
		String result = "";
		if(response.getStatus() == 200){
			JSONObject responseJSON = new JSONObject(response.getBody());

			result += "";
			result += "<div class=\"pricing-table group\">";

			HashMap<String, HashMap<String, Double>> resultHash = new HashMap<String, HashMap<String, Double>>();

			JSONObject results = responseJSON.getJSONObject("results");
			JSONObject clusters = results.getJSONObject("clusters");
			int numberClusters = Integer.parseInt(results.get("numberClusters").toString());
			if(numberClusters==-1){
				numberClusters=1;
			}
			else{
				if(limit<numberClusters){
					numberClusters=limit;
				}
			}
			int counter = 0;
			for (Object clusterId : clusters.keySet()){
				if(limit==counter){
					break;
				}
				JSONObject cid = clusters.getJSONObject(clusterId.toString());
				JSONObject entitiesLabels = cid.getJSONObject("entities");
				HashMap<String, Double> innerMap = new HashMap<String, Double>();

				String color="personal";
				switch (counter%5) {
				case 0:
					color = "business";
					break;
				case 1:
					color = "professional";
					break;
				case 2:
					color = "holidays";
					break;
				case 3:
					color = "meeting";
					break;
				default:
					color = "personal";
					break;
				}
				result += "<div class=\"block "+color+" fl block"+numberClusters+"\">";
				result += "<h2 class=\"title\">"+clusterId.toString()+"</h2>";
				result += "<ul class=\"features\">";
				for (Object entity : entitiesLabels.keySet()){
					JSONObject jEnt = entitiesLabels.getJSONObject(entity.toString());
					Object label = jEnt.get("label");
					Object meanVal = jEnt.get("meanValue");
					innerMap.put(label.toString(), Double.parseDouble(meanVal.toString()));

					result += "<li>"+label.toString().replace('_', ' ')+"</li>";
				}
				result += "</ul>";
				result += "<div class=\"pt-footer\">";
				result += "<p><span>_ _ _ </span></p>";
				result += "</div>";
				result += "</div>";
				resultHash.put(clusterId.toString(), innerMap);
				counter++;
			}
			result += "</div>";
			return result;
		}
		else{
			String msg = "Error at generating clustering for collection: "+collectionName;
			return msg;
		}
	}

}
