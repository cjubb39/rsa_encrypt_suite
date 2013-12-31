package client;

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
import javax.swing.table.TableColumnModel;

public class InboxController extends ListManager<InboxMessage> {

	private JPanel list, fullMessage;
	private JMenuItem deleteSelected;
	private JLabel fullMessageHeader;
	private JTextArea fullMessageText;
	
	public InboxController(ArrayList<InboxMessage> messages, JPanel list, 
			JPanel fullMessage, JMenuItem deleteSelected){
		super(messages);
		this.deleteSelected = deleteSelected;
		this.list = list;
		this.fullMessage = fullMessage;
		
		this.initFullMessageArea();
		
		this.viewAll();
	}
	
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
	
	@Override
	public ArrayList<InboxMessage> viewAll(){		
		this.deleteSelected.addActionListener(this);
		
		// setup table
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dataTable.setRowSelectionAllowed(true);
		this.dataTable.setColumnSelectionAllowed(false);
		this.dataTable.setAutoCreateRowSorter(true);
		//this.dataTable.getModel().addTableModelListener(this);
		this.dataTable.getSelectionModel().addListSelectionListener(this);
		this.dataTable.setDefaultRenderer(new Date().getClass(), this.cellRenderer);
		//this.resetCellRenderers();
		
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.list.add(this.scrollpane, BorderLayout.CENTER);

		this.resetColumnWidths();
		this.sortByDateColumn();
		
		return this.getData();
	}
	
	@Override
	public void resetTable(){
		this.dataTable.setVisible(false);
		super.resetTable();
		this.sortByDateColumn();
		this.dataTable.setVisible(true);
	}
	
	private void sortByDateColumn(){
		TableColumnModel colMod = this.dataTable.getColumnModel();
		int dateColNum = -1;
		for (int i = 0; i < colMod.getColumnCount(); i++){
			if (colMod.getColumn(i).getHeaderValue().getClass() == new Date().getClass()){
				dateColNum = i;
			}
		}
	
		dateColNum = 2; //TODO
		List<RowSorter.SortKey> tmp = new CopyOnWriteArrayList<RowSorter.SortKey>();
		tmp.add(new RowSorter.SortKey(dateColNum, SortOrder.DESCENDING));
		this.dataTable.getRowSorter().setSortKeys(tmp);
	}
	
	@Override
	//TODO load message from file into inbox
	public ArrayList<InboxMessage> addOne() {
		this.resetTable();
		return this.getData();
	}
	
	public ArrayList<InboxMessage> addOne(InboxMessage in){
		this.getData().add(in);
		this.resetTable();
		return this.getData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.deleteSelected)){
			this.deleteSelected();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {	
		//if (arg0.getValueIsAdjusting()) return;
		
		int index = 0;
		InboxMessage message;
		try{
			index = this.dataTable.getRowSorter().convertRowIndexToModel(
					this.dataTable.getSelectionModel().getLeadSelectionIndex());
			message = this.getData().get(index);
		} catch (IndexOutOfBoundsException e){
			this.fullMessageHeader.setText("");
			this.fullMessageText.setText("");
			this.fullMessage.revalidate();
			return;
		}
		
		this.fullMessageHeader.setText("From: " + message.sender + "    Date: " + message.date.toString());
		this.fullMessageText.setText(message.message);
		
		this.fullMessage.revalidate();
	}
}
