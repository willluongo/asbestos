package com.willluongo.asbestos.prototypes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.madhackerdesigns.jinder.Campfire;

public class SimpleTest {
	private static String SUBDOMAIN = null;
	private static String TOKEN = null;
	private static final Logger log = LogManager.getLogger(SimpleTest.class.getName());

	private static Campfire campfire;

	public static void main(String[] args) {
		initialize();

		try {
			System.out.println(campfire.rooms().get(2).name);
			System.out.println(campfire.rooms().get(2).recent().get(1).body);
			System.out.println(campfire.rooms().get(2).recent().get(1).created_at);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initialize() {
		log.debug("Loading properties from file...");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("asbestos.properties"));
			SUBDOMAIN = prop.getProperty("campfire.subdomain");
			TOKEN = prop.getProperty("campfire.token");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Campfire subdomain: ".concat(SUBDOMAIN));
		log.debug("Campfire token: ".concat(TOKEN));
		campfire = new Campfire(SUBDOMAIN, TOKEN);

	}

}