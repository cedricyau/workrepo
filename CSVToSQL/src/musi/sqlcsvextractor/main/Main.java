/*
 * This application will read from a csv file and create sql insert 
 * statements.
*/

package musi.sqlcsvextractor.main;

import musi.sqlcsvextractor.loader.CSVLoader;

public class Main {
	public static void main(String[] args) {
		CSVLoader csvLoader = new CSVLoader(args);
		csvLoader.run();
	}
}