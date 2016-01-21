/**
 * This class converts a String to an ArrayList
 * 
 * @see de.simonsator.partyandfriends.utilities.StringToArray
 * @author Simonsator
 * @version 1.0.0
 */
package de.simonbrungs.teachingit.utilities;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class converts a String to an ArrayList
 * 
 * @author Simon Brungs
 * @version 1.0.0
 */
public class StringToArrayList {
	/**
	 * This method converts a String to an ArrayList. It splits the String every
	 * time there is a "|".
	 * 
	 * @author Simon Brungs
	 * @version 1.0.0
	 * @param string
	 *            The String, which should be converted to a String
	 * @return Return the created ArrayList
	 */
	public static ArrayList<String> stringToArrayList(String string) {
		StringTokenizer st = new StringTokenizer(string, "|");
		int stLength = st.countTokens();
		ArrayList<String> stringArray = new ArrayList<>();
		for (int i = 0; i < stLength; i++) {
			stringArray.add(st.nextToken());
		}
		return stringArray;
	}

	public static ArrayList<Integer> stringToIntegerArrayList(String string) {
		StringTokenizer st = new StringTokenizer(string, "|");
		int stLength = st.countTokens();
		ArrayList<Integer> stringArray = new ArrayList<>();
		for (int i = 0; i < stLength; i++) {
			stringArray.add(Integer.parseInt(st.nextToken()));
		}
		return stringArray;
	}
}
