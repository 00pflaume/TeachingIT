package de.simonbrungs.teachingit.api.users;

import java.net.SocketAddress;

public class User {
	private Account account;
	private String path;
	private SocketAddress socketAddress;

	public User(String pPath, Account pAccount, SocketAddress pSocketAddress) {
		path = pPath;
		account = pAccount;
		socketAddress = pSocketAddress;
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
