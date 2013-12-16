package client;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ServerList extends ListManager<ServerProfile> {

	public ServerList(ArrayList<ServerProfile> data) {
		super(data);
	}

	@Override
	public ArrayList<ServerProfile> addOne() {
		JTextField hostname = new JTextField(), port = new JTextField(), nickname = new JTextField();
		
		Object[] message = { "Nickname: ", nickname, "HostName: ", hostname, "Port: ", port};
	
		boolean finished = false;
		while(!finished){
			int option = JOptionPane.showConfirmDialog(this.getGUI(), message,
							"Add Server", JOptionPane.OK_CANCEL_OPTION);
			
			if (option == JOptionPane.YES_OPTION){
				if (nickname.getText().equals("") || hostname.getText().equals("") || port.getText().equals("")){
					JOptionPane.showMessageDialog(this.getGUI(),"All fields must be filled out!");
					continue;
				}
				
				int portNum;
				try{
					portNum = Integer.parseInt(port.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(this.getGUI(),"Port must be Integer!");
					continue;
				}
				this.getData().add(new ServerProfile(hostname.getText(), portNum, nickname.getText()));
				finished = true;
			} else {
				finished = true;
			}
		}
		
		return this.getData();
	}
	
	public ArrayList<ServerProfile> addOne(ServerProfile in){
		this.getData().add(in);		
		return this.getData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(this.getViewExitButton())){
			this.getGUI().dispose();
		} else if (e.getSource().equals(this.getViewAddButton())){
			this.addOne();
			this.resetTable();
		}  else if (e.getSource().equals(this.getDeleteButton())){
			this.deleteSelected();
		}
	}

}
