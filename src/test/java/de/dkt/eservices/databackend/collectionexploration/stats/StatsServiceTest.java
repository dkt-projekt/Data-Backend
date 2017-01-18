package de.dkt.eservices.databackend.collectionexploration.stats;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

import de.dkt.eservices.databackend.TestConstants;
import eu.freme.bservices.testhelper.TestHelper;
import eu.freme.bservices.testhelper.ValidationHelper;
import eu.freme.bservices.testhelper.api.IntegrationTestSetup;
import eu.freme.common.FREMECommonConfig;


//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = {StatsConfig.class, FREMECommonConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatsServiceTest {

	@Autowired
	StatisticsService ss;
	
	TestHelper testHelper;
	ValidationHelper validationHelper;

	@Before
	public void setup() {
		ApplicationContext context = IntegrationTestSetup
				.getContext(TestConstants.pathToPackage);
		testHelper = context.getBean(TestHelper.class);
		validationHelper = context.getBean(ValidationHelper.class);
	}
	private GetRequest requestGet(String urlPart) {
		String url = testHelper.getAPIBaseUrl() + "/data-backend/"+urlPart;
		return Unirest.get(url);
	}
		


//	@Test
//	public void testFetchStatistics() throws IOException {
//		JSONObject json = ss.getEntityStats("mendelsohn");
//		assertTrue(json != null);
//		System.out.println(json);
//	}
//	
//	@Test
//	public void testGetNumberOfTriples(){
//		Integer count = ss.getNumberOfTriples();
//		System.out.println(count);
//		assertTrue(count!=null);
//	}
//	
//	@Test
//	public void testGetNumberOfContexts(){
//		Integer count = ss.getNumberOfContexts();
//		System.out.println(count);
//		assertTrue(count!=null);
//	}
}
