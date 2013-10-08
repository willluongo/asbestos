package com.willluongo.asbestos.gui;

import java.beans.Beans;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
	private Message lastMessage = new Message();
	private Hashtable<Long, User> userCache = new Hashtable<Long, User>();
	// UI Elements
	private Display display = null;
	private TabItem tbtmRoom = null;
	private static final int UPDATERATE = 1000;

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
			if (lastMessage.id == null)
				lastUpdate = room.recent();
			else
				lastUpdate = room.recent(lastMessage.id);
			for (Message msg : lastUpdate) {
				log.debug(msg.created_at + " " + msg.type);
				if (msg.type.equals("TextMessage")) {
					if (!(msg.equals(lastMessage))) {
						if (!(userCache.containsKey(msg.user_id))) {
							userCache.put(msg.user_id, room.user(msg.user_id));
						}
						text_messages.append(userCache.get(msg.user_id).name
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
		Display.setAppName("Asbestos");
		display = Display.getDefault();
		
		createContents();
		shlAsbestos.open();
		shlAsbestos.layout();

		Runnable timer = new Runnable() {

			@Override
			public void run() {
				updateMessages();
				display.timerExec(UPDATERATE, this);

			}

		};
		display.timerExec(UPDATERATE, timer);
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
		shlAsbestos = new Shell();
		shlAsbestos.setSize(450, 300);
		shlAsbestos.setText("Asbestos");
		TabFolder tabFolder = new TabFolder(shlAsbestos, SWT.NONE);
		tabFolder.setBounds(0, 0, 450, 299);
		tbtmRoom = new TabItem(tabFolder, SWT.NONE);
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmRoom.setControl(composite);

		text_messages = new Text(composite, SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.MULTI);
		text_messages.setBounds(0, 0, 430, 217);

		try {
			users = campfire.users();
			log.debug(users);

			selectRooms();

			room.join();

			for (User user : users) {

				log.debug(user.name);
				log.debug(user.id);
				userIds.add(user.id);
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		text = new Text(composite, SWT.BORDER);
		text.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if ((e.detail == SWT.TRAVERSE_RETURN)
						&& (text.getText().length() > 0)) {
					try {
						room.speak(text.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					text.setText("");
					updateMessages();
				}

			}
		});
		text.setBounds(0, 223, 430, 19);

		Menu menu = new Menu(shlAsbestos, SWT.BAR);
		shlAsbestos.setMenuBar(menu);

		MenuItem mntmRooms_1 = new MenuItem(menu, SWT.CASCADE);
		mntmRooms_1.setText("Rooms");

		Menu menu_1 = new Menu(mntmRooms_1);
		mntmRooms_1.setMenu(menu_1);

		MenuItem mntmSelectRooms = new MenuItem(menu_1, SWT.NONE);
		mntmSelectRooms.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					selectRooms();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mntmSelectRooms.setText("Select room...");
		updateMessages();

	}

	private void selectRooms() throws IOException {
		if (Beans.isDesignTime()) {
			room = campfire.rooms().get(2);
		} else {
			RoomSelector select = new RoomSelector(shlAsbestos, 0,
					campfire.rooms());

			room = campfire.rooms().get((int) select.open());
		}
		tbtmRoom.setText(room.name);
		lastMessage = new Message();
		text_messages.setText("");
		updateMessages();

	}
}
