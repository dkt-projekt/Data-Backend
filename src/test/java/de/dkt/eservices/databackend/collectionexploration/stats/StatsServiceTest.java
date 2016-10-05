package de.dkt.eservices.databackend.collectionexploration.stats;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.freme.common.FREMECommonConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {StatsConfig.class, FREMECommonConfig.class})
public class StatsServiceTest {

	@Autowired
	StatisticsService ss;
	
	@Test
	public void testFetchStatistics() throws IOException {
		JSONObject json = ss.getEntityStats("mendelsohn");
		assertTrue(json != null);
		System.out.println(json);
	}
	
	@Test
	public void testGetNumberOfTriples(){
		Integer count = ss.getNumberOfTriples();
		System.out.println(count);
		assertTrue(count!=null);
	}
	
	@Test
	public void testGetNumberOfContexts(){
		Integer count = ss.getNumberOfContexts();
		System.out.println(count);
		assertTrue(count!=null);
	}
}
