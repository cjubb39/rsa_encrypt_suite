package client.table;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import client.server.ServerProfile;
import client.table.gui.SelectActiveServerGUI;

/**
 * Instantiation of ListManager<ServerProfile> used to manage a user's list of servers
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class ServerList extends ListManager<ServerProfile> implements Serializable {

	private static final long serialVersionUID = -129161519568833602L;
	private volatile ServerProfile activeServer;

	/**
	 * Constructor. Same as super(data);
	 * 
	 * @param data
	 *          List of servers to manage
	 */
	public ServerList(ArrayList<ServerProfile> data){
		super(data);
	}

	/**
	 * Constructor. Same as super();
	 */
	public ServerList(){
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#addOne()
	 */
	@Override
	public ArrayList<ServerProfile> addOne(){
		JTextField hostname = new JTextField(), port = new JTextField(), nickname = new JTextField();

		Object[] message = {"Nickname: ", nickname, "HostName: ", hostname, "Port: ", port};

		boolean finished = false;
		while (!finished) {
			int option = JOptionPane.showConfirmDialog(this.getGUI(), message, "Add Server",
					JOptionPane.OK_CANCEL_OPTION);

			if (option == JOptionPane.YES_OPTION) {
				if (nickname.getText().equals("") || hostname.getText().equals("")
						|| port.getText().equals("")) {
					JOptionPane.showMessageDialog(this.getGUI(), "All fields must be filled out!");
					continue;
				}

				int portNum;
				try {
					portNum = Integer.parseInt(port.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this.getGUI(), "Port must be Integer!");
					continue;
				}
				this.getData().add(new ServerProfile(hostname.getText(), portNum, nickname.getText()));

				// set new active server
				this.setActiveServer(this.getData().get(this.getData().size() - 1));

				finished = true;
			} else {
				finished = true;
			}
		}

		return this.getData();
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#addOne(shared.TableData)
	 */
	public ArrayList<ServerProfile> addOne(ServerProfile in){
		this.getData().add(in);
		this.setActiveServer(in);
		return this.getData();
	}

	/**
	 * Sets Active Server as selected by a GUI
	 */
	public void setActiveServer(){
		new SelectActiveServerGUI(this);
		this.fireActiveServerChange();
	}

	/**
	 * Sets Active Server as specified as argument
	 * 
	 * @param serv
	 *          ServerProfile to set as active
	 */
	public void setActiveServer(ServerProfile serv){
		this.activeServer = serv;
		// move new active server to front so it persists
		Collections.swap(this.getData(), this.getData().indexOf(serv), 0);
		this.fireActiveServerChange();
	}

	/**
	 * Notify PropertyChangeListeners of change to active server
	 */
	public void fireActiveServerChange(){
		super.changes.firePropertyChange("ActiveServer", null, this.getActiveServer());
	}

	/**
	 * @return Active Server if set; else "new ServerProfile("",0,"No ActiveServer")" is
	 *         returned
	 */
	public ServerProfile getActiveServer(){
		return (this.activeServer != null) ? this.activeServer : new ServerProfile("", 0,
				"No Active Server");
	}

	/*
	 * (non-Javadoc)
	 * @see client.table.ListManager#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource().equals(this.getViewExitButton())) {
			this.getGUI().dispose();
		} else if (e.getSource().equals(this.getViewAddButton())) {
			this.addOne();
			this.resetTable();
		} else if (e.getSource().equals(this.getDeleteButton())) {
			this.deleteSelected();
		}
	}

	/**
	 * Do nothing when value selected changes
	 * 
	 * @see client.table.ListManager#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent arg0){}
}
