package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public abstract class SelectPropertyGUI<T extends ListManager<?>> implements ActionListener {

	//private String[] strData;
	private T data;
	
	private JList dataList;
	private JDialog addProp;
	private JButton cancelButton;
	private JButton setButton;
	
	public SelectPropertyGUI(T data, String header) {
		this.data = data;
		
		this.initGUI(header);
	}
	
	public abstract void actionOnSet();
	
	public void initGUI(String header){
		this.addProp = new JDialog();
		this.addProp.setTitle(header);
		this.addProp.setSize(new Dimension(350,250));
		this.addProp.setLayout(new BorderLayout());
		
		// set up list
		this.dataList = new JList(this.data.getDataStringArray());
		this.dataList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.dataList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.dataList.setFixedCellWidth(-1);
		this.dataList.setFixedCellHeight(25);
		this.dataList.setCellRenderer(new ListRenderer(10, 5, 10, 10));
		
		JScrollPane scrollPane = new JScrollPane(this.dataList);
		
		//get buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new BorderLayout());
		
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);
		
		this.setButton = new JButton("Select");
		this.setButton.addActionListener(this);
		
		buttons.add(this.setButton, BorderLayout.WEST);
		buttons.add(this.cancelButton, BorderLayout.EAST);
		
		this.addProp.add(buttons, BorderLayout.SOUTH);
		this.addProp.add(scrollPane, BorderLayout.CENTER);
		this.addProp.setVisible(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(this.cancelButton)){
			this.addProp.dispose();
		} else if (arg0.getSource().equals(this.setButton)){
			this.actionOnSet();
			this.addProp.dispose();
		}
	}


	public T getData() {
		return data;
	}


	public JList getDataList() {
		return dataList;
	}


	public JDialog getAddProp() {
		return addProp;
	}


	public JButton getCancelButton() {
		return cancelButton;
	}


	public JButton getSetButton() {
		return setButton;
	}
}
