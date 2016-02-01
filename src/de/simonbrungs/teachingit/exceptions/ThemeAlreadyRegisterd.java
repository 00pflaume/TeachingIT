package de.simonbrungs.teachingit.exceptions;

public class ThemeAlreadyRegisterd extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
	public void printStackTrace() {
		System.out.println(getMessage());
	}

	@Override
	public String getMessage() {
		return "A theme is already registred.";
	}
}
