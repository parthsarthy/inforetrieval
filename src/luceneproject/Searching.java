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
import org.apache.lucene.store.FSDirectory;

public class Searching {
	private Searching() {}

	  
	  public static void main(String[] args) throws Exception {
		  //Reading given queries
		  BufferedReader br = new BufferedReader(new FileReader(new File("/home/ubuntu/lucene-assignment/cran.qry")));
		  String readLine;
		  File file = new File("/home/ubuntu/lucene-assignment/searchResults.txt");
		  BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		  String qryStr = "";
		  int i = 0;
		  while((readLine = br.readLine()) != null)
		  {
			  if(readLine.startsWith(".I") && i!=0)
			  {
				  qryStr = qryStr.replaceAll("\\?", "");
				  runQuery(qryStr, i, writer);
				 
			  }
			  else if (readLine.startsWith(".W"))
			  {
				  i++;
				  qryStr = "";
			  }
			  else 
			  {
				  qryStr = qryStr + " " + readLine;
			  }
		  }
		  //Last query
		  qryStr = qryStr.replaceAll("\\?", "");
		  runQuery(qryStr, i, writer);
//		  Closing to avoid data leakage
		  writer.close();
		  br.close();
		  }
	  
	  
	  
	  	public static void runQuery(String queryString, int queryNo, BufferedWriter writer) throws IOException, ParseException
	  	{
	  		
	  		String indexFilePath = "/home/ubuntu/lucene-assignment/index_file";
		    IndexReader dirReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexFilePath)));
		    IndexSearcher indexSearcher = new IndexSearcher(dirReader);
//		    indexSearcher.setSimilarity(new BM25Similarity());
		    Analyzer analyzer = new StandardAnalyzer();
		    BooleanQuery.Builder boolQ = new BooleanQuery.Builder();
		    
			//Using standard analyzer//
			QueryParser queryParser1 = new QueryParser("title", analyzer);
			QueryParser queryParser2 = new QueryParser("author", analyzer);
			QueryParser queryParser3 = new QueryParser("contents", analyzer);
			
			Query query1 = queryParser1.parse(queryString);
			Query query2 = queryParser2.parse(queryString);
			Query query3 = queryParser3.parse(queryString);
			
			//Query boosting
		    Query boostQuery1 = new BoostQuery(query1, (float) 2.5);
		    Query boostQuery2 = new BoostQuery(query2, 2);
		    Query boostQuery3 = new BoostQuery(query3, (float) 0.7);
		
		    
		    boolQ.add(boostQuery1, Occur.SHOULD);
		    boolQ.add(boostQuery2, Occur.SHOULD);
		    boolQ.add(boostQuery3, Occur.SHOULD);
		        
		    TopDocs docs = indexSearcher.search(boolQ.build(), 15);
		    for( ScoreDoc doc : docs.scoreDocs) {
		        Document thisDoc = indexSearcher.doc(doc.doc);
		        writer.write(queryNo + " " + thisDoc.get("index") + "\n");
		    }
		    dirReader.close();
	  	}
	}
