package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class InboxController extends ListManager<InboxMessage>{

	private JPanel list, fullMessage;
	private JButton deleteSelected;
	private JLabel fullMessageHeader;
	private JTextArea fullMessageText;
	
	public InboxController(ArrayList<InboxMessage> messages, JPanel list, JPanel fullMessage){
		super(messages);
		
		this.list = list;
		this.fullMessage = fullMessage;
		
		this.initFullMessageArea();
		
		this.viewAll();
	}
	
	public void initFullMessageArea(){
		this.fullMessageHeader = new JLabel();
		this.fullMessageText = new JTextArea();
		this.fullMessageText.setEditable(false);
		
		JScrollPane scrollpane = new JScrollPane(this.fullMessageText, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.fullMessage.add(this.fullMessageHeader, BorderLayout.NORTH);
		this.fullMessage.add(scrollpane, BorderLayout.CENTER);
	}
	
	@Override
	public ArrayList<InboxMessage> viewAll(){		
		// create control panel
		JPanel controlPanel = new JPanel();
		this.deleteSelected = new JButton("Delete Selected");
		controlPanel.add(this.deleteSelected);
		this.deleteSelected.addActionListener(this);
		
		this.list.add(controlPanel, BorderLayout.NORTH);
		
		// setup table
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dataTable.setRowSelectionAllowed(true);
		this.dataTable.setColumnSelectionAllowed(false);
		this.dataTable.setAutoCreateRowSorter(true);
		//this.dataTable.getModel().addTableModelListener(this);
		this.dataTable.getSelectionModel().addListSelectionListener(this);
		
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.list.add(this.scrollpane, BorderLayout.CENTER);

		return this.getData();
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
		
		try{
			index = this.dataTable.getRowSorter().convertRowIndexToModel(
					this.dataTable.getSelectionModel().getLeadSelectionIndex());
		} catch (IndexOutOfBoundsException e){;
			this.fullMessageHeader.setText("");
			this.fullMessageText.setText("");
			this.fullMessage.revalidate();
		}
		
		InboxMessage message = this.getData().get(index);
		this.fullMessageHeader.setText("From: " + message.sender + "    Date: " + message.date.toString());
		this.fullMessageText.setText(message.message);
		
		this.fullMessage.revalidate();
	}
}
