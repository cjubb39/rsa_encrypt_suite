package client.table.gui;

import javax.swing.ListSelectionModel;

import client.table.ServerList;

/**
 * Instantiation of SelectPropertyGUI<ServerList> used to select new active server
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class SelectActiveServerGUI extends SelectPropertyGUI<ServerList> {

	/**
	 * Constructor
	 * 
	 * @param data
	 *          List of servers from which to choose
	 * @param header
	 *          Header bar text to display
	 */
	public SelectActiveServerGUI(ServerList data, String header){
		super(data, header);
		this.getDataList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getDataList().setSelectedIndex(0);
	}

	/**
	 * Constructor. Uses default header bar text: "Select Active Server".
	 * 
	 * @param data
	 *          List of servers from which to choose
	 */
	public SelectActiveServerGUI(ServerList data){
		this(data, "Select Active Server");
	}

	/**
	 * Set selected server as active server
	 * 
	 * @see client.table.gui.SelectPropertyGUI#actionOnSet()
	 */
	@Override
	public void actionOnSet(){
		int index = this.getDataList().getSelectedIndices()[0];

		this.getData().setActiveServer(this.getData().getData().get(index));
	}

}
