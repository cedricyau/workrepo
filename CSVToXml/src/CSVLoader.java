import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVLoader {
	private String template;

	public void run() {
		try {
			BufferedReader templateBuff = new BufferedReader(new FileReader(new File("./template.xml")));
			BufferedReader csvBuff = new BufferedReader(new FileReader("./inputFile.csv"));
			StringBuilder templateSb = new StringBuilder();
			String templateLine = null;
			String csvLine = null;
			String[] row = null;		
			
			while ((templateLine = templateBuff.readLine()) != null) {
				templateSb.append(templateLine);
			}
			template = templateSb.toString();
			
			int count = 0;
			while ((csvLine = csvBuff.readLine()) != null) {
				count++;
				row = csvLine.split(",");
				output(template.replace("EXTERNAL_ID", row[0]).replace("INTERNAL_ID", row[1]), row[0] + ".xml", count);	
				if (count == 10) {
					count = 0;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void output(String data, String fileName, int count) {
		try {
			String output = "./output/";
			File outputDir = new File(output);
			if (!outputDir.exists()) {
				outputDir.mkdir();
			}

			FileWriter fw = new FileWriter(output + fileName);
			
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(fw));
			printWriter.write(data);
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
