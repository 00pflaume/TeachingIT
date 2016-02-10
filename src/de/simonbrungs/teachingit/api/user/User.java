package de.simonbrungs.teachingit.api.user;

import com.sun.net.httpserver.HttpExchange;

public class User {
	HttpExchange httpExchange;

	public User(HttpExchange pHttpExchange) {
		httpExchange = pHttpExchange;
	}

	public HttpExchange getHttpExchange() {
		return httpExchange;
	}
}
