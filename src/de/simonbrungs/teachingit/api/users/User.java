package de.simonbrungs.teachingit.api.users;

import com.sun.net.httpserver.HttpExchange;

public class User {
	private HttpExchange httpExchange;
	private Account account;

	public User(HttpExchange pHttpExchange, Account pAccount) {
		httpExchange = pHttpExchange;
		account = pAccount;
	}

	public HttpExchange getHttpExchange() {
		return httpExchange;
	}

	public Account getAccount() {
		return account;
	}
}
