package musi.sqlcsvextractor.loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class CSVLoader {
	private File inputFile;
	private File outputFile;
	private String tableName;
	
	public CSVLoader(String[] args) {
		inputFile = new File(args[0]);
		outputFile = new File(args[1]);
		tableName = args[2].toLowerCase();
	}

	public void run() {
		String line = null;
		String[] row = null;
		int lineNumber = 0;
		List<String> columnNames = new ArrayList<String>();
		StringBuilder insertColumnsString;
		StringBuilder insertValuesString = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			insertColumnsString = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
			while ((line = br.readLine()) != null) {
				
				lineNumber++;
				row = line.split(",");
				if (lineNumber == 1) {
					columnNames = Arrays.asList(row);
					for (String column : columnNames) {
						insertColumnsString.append(column.toLowerCase().replaceAll(" ", "_"));
						if (!column.equals(columnNames.get(columnNames.size() -1))) {
							insertColumnsString.append(", ");
						}
					}
					insertColumnsString.append(") VALUES (");
					continue;
				}
				insertValuesString.append(insertColumnsString.toString());
				for (int i = 0; i < row.length; i++) {
					insertValuesString.append("'").append(row[i]).append("'");
					if (i != (row.length - 1)) {
						insertValuesString.append(",");						
					}
				}	
				insertValuesString.append(");\n");
				System.out.println(insertValuesString);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		output(insertValuesString.toString());
	}

	private void output(String data) {
		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			printWriter.write(data);
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
