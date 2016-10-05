package de.dkt.eservices.databackend.collectionexploration.authorities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.dkt.eservices.databackend.collectionexploration.authorities.model.Document;
import de.dkt.eservices.databackend.collectionexploration.authorities.model.Entity;
import eu.freme.common.exception.NotFoundException;

/**
 * @author Jan Nehring - jan.nehring@dfki.de
 */
@RestController
public class FindAuthoritiesRest {

	@Autowired
	FindAuthoritiesService findAuthoritiesService;

	/**
	 * Serve UI
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/data-backend/{collection}/authorities/ui/{file:.+}", method = RequestMethod.GET)
	public String ui(@PathVariable String file,
			@PathVariable(value = "collection") String collectionName) throws IOException {

		findAuthoritiesService.setCollection(collectionName);
		InputStream is = null;
		try {
			is = this.getClass().getClassLoader()
					.getResourceAsStream("authorities-ui/" + file);
			if (is == null) {
				throw new NotFoundException();
			}
			String str = new String(IOUtils.toByteArray(is));
			return str;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	@RequestMapping(value = "/data-backend/{collection}/authorities/api/load-contexts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadContexts(@PathVariable(value = "collection") String collectionName) throws IOException {
		findAuthoritiesService.setCollection(collectionName);
		List<Document> list = findAuthoritiesService.getAllDocuments();
		return findAuthoritiesService.documentsToJson(list);
	}

	@RequestMapping(value = "/data-backend/{collection}/authorities/api/load-authorities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadContexts(@RequestParam("text") String text,
			@PathVariable(value = "collection") String collectionName)
			throws IOException {
		findAuthoritiesService.setCollection(collectionName);
		return findAuthoritiesService.fetchAuthorativeDocuments(text)
				.toString();
	}

	@RequestMapping(value = "/data-backend/{collection}/authorities/api/load-entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String loadEntities(@RequestParam("resource") String resourceUri,
			@PathVariable(value = "collection") String collectionName)
			throws IOException {
		findAuthoritiesService.setCollection(collectionName);
		List<Entity> list = findAuthoritiesService.fetchEntities(resourceUri);
		List<JSONObject> json = new ArrayList<>();
		for (Entity e : list) {
			JSONObject obj = new JSONObject();
			obj.put("class", e.getCls());
			obj.put("text", e.getText());
			json.add(obj);
		}

		JSONArray array = new JSONArray(json);
		return array.toString();

	}
	
	@RequestMapping(value = "/data-backend/{collection}/authorities/api/load-text", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String loadText(@RequestParam("resource") String resourceUri,
			@PathVariable(value = "collection") String collectionName)
			throws IOException {
		findAuthoritiesService.setCollection(collectionName);
		String text = findAuthoritiesService.fetchText(resourceUri);
		return text;
	}
}
