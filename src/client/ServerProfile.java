package client;

import shared.TableData;

public class ServerProfile implements TableData, java.io.Serializable{

	private static final long serialVersionUID = 2460806966800521724L;
	public boolean delete;
	public String nickname, hostname;
	public int port;
	
	public ServerProfile(String hostname, int port, String nickname){
		this.hostname = hostname;
		this.port = port;
		this.nickname = nickname;
	}
	
	public String toString(){
		return this.nickname;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public boolean getDelete() {
		return this.delete;
	}

	@Override
	public void setDelete(boolean in) {
		this.delete = in;
	}
}
