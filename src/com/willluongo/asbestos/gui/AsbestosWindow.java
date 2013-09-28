package com.willluongo.asbestos.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.List;

import com.madhackerdesigns.jinder.Campfire;
import com.madhackerdesigns.jinder.models.Message;

public class AsbestosWindow {

	protected Shell shlAsbestos;
	private Text text_send;
	private Text text_messages;
	
	private static Campfire campfire;
	private static String SUBDOMAIN = null;
	private static String TOKEN = null;
	private static final Logger log = LogManager.getLogger(AsbestosWindow.class.getName());
	
	/**
	 * Launch the application.
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
		shlAsbestos = new Shell();
		shlAsbestos.setSize(450, 300);
		shlAsbestos.setText("Asbestos");
		
		text_send = new Text(shlAsbestos, SWT.BORDER);
		text_send.setBounds(0, 249, 370, 29);
		
		Button btnSend = new Button(shlAsbestos, SWT.NONE);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnSend.setBounds(376, 249, 74, 28);
		btnSend.setText("Send");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shlAsbestos, SWT.H_SCROLL);
		scrolledComposite.setBounds(0, 0, 370, 243);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		text_messages = new Text(scrolledComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		scrolledComposite.setContent(text_messages);
		scrolledComposite.setMinSize(text_messages.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		try {
			for (Message msg: campfire.rooms().get(2).recent())
			{
				text_messages.append(msg.body.concat("\n"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List list_users = new List(shlAsbestos, SWT.BORDER);
		list_users.setBounds(376, 0, 74, 243);

	}
}
