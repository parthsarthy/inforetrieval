package luceneproject;

/**
 * @author parthsarthy
 *
 */

import java.io.*;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

public class Searching {
	private Searching() {}

	  
	  public static void main(String[] args) throws Exception {
		  //final Path path = Paths.get("cran.qry");
		  BufferedReader br = new BufferedReader(new FileReader(new File("/home/ubuntu/lucene-assignment/cran.qry")));
		  String line;
		  File file = new File("searchResults.txt");
		  BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		  String queryString = "";
		  int i = 0;
		  while((line = br.readLine()) != null)
		  {
			  if(line.startsWith(".I") && i!=0)
			  {
				  System.out.println(i + ". " + queryString);
				  queryString = queryString.replaceAll("\\?", "");
				  runQuery(queryString, i, writer);
				 
			  }
			  else if (line.startsWith(".W"))
			  {
				  i++;
				  queryString = "";
			  }
			  else 
			  {
				  queryString = queryString + " " + line;
			  }
		  }
		  //sending last one. 
		  System.out.println(i + ". " + queryString);
		  queryString = queryString.replaceAll("\\?", "");
		  runQuery(queryString, i, writer);
		  writer.close();
		   //
		  }
	  
	  
	  
	  	public static void runQuery(String queryString, int queryNo, BufferedWriter writer) throws IOException, ParseException
	  	{
	  		
	  		String indexFilePath = "/home/ubuntu/lucene-assignment/index_file";
		    IndexReader dirReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexFilePath)));
		    IndexSearcher indexSearcher = new IndexSearcher(dirReader);
		    indexSearcher.setSimilarity(new BM25Similarity());
		    Analyzer analyzer = new StandardAnalyzer();
		    
			//-------------------//
			QueryParser queryParser1 = new QueryParser("title", analyzer);
			QueryParser queryParser2 = new QueryParser("author", analyzer);
			QueryParser queryParser3 = new QueryParser("contents", analyzer);
			    
			Query query1 = queryParser1.parse(queryString);
			Query query2 = queryParser2.parse(queryString);
			Query query3 = queryParser3.parse(queryString);
			
		    Query boostQuery1 = new BoostQuery(query1, (float) 2.5);
		    Query boostQuery2 = new BoostQuery(query2, 3);
		    Query boostQuery3 = new BoostQuery(query3, (float) 0.7);
		
		    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		    booleanQuery.add(boostQuery1, Occur.SHOULD);
		    booleanQuery.add(query2, Occur.SHOULD);
		    booleanQuery.add(boostQuery3, Occur.SHOULD);
		        
		    TopDocs docs = indexSearcher.search(booleanQuery.build(), 15);
		        System.out.println ("length of top docs: " + docs.scoreDocs.length);
		    for( ScoreDoc doc : docs.scoreDocs) {
		        Document thisDoc = indexSearcher.doc(doc.doc);
		            //System.out.println(thisDoc);
		        writer.write(queryNo + " " + thisDoc.get("index") + "\n");
		    }
		    
		
		    dirReader.close();
	  	}
	}