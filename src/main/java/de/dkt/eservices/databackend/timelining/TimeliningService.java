package de.dkt.eservices.databackend.timelining;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.databackend.common.SparqlService;

@Service
public class TimeliningService {

	@Autowired
	SparqlService sparqlService;

	public String getCollectionTimelining(String collectionName, int limit){
		try{
			if(limit==0){
				limit=Integer.MAX_VALUE;
			}
			sparqlService.setCollectionName(collectionName);
//			JSONObject mediaO = new JSONObject();
//			mediaO.put("url", "//www.flickr.com/photos/tm_10001/2310475988/");
//			mediaO.put("caption", "Whitney Houston performing on her My Love is Your Love Tour in Hamburg.");
//			mediaO.put("credit", "flickr/<a href='http://www.flickr.com/photos/tm_10001/'>tm_10001</a>");

			JSONObject mediaT = new JSONObject();
			mediaT.put("headLine", collectionName);
			mediaT.put("text", "<p>Timelining representation of the documents of the collection.</p>");

			JSONObject titleObject = new JSONObject();
//			titleObject.put("media", mediaO);
			titleObject.put("text", mediaT);
			
			JSONArray eventsArray = new JSONArray();
			int counter = 0;
			
			QueryExecution qexec = null;
			try {
				qexec = sparqlService.createQueryExecution("fetch-context-timeexpressions.txt");
				ResultSet res = qexec.execSelect();
				while (res.hasNext()) {
					QuerySolution qs = res.next();
					String uri = qs.get("uri").toString();
					String startdate = qs.get("startdate").toString();
					String enddate = qs.get("enddate").toString();
					String text = qs.get("text").toString();

//					System.out.println(uri + "--"+startdate + " -- " + enddate);
					
					String dateRange = startdate+"_"+enddate;
//					System.out.println("-------DEBUG: "+dateRange);

					if(!dateRange.contains("null") && (counter<limit) ){
						JSONObject mediaTDoc = new JSONObject();
						mediaTDoc.put("headLine", uri);
						String docText = text;
//						mediaTDoc.put("text", "<p>"+docText.substring(0, 250)+"...</p>");
						mediaTDoc.put("text", "<p>" + org.json.simple.JSONObject.escape(docText.substring(0, 250)) + "</p>");

						JSONObject mediaDDoc = new JSONObject();
						mediaDDoc.put("day",startdate.substring(8, 10));
						mediaDDoc.put("month",startdate.substring(5, 7));
						mediaDDoc.put("year",startdate.substring(0, 4));
						JSONObject docObject = new JSONObject();
//						docObject.put("media", mediaODoc);
						docObject.put("text", mediaTDoc);
						docObject.put("start_date", mediaDDoc);
						
						eventsArray.put(docObject);
						counter++;
					}
				}
			} finally {
				if (qexec != null) {
					qexec.close();
				}
			}			
			
			JSONObject obj = new JSONObject();
			obj.put("title", titleObject);
			obj.put("events", eventsArray);
			return obj.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			String msg = "Error at generating timelinig for collection: "+collectionName;
			return msg;
		}
	}

}
