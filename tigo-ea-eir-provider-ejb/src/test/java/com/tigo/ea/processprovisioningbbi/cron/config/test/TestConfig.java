package com.tigo.ea.processprovisioningbbi.cron.config.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tigo.ea.eir.provider.config.EirConfig;

@ActiveProfiles("standalone")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={EirConfig.class})
public class TestConfig {

	@Test
	@Ignore
	public void testInitialize() {
		Assert.assertTrue(true);
	}
	
	@Test
	public void testCron() {
		
		while (true) {
			
			
		}
		
	}
	
}


