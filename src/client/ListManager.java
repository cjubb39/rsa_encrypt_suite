package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public abstract class ListManager<T> implements ActionListener{
	
	private ArrayList<T> data;
	protected ListManagerTableMod<T> tableModel;
	
	private JButton viewAddButton, viewExitButton;
	private JFrame mainGUI;
	protected JTable dataTable;
	protected JScrollPane scrollpane;
	
	public ListManager(ArrayList<T> data){
		this.data = data;
		this.tableModel = new ListManagerTableMod<T>(data);
	}
	
	public abstract ArrayList<T> addOne();
	public abstract ArrayList<T> addOne(T toAdd);
	public abstract void actionPerformed(ActionEvent e);
	
	public ArrayList<T> viewAll(){
		this.tableModel.updateFields();
		
		this.mainGUI = new JFrame("Address Book");
		this.mainGUI.setPreferredSize(new Dimension(350, 250));
		this.mainGUI.setSize(new Dimension(350, 250));
		this.mainGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.mainGUI.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel controlButtons = new JPanel();
		controlButtons.setLayout(new BorderLayout(0, 0));
		
		this.viewAddButton = new JButton("Add");
		this.viewAddButton.addActionListener(this);
		controlButtons.add(this.viewAddButton, BorderLayout.WEST);
		
		this.viewExitButton = new JButton("Close");
		this.viewExitButton.addActionListener(this);
		controlButtons.add(this.viewExitButton, BorderLayout.EAST);
		
		this.mainGUI.add(controlButtons, BorderLayout.SOUTH);
		
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setAutoCreateRowSorter(true);
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.mainGUI.add(this.scrollpane, BorderLayout.CENTER);
		
		this.mainGUI.setVisible(true);
		
		return this.data;
	}

	public void repaintTable(){
		this.dataTable.revalidate();
	}
	
	public String[] getDataStringArray(){
		String[] toRet = new String[this.getData().size()];
		
		for(int i = 0; i < toRet.length; i++){
			toRet[i] = this.getData().get(i).toString();
		}
		
		return toRet;
	}
	
	public ArrayList<T> getData(){
		return this.data;
	}
	
	public JButton getViewExitButton(){
		return this.viewExitButton;
	}
	
	public JButton getViewAddButton(){
		return this.viewAddButton;
	}
	
	public JFrame getGUI(){
		return this.mainGUI;
	}
}
