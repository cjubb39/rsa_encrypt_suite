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
	
	public InboxController(ArrayList<InboxMessage> messages, JPanel panel){
		super(messages);
		
		this.panel = panel;
		this.viewAll();
	}
	
	@Override
	public ArrayList<InboxMessage> viewAll(){
		// ready given panel
		//this.panel.setLayout(new BorderLayout(0,0));
		
		// create control panel
		JPanel controlPanel = new JPanel();
		JButton tempButton = new JButton("Real Action");
		controlPanel.add(tempButton);
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
		// we want to make sure Table manager has correct field names
		if (this.getData().size() > 0){
			this.tableModel.updateFields();
		}
		this.repaintTable();
		return this.getData();
	}
	
	public ArrayList<InboxMessage> addOne(InboxMessage in){
		this.getData().add(in);
		
		// we want to make sure Table manager has correct field names
		if (this.getData().size() > 0){
			this.tableModel.updateFields();
		}
		this.repaintTable();
		return this.getData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
