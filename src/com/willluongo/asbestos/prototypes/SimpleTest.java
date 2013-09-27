package com.willluongo.asbestos.prototypes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.madhackerdesigns.jinder.Campfire;

public class SimpleTest {
	private static String SUBDOMAIN = null;
	private static String TOKEN = null;

	private static Campfire campfire;

	public static void main(String[] args) {
		initialize();

		System.out.println(campfire);

		try {
			System.out.println(campfire.rooms().get(0).name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initialize() {
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
		campfire = new Campfire(SUBDOMAIN, TOKEN);

	}

}