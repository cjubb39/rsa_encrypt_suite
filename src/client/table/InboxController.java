package client.table;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;

import client.message.InboxMessage;

/**
 * Instantiation of ListManager<InboxMessage> used to manage messages in a user's inbox.
 * Slight modifications to superclass.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class InboxController extends ListManager<InboxMessage> {

	private static final long serialVersionUID = -5752149128185697360L;
	private JPanel list, fullMessage;
	private JMenuItem deleteSelected;
	private JLabel fullMessageHeader;
	private JTextArea fullMessageText;

	/**
	 * Constructor.
	 * 
	 * @param messages
	 *          List of messages to be in the inbox
	 * @param list
	 *          Where to build inbox list view
	 * @param fullMessage
	 *          Where to show selected message
	 * @param deleteSelected
	 *          Button to cause deletion of selected messages
	 */
	public InboxController(ArrayList<InboxMessage> messages, JPanel list, JPanel fullMessage,
			JMenuItem deleteSelected){
		super(messages);
		this.deleteSelected = deleteSelected;
		this.list = list;
		this.fullMessage = fullMessage;

		this.initFullMessageArea();

		this.viewAll();
	}

	/**
	 * Set up area where selected message will be displayed in full.
	 */
	public void initFullMessageArea(){
		this.fullMessageHeader = new JLabel();
		this.fullMessageText = new JTextArea();
		this.fullMessageText.setEditable(false);
		this.fullMessageText.setLineWrap(true);
		this.fullMessageText.setWrapStyleWord(true);

		JScrollPane scrollpane = new JScrollPane(this.fullMessageText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.fullMessage.add(this.fullMessageHeader, BorderLayout.NORTH);
		this.fullMessage.add(scrollpane, BorderLayout.CENTER);
	}

	/**
	 * View looks slightly different from suepr.viewAll() because this class uses a JPanel
	 * instead of standalone popup GUI and has action on selection
	 * 
	 * @see client.table.ListManager#viewAll()
	 */
	@Override
	public ArrayList<InboxMessage> viewAll(){
		this.deleteSelected.addActionListener(this);

		// setup table
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dataTable.setRowSelectionAllowed(true);
		this.dataTable.setColumnSelectionAllowed(false);
		this.dataTable.setAutoCreateRowSorter(true);
		this.dataTable.getSelectionModel().addListSelectionListener(this);
		this.dataTable.setDefaultRenderer(new Date().getClass(), this.cellRenderer);

		this.scrollpane = new JScrollPane(this.dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.list.add(this.scrollpane, BorderLayout.CENTER);

		this.resetColumnWidths();
		this.sortByDateColumn();

		return this.getData();
	}

	/**
	 * Overridden to add sort by date on reset
	 * 
	 * @see client.table.ListManager#resetTable()
	 */
	@Override
	public void resetTable(){
		this.dataTable.setVisible(false);
		super.resetTable();
		this.sortByDateColumn();
		this.dataTable.setVisible(true);
	}

	/**
	 * Sort messages by date (newest first)
	 */
	private void sortByDateColumn(){
		;
		if (this.getData().size() > 0) {

			// find date column
			int dateColNum = -1;
			for (int i = 0; i < this.dataTable.getColumnModel().getColumnCount(); i++) {
				if (this.dataTable.getModel().getValueAt(0, i).getClass() == new Date().getClass()) {
					dateColNum = i;
					break;
				}
			}

			// sort by this column in descending order
			List<RowSorter.SortKey> tmp = new CopyOnWriteArrayList<RowSorter.SortKey>();
			tmp.add(new RowSorter.SortKey(dateColNum, SortOrder.DESCENDING));
			this.dataTable.getRowSorter().setSortKeys(tmp);
		}
	}

	/**
	 * Resets table. Essentially a blank method as adding message from GUI is not supported.
	 * 
	 * @see client.table.ListManager#addOne()
	 */
	@Override
	public ArrayList<InboxMessage> addOne(){
		this.resetTable();
		return this.getData();
	}

	/**
	 * Resets table after adding message
	 * 
	 * @see client.table.ListManager#addOne(shared.TableData)
	 */
	public ArrayList<InboxMessage> addOne(InboxMessage in){
		this.getData().add(in);
		this.resetTable();
		return this.getData();
	}

	/**
	 * Add multiple message. With this method, we only reset the table once
	 * 
	 * @param in
	 *          List of InboxMessages to add
	 * @return Whole list of data being managed
	 */
	public ArrayList<InboxMessage> addMultiple(List<InboxMessage> in){
		for (InboxMessage im : in) {
			this.getData().add(im);
		}
		this.resetTable();
		return this.getData();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource().equals(this.deleteSelected)) {
			this.deleteSelected();
		}
	}

	/**
	 * Show all of selected message in full message area.
	 * 
	 * @param arg0
	 *          Event representing change
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0){

		int index = 0;
		InboxMessage message;
		try {
			index = this.dataTable.getRowSorter().convertRowIndexToModel(
					this.dataTable.getSelectionModel().getLeadSelectionIndex());
			message = this.getData().get(index);
		} catch (IndexOutOfBoundsException e) {
			this.fullMessageHeader.setText("");
			this.fullMessageText.setText("");
			this.fullMessage.revalidate();
			return;
		}

		this.fullMessageHeader.setText("From: " + message.sender + "    Date: "
				+ message.date.toString());
		this.fullMessageText.setText(message.message);

		this.fullMessage.revalidate();
	}
}
