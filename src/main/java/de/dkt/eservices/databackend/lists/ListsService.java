package de.dkt.eservices.databackend.lists;

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
public class ListsService {

	@Autowired
	SparqlService sparqlService;

	public String listDocuments(String collectionName,String user,int limit){
		if(limit==0){
			limit=Integer.MAX_VALUE;
		}
		sparqlService.setCollectionName(collectionName);
		
		JSONObject obj = new JSONObject();
		JSONArray joDocumentsArray= new JSONArray();
		
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
				
				JSONObject joIndividualObject = new JSONObject();
				joIndividualObject.put("uri", contextUrl);
				joIndividualObject.put("nifcontent", NIFReader.model2String(m, RDFSerialization.TURTLE));
				
				joDocumentsArray.put(joIndividualObject);
			}
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}			
		obj.put("documents", joDocumentsArray);
		return obj.toString();		
	}

}
