package de.dkt.eservices.databackend.collectionexploration.autoglossary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import de.dkt.eservices.databackend.common.SparqlService;

@Service
public class AutoGlossaryService {

	@Autowired
	SparqlService ss;
	
	public HashMap<String,List<String>> fetchGlossary(String collectionName){
		ss.setCollectionName(collectionName);
		QueryExecution qexec = null;
		HashMap<String,List<String>> data = new HashMap<String, List<String>>();
		
		try{
			qexec = ss.createQueryExecution("fetch-all-entities.sparql");
			ResultSet res = qexec.execSelect();
			while(res.hasNext()){
				QuerySolution qs = res.next();
				String anchor = qs.getLiteral("anchor").getString();
				String uri = qs.getResource("uri").getURI();
				List<String> list = data.get(anchor);
				if( list == null ){
					list = new ArrayList<String>();
					data.put(anchor, list);
				}
				list.add(uri);
			}
			return data;
		} finally{
			if( qexec != null ){
				qexec.close();
			}
		}
	}
	
	public JSONObject sortGlossary(HashMap<String,List<String>> data){
		
		HashSet<Character> charSet = new HashSet<Character>();
		for( String key : data.keySet() ){
			charSet.add(key.toLowerCase().charAt(0));
		}
		ArrayList<Character> charList = new ArrayList<Character>();
//		charList.addAll(charSet);
		charList.add('a');
		charList.add('b');
		charList.add('c');
		charList.add('d');
		charList.add('e');
//		charList.addAll( new Character[]{'a', 'b', 'c'} );
		Collections.sort(charList);

		JSONObject result = new JSONObject();
		for( Character firstChar : charList ){
			ArrayList<JSONObject> list = new ArrayList<JSONObject>();
			for( String key : data.keySet() ){
				if( key.substring(0,1).toLowerCase().charAt(0) == firstChar.charValue() ){
					JSONObject json = new JSONObject();
					json.put("word", key);
					json.put("references", new JSONArray(data.get(key)));
					list.add(json);
				}
			}
			Collections.sort(list, new Comparator<JSONObject>(){

				@Override
				public int compare(JSONObject o1, JSONObject o2) {
					String word1 = o1.getString("word").toLowerCase();
					String word2 = o2.getString("word").toLowerCase();
					return word1.compareTo(word2);
				}
				
			});
			result.put("" + firstChar, new JSONArray(list));
		}
		
		return result;
	}
}
