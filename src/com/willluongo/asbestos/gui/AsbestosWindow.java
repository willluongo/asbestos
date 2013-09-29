package com.willluongo.asbestos.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TraverseEvent;

public class AsbestosWindow {

	protected Shell shlAsbestos;

	private static Campfire campfire;
	private static String SUBDOMAIN = null;
	private static String TOKEN = null;
	private static final Logger log = LogManager.getLogger(AsbestosWindow.class
			.getName());
	private Text text_messages;
	private Room room = null;
	private SortedSet<User> users = null;
	private SortedSet<Long> userIds = new TreeSet<Long>();
	private Text text;
	private Message lastMessage = null;

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

	private void updateMessages() {
		List<Message> lastUpdate = null;
		try {
			lastUpdate = room.recent();
				for (Message msg : lastUpdate) {
					log.debug(msg.created_at + " " + msg.type);
					if (msg.type.equals("TextMessage")) {
						if (lastMessage != null)
						{
							if (!(msg.equals(lastMessage)))
							{
								text_messages.append(room.user(msg.user_id).name
										.concat(": ").concat(msg.body.concat("\n")));
								lastMessage = msg;
							}
						}
						else
						{
							text_messages.append(room.user(msg.user_id).name
									.concat(": ").concat(msg.body.concat("\n")));
							lastMessage = msg;
						}

					}
				}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shlAsbestos.open();
		shlAsbestos.layout();
		Runnable timer = new Runnable(){

			@Override
			public void run() {
				updateMessages();
				display.timerExec(1000, this);
				

			}
			
		};
		display.timerExec(1000, timer);
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

		try {
			users = campfire.users();
			log.debug(users);
			room = campfire.rooms().get(2);

			for (User user : users) {

				log.debug(user.name);
				log.debug(user.id);
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
		text_messages.setBounds(0, 0, 430, 217);

		text = new Text(composite, SWT.BORDER);
		text.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				try {
					room.speak(text.getText());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				text.setText("");
				updateMessages();
			}
		});
		text.setBounds(0, 223, 430, 19);
		updateMessages();

	}
}
