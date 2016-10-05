package de.dkt.eservices.databackend.collectionexploration.authorities;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.dkt.eservices.databackend.collectionexploration.authorities.model.Document;
import de.dkt.eservices.databackend.collectionexploration.authorities.model.Entity;
import eu.freme.common.FREMECommonConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FindAuthoritiesConfiguration.class, FREMECommonConfig.class})
public class FindAuthoritiesServiceTest {

	@Autowired
	FindAuthoritiesService fas;
	
	@Test
	public void testAllGetDocuments() throws IOException {

		List<Document> docs = fas.getAllDocuments();
		assertTrue(docs.size() > 0);
	}

	@Test
	public void testFetchEntities() throws IOException {
		List<Entity> entities = fas
				.fetchEntities("http://digitale-kuratierung.de/ns/1874.txt#char=0,1168");
		assertTrue(entities.size() > 0);
	}
}
