package com.willluongo.asbestos.gui;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import com.madhackerdesigns.jinder.Room;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

public class RoomSelector extends Dialog {

	protected ArrayList<Room> result = new ArrayList<Room>();
	protected Shell shell;
	private ArrayList<Room> rooms;


	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public RoomSelector(Shell parent, int style, java.util.List<Room> rooms) {
		super(parent, style);
		this.rooms = (ArrayList<Room>) rooms;
		setText("Select Rooms");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public ArrayList<Room> open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());

		final List list = new List(shell, SWT.BORDER | SWT.MULTI);
		list.setBounds(10, 25, 430, 225);
		for (Room room: rooms)
		{
			list.add(room.name);
		}


		Button btnSelect = new Button(shell, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] selected = list.getSelectionIndices();
				for (int i : selected){
					result.add(rooms.get(i));
				}
					shell.close();
			}
		});
		btnSelect.setBounds(299, 250, 94, 28);
		btnSelect.setText("Select");

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(64, 250, 94, 28);
		btnCancel.setText("Cancel");
		
		Label lblSelectRooms = new Label(shell, SWT.CENTER);
		lblSelectRooms.setBounds(187, 5, 89, 14);
		lblSelectRooms.setText("Select Rooms");

	}
}
