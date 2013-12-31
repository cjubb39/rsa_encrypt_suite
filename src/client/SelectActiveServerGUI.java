package client;

import javax.swing.ListSelectionModel;

public class SelectActiveServerGUI extends SelectPropertyGUI<ServerList> {
	
	public SelectActiveServerGUI(ServerList data, String header) {
		super(data, header);
		this.getDataList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getDataList().setSelectedIndex(0);
	}
	
	public SelectActiveServerGUI(ServerList data){
		this(data, "Select Active Server");
	}

	@Override
	public void actionOnSet() {
		int index = this.getDataList().getSelectedIndices()[0];
		
		this.getData().setActiveServer(this.getData().getData().get(index));
	}

}
