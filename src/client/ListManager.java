package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import shared.TableData;

public abstract class ListManager<T extends TableData> implements ActionListener{
	
	private ArrayList<T> data;
	protected ListManagerTableMod<T> tableModel;
	
	//private JButton deleteButton;
	private JButton viewAddButton, viewExitButton, deleteButton;
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
	
	public ArrayList<T> deleteSelected(){
		for(int i = 0; i < this.data.size();){
			if (this.data.get(i).getDelete()){
				this.data.remove(i);
			} else {
				++i;
			}
		}
		this.resetTable();
		return this.data;
	}
	public ArrayList<T> delete(T toDelete){
		this.data.remove(toDelete);
		this.resetTable();
		return this.data;
	}
	
	public ArrayList<T> viewAll(){
		this.mainGUI = new JFrame("Address Book");
		this.mainGUI.setPreferredSize(new Dimension(350, 250));
		this.mainGUI.setSize(new Dimension(350, 250));
		this.mainGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.mainGUI.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel controlButtons = new JPanel();
		controlButtons.setLayout(new FlowLayout());
		
		this.viewAddButton = new JButton("Add");
		this.viewAddButton.addActionListener(this);
		controlButtons.add(this.viewAddButton);
		
		this.deleteButton = new JButton("Delete Selected");
		this.deleteButton.addActionListener(this);
		controlButtons.add(this.deleteButton);
		
		this.viewExitButton = new JButton("Close");
		this.viewExitButton.addActionListener(this);
		controlButtons.add(this.viewExitButton);
		
		this.mainGUI.add(controlButtons, BorderLayout.SOUTH);
		
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setAutoCreateRowSorter(true);
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.mainGUI.add(this.scrollpane, BorderLayout.CENTER);
		
		this.mainGUI.setVisible(true);
		
		return this.data;
	}

	public void resetTable(){
		//this.dataTable.revalidate();
		this.dataTable.setModel(this.tableModel = new ListManagerTableMod<T>(this.data));
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
	
	public JButton getDeleteButton(){
		return this.deleteButton;
	}
}
