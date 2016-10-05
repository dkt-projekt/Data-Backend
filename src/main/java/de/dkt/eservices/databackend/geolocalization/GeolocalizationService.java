package de.dkt.eservices.databackend.geolocalization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.databackend.common.SparqlService;

@Service
public class GeolocalizationService {

	@Autowired
	SparqlService sparqlService;

	public String getCollectionGeolocalization(String collectionName, String userName, int limit){
		
		if(limit==0){
			limit = Integer.MAX_VALUE;
		}
		int counter=0;
		sparqlService.setCollectionName(collectionName);

		QueryExecution qexec = null;
		try {
			float meanLat = 0;
			float meanLong = 0;
			String output2 = "";
			qexec = sparqlService.createQueryExecution("fetch-context-geolocations.txt");
			ResultSet res = qexec.execSelect();
			while (res.hasNext()) {
				QuerySolution qs = res.next();
				String uri = qs.get("uri").toString();
				String latitude = qs.get("latitude").toString();
				String longitude = qs.get("longitude").toString();

				if( counter<limit ){
					meanLat += Float.parseFloat(latitude);
					meanLong += Float.parseFloat(longitude);
					counter++;
					output2 += "L.marker(["+latitude+", "+longitude+"]).addTo(mymap)";
					output2 += "    .bindPopup('"+uri+"')";
					//			            output += "    //.openPopup();";
					output2 += "        ;";
				}
			}
			meanLat = meanLat/counter;
			meanLong = meanLong/counter;

			String output = ""
					+ "<div class=\"container2\">"
					+ "<div id=\"map-place\" style=\"width: 100%;height: 400px;margin: 0;padding: 1px;\"></div>"
					+ "</div>"
					+ "<script type=\"text/javascript\">"
					+ "var mymap = L.map('map-place').setView(["+meanLat+", "+meanLong+"], 8);"

	            + "L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {"
	            + "    attribution: '&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors'"
	            + "}).addTo(mymap);";

			output += output2;
			output += "</script>";
			return output;
		}
		catch(Exception e){
//			String msg = "Error at generating geolocalization for collection: "+collectionName;
			e.printStackTrace();
			return "";
		}	
	}

}
