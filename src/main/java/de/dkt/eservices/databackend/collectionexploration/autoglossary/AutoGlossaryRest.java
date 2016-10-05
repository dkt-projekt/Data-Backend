package de.dkt.eservices.databackend.collectionexploration.autoglossary;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoGlossaryRest {

	@Autowired
	AutoGlossaryService ags;
	
	@RequestMapping(value = "/data-backend/{collection}/glossary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getGlossary(
			@PathVariable(value = "collection") String collectionName) throws IOException {
		HashMap<String,List<String>> map = ags.fetchGlossary(collectionName);
		return ags.sortGlossary(map).toString();
	}
}
