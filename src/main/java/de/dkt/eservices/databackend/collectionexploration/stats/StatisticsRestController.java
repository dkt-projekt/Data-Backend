package de.dkt.eservices.databackend.collectionexploration.stats;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.dkt.eservices.databackend.collectionexploration.authorities.model.Document;

@RestController
public class StatisticsRestController {

	@Autowired
	StatisticsService ss;
	
	@RequestMapping(value = "/data-backend/{collection}/stats/entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadEntities(			
			@PathVariable(value = "collection") String collectionName) throws IOException {
		return ss.getEntityStats(collectionName).toString();
	}	
	
	@RequestMapping(value = "/data-backend/{collection}/stats/general", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadGeneralStats(
			@PathVariable(value = "collection") String collectionName) throws IOException {
		return ss.getGeneralStats(collectionName).toString();
	}	
}
