package client;

import javax.swing.ListSelectionModel;

public class SelectActiveServerGUI extends SelectPropertyGUI<ServerList> {

	public RSAEncryptGUIController controller;
	
	public SelectActiveServerGUI(ServerList data, RSAEncryptGUIController controller, String header) {
		super(data, header);
		this.controller = controller;
		this.getDataList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getDataList().setSelectedIndex(0);
	}
	
	public SelectActiveServerGUI(ServerList data, RSAEncryptGUIController controller){
		this(data, controller, "Select Active Server");
	}

	@Override
	public void actionOnSet() {
		int index = this.getDataList().getSelectedIndices()[0];
		
		this.controller.setActiveServer(this.getData().getData().get(index));
	}

}
