package com.willluongo.asbestos.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.Room;
import com.madhackerdesigns.jinder.models.Message;
import com.madhackerdesigns.jinder.models.User;

public class AsbestosWindow {

	protected Shell shlAsbestos;

	private static Campfire campfire;
	private static String SUBDOMAIN = null;
	private static String TOKEN = null;
	private static final Logger log = LogManager.getLogger(AsbestosWindow.class
			.getName());
	private Text text_messages;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		initialize();

		try {
			AsbestosWindow window = new AsbestosWindow();
			window.open();
		} catch (Exception e) {
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

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlAsbestos.open();
		shlAsbestos.layout();
		while (!shlAsbestos.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		Room room = null;
		SortedSet<User> users = null;
		SortedSet<Long> userIds = new TreeSet<Long>();
		try {
			users = campfire.users();
			room = campfire.rooms().get(2);

			for (User user : users) {
				System.out.println(users);
				System.out.println(user.name);
				userIds.add(user.id);
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		shlAsbestos = new Shell();
		shlAsbestos.setSize(450, 300);
		shlAsbestos.setText("Asbestos");

		TabFolder tabFolder = new TabFolder(shlAsbestos, SWT.NONE);
		tabFolder.setBounds(0, 0, 450, 299);

		TabItem tbtmGeneralChat = new TabItem(tabFolder, SWT.NONE);
		tbtmGeneralChat.setText(room.name);

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmGeneralChat.setControl(composite);

		text_messages = new Text(composite, SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.MULTI);
		text_messages.setBounds(0, 0, 430, 240);

		try {
			for (Message msg : room.recent()) {
				if (userIds.contains(msg.user_id))
					text_messages.append(room.user(msg.user_id).name.concat(
							": ").concat(msg.body.concat("\n")));
				else {
					text_messages.append("UNKNOWN: ".concat(msg.body.concat("\n")));

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
