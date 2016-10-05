package de.dkt.eservices.databackend.lists;

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
public class ListsRest {

	@Autowired
	ListsService cls;
	
	@RequestMapping(value = "/data-backend/{collection}/listDocuments", method = { RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<String> listDocumentsFromCollection(
			HttpServletRequest request, 
			@PathVariable(value = "collection") String collectionName,
			@RequestParam(value = "user", required = false) String userName,
			@RequestParam(value = "limit", required = false, defaultValue="0") int limit,
            @RequestBody(required = false) String postBody) throws Exception {
		try {
			String result = cls.listDocuments(collectionName, userName, limit);
			HttpHeaders responseHeaders = new HttpHeaders();
//			responseHeaders.add("Content-Type", RDFSerialization.JSON.name());
			return new ResponseEntity<String>(result, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}
		
}
