/**
 * 
 */
package luceneproject;

/**
 * @author parthsarthy
 *
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.lucene.analysis.Analyzer;

//@SuppressWarnings("deprecation")
public class CranFileSplitter {
	static Analyzer stop;
	static Analyzer snowball;

	private static class Splitter extends Thread {
		String inputFile;
		String outputDirectory;

		Splitter(String inputFile, String outputDirectory) {
			super();
			//System.out.println("inside Thread");
			this.inputFile = inputFile;
			this.outputDirectory = outputDirectory;
		}

		public void run() {
			processFile(inputFile, outputDirectory);
		}
	}

	public static void main(String[] args) {

		String inputDirectory = "/home/ubuntu/lucene-assignment/cran/";
		Splitter documents_splitter = new Splitter(inputDirectory + "cran.all.1400", "/home/ubuntu/lucene-assignment/split_documents/");
		documents_splitter.start();
		//Splitter queries_splitter = new Splitter(inputDirectory + "cran.qry", "/Users/rahulsatya/Desktop/split_query");
		//queries_splitter.start();

		try {
			documents_splitter.join();
			//queries_splitter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void processFile(String inputFile, String outputDirectory) {
		BufferedReader bufferedReader = null;
		String line;
//		String file_name = null;
		String output = "";
		Integer counter = 0;
//		boolean controlW = false;

		try {
			bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {

				//System.out.println(line);
				
				if (line.startsWith(".T")) {
					counter ++;
				}
				if(line.startsWith(".I") && counter > 0) {
					writeOutput(outputDirectory + counter.toString() + ".txt", output);
					output = "";
				}
				output = output + line + System.getProperty("line.separator");
			}
			writeOutput(outputDirectory + counter.toString() + ".txt", output);
			bufferedReader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void writeOutput(String outputFileName, String output) throws IOException {
		final File outputDirectory = new File(outputFileName);

		if (!outputDirectory.exists()) {
			outputDirectory.createNewFile();
		}

		Writer fileWriter = new FileWriter(outputDirectory);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(output);
		//bufferedWriter.write(analyze(output));
		bufferedWriter.close();
		fileWriter.close();
	}

	
}
