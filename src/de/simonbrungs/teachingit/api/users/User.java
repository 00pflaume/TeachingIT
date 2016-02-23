package de.simonbrungs.teachingit.api.users;

import java.net.SocketAddress;
import java.util.HashMap;

public class User {
	private Account account;
	private String path;
	private SocketAddress socketAddress;
	private HashMap<String, Object> postRequests;

	public User(String pPath, Account pAccount, SocketAddress pSocketAddress, HashMap<String, Object> pPostRequests) {
		path = pPath;
		account = pAccount;
		socketAddress = pSocketAddress;
		postRequests = pPostRequests;
	}

	public Object getPostRequest(String pKey) {
		return postRequests.get(pKey);
	}

	public String getStringPostRequest(String pKey) {
		Object string = postRequests.get(pKey);
		if (string == null)
			if (string instanceof String)
				return (String) string;
		return null;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public String getCalledPath() {
		return path;
	}

	public Account getAccount() {
		return account;
	}

}
