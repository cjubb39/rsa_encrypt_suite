package client.server;

import java.io.Serializable;

import shared.TableData;

/**
 * Holds information necessary to interact with server, including hostname, port, and
 * nickname.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class ServerProfile implements TableData, Serializable {

	private static final long serialVersionUID = 2460806966800521724L;
	public boolean delete;
	public String hostname;
	public int port;
	public String nickname;

	/**
	 * Constructor. All fields required
	 * 
	 * @param hostname
	 *          Hostname of server. May be text or IP.
	 * @param port
	 *          Port through which to connect. (4444 is default)
	 * @param nickname
	 *          Text nickname for server. Will be returned in toString method
	 */
	public ServerProfile(String hostname, int port, String nickname){
		this.hostname = hostname;
		this.port = port;
		this.nickname = nickname;
	}

	/**
	 * Returns nickname of server
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return this.nickname;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname(){
		return hostname;
	}

	/**
	 * @param hostname
	 *          the hostname to set
	 */
	public void setHostname(String hostname){
		this.hostname = hostname;
	}

	/**
	 * @return the port
	 */
	public int getPort(){
		return port;
	}

	/**
	 * @param port
	 *          the port to set
	 */
	public void setPort(int port){
		this.port = port;
	}

	@Override
	public boolean getDelete(){
		return this.delete;
	}

	@Override
	public void setDelete(boolean in){
		this.delete = in;
	}
}
