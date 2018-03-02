package luceneproject;

/**
 * @author parthsarthy
 *
 */

import java.io.*;

public class PrecRecCalc {

	public static int givenResults[][];
	public static int myResults[][];
	public static int flag = 0;
	public static int counter;
	
	public static void main(String args[]) throws IOException
	{
		
		int i = 0;
		// Reading results that are already provided
		BufferedReader br1 = new BufferedReader(new FileReader(new File("../lucene-assignment/cranqrel")));
		BufferedReader bufferReader = new BufferedReader(new FileReader(new File("../lucene-assignment/cranqrel")));
		
		//File to store results
		File newFile = new File("../lucene-assignment/searchResults.txt");
		BufferedReader br2 = new BufferedReader(new FileReader(newFile));
		
		while(br1.readLine() != null)
		{
			i++;
		}
		br1.close();
		
		givenResults = new int[i][3];
		myResults = new int[225*15][2];
		
		String line;
		
		int j = 0;
		while((line = bufferReader.readLine()) != null)
		{
			line = line.replaceAll("\\s+", " ");
			if(line.charAt(line.length() - 1) != 32)
			{
				givenResults[j][0] = Integer.parseInt(line.substring(0, line.indexOf(32)));
				givenResults[j][1] = Integer.parseInt(line.substring(line.indexOf(32) + 1, line.indexOf(32, line.indexOf(32) + 1)));
				givenResults[j][2] = Integer.parseInt(line.substring(line.indexOf(32, line.indexOf(32) + 1) + 1));
			}
			else
			{
				givenResults[j][0] = Integer.parseInt(line.substring(0, line.indexOf(32)));
				givenResults[j][1] = Integer.parseInt(line.substring(line.indexOf(32) + 1, line.indexOf(32, line.indexOf(32) + 1)));
				givenResults[j][2] = Integer.parseInt(line.substring(line.indexOf(32, line.indexOf(32) + 1) + 1,line.lastIndexOf(32)));
			}
			if(givenResults[j][2] < 4)
			{
				j++;
			}
		}
		bufferReader.close();
		
		counter = j;
		
		i = 0;
		while((line = br2.readLine()) != null)
		{
			line = line.replaceAll("\\s+", " ");
			if(i<3375)
			{
				myResults[i][0] = Integer.parseInt(line.substring(0, line.indexOf(32)));
				myResults[i][1] = Integer.parseInt(line.substring(line.indexOf(32) + 1));
				i++;
			}
		}
		PrecRecCalculator(givenResults, myResults);
	}

	private static void PrecRecCalculator(int[][] results, int[][] searchResults) throws IOException 
	{ 		
//		File file = new File("../lucene-assignment/recallPrecision.txt");
//		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		int match = 0;
		double precision = 0;
		double avg_precision = 0;
		int flag2 = 1;
		int flag3 = 0;
		double recall = 0;
		double avg_recall = 0;
		System.out.println(counter);
		for(int i = 0; i < (counter); i++)
		{
			if(flag2 != results[i][0])
			{
				recall = (double)match/(i-flag3);
				flag3 = i;
				avg_recall = avg_recall + recall;
				flag2++;
				flag = flag + 15;
				precision = (double)match/15.0;
				avg_precision = avg_precision + precision;
				match = 0;
				System.out.println(results[i - 1][0] + " " + Math.round(precision * 100.0)/100.0 + " " + Math.round(recall * 100.0)/100.0);
			}
			if(checkResults(results[i][1], searchResults))
			{
				match++;
			}	
		}
		// FOR THE LAST QUERY
		recall = (double)match/(counter - 1 -flag3);
		avg_recall = avg_recall + recall;
		precision = (double)match/15.0;
		avg_precision = avg_precision + precision;
		
		match = 0;
		System.out.println(results[counter - 1][0] + " " + precision + " " + recall);
		System.out.println("Average precission when 15 documents are returned for each search = " + Math.round((avg_precision/225)*100.0)/ 100.0);
		System.out.println("Average recall when 15 documents are returned for each search  = " + Math.round((avg_recall/225)*100.0)/ 100.0);	
	}
	
	public static boolean checkResults(int fileIndex, int[][] searchResults)
	{
		for(int i = flag; i < (flag + 15); i++)
		{
			if(i<3375)
			{
				if(searchResults[i][1] == fileIndex)
				{
					return true;
				}
			}
		}
		return false;
	}

}