package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class InboxController extends ListManager<InboxMessage> {

	private JPanel panel;
	private JButton deleteSelected;
	
	public InboxController(ArrayList<InboxMessage> messages, JPanel panel){
		super(messages);
		
		this.panel = panel;
		this.viewAll();
	}
	
	@Override
	public ArrayList<InboxMessage> viewAll(){		
		// create control panel
		JPanel controlPanel = new JPanel();
		this.deleteSelected = new JButton("Delete Selected");
		controlPanel.add(this.deleteSelected);
		this.deleteSelected.addActionListener(this);
		
		this.panel.add(controlPanel, BorderLayout.NORTH);
		
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setAutoCreateRowSorter(true);
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.panel.add(this.scrollpane, BorderLayout.CENTER);

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
}
