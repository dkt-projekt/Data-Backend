package de.dkt.eservices.databackend.timelining;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeliningRest {

	@Autowired
	TimeliningService tls;
	
	@RequestMapping(value = "/data-backend/{collection}/timelining", method = { RequestMethod.GET })
	public ResponseEntity<String> getCollectionTimelining(
			HttpServletRequest request, 
			@PathVariable(value = "collection") String collectionName,
			@RequestParam(value = "limit", required = false, defaultValue="0") int limit,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String jsonString = tls.getCollectionTimelining(collectionName, limit);
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<String>(jsonString, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}	

}
