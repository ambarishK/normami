package org.contentmine.ami;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * 
 * @author pm286
 *
 */
public class AMISearch {
	private static final Logger LOG = Logger.getLogger(AMISearch.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String SEARCH = "search";
	public static final String HELP = "help";

	public static void main(String[] args) {
		List<String> argList = new ArrayList<String>(Arrays.asList(args));
		if (argList.size() == 0 || HELP.equals(argList.get(0))) {
			runHelp(argList);
		} else {
			runAMISearches(argList);
		}
	}

	public static void runHelp(List<String> argList) {
		if (argList.size() > 0) argList.remove(0);
		AMIDictionary dictionaries = new AMIDictionary();
		dictionaries.help(argList);
	}

	private static void runAMISearches(List<String> argList) {
		String projectName = argList.get(0);
		argList.remove(0);
		if (argList.size() == 0) {
			System.err.println("No default action for project: "+projectName+" (yet)");
		} else {
			AMIProcessor amiProcessor = AMIProcessor.createProcessor(projectName);
			amiProcessor.setDebugLevel(Level.DEBUG);
			amiProcessor.runSearchesAndCooccurrence(argList);
		}
	}



}
