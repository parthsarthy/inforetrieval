package luceneproject;

import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class Searching {

  private Searching() {}

  
  public static void main(String[] args) throws Exception {
	  //final Path path = Paths.get("cran.qry");
	  BufferedReader br = new BufferedReader(new FileReader(new File("cran.qry")));
	  String line;
	  String queryString = "";
	  int i = 0;
	  while((line = br.readLine()) != null)
	  {
		  if(line.startsWith(".I") && i!=0)
		  {
			  System.out.println(i + ". " + queryString);
			  queryString = queryString.replaceAll("\\?", "");
			  runQuery(queryString, i);
			 
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
	  runQuery(queryString, i);
	   //
	  }
  
  
  
  	public static void runQuery(String queryString, int queryNo) throws IOException, ParseException
  	{
  		
  		String index = "../lucene-assignment/index_file";
	    String field = "author";
	    String queries = null;
	    int repeat = 0;
	    boolean raw = false;
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    //Analyzer analyzer = new StandardAnalyzer();
	    EnglishAnalyzer analyzer = new EnglishAnalyzer();
	    
		    //-------------------//
		QueryParser parser1 = new QueryParser("title", analyzer);
		QueryParser parser2 = new QueryParser("author", analyzer);
		QueryParser parser3 = new QueryParser("contents", analyzer);
		    
		Query query1 = parser1.parse(queryString);
		Query query2 = parser2.parse(queryString);
		Query query3 = parser3.parse(queryString);
		    
		//TermQuery query1 = new TermQuery(new Term("title", queryString ));
	    //TermQuery query2 = new TermQuery(new Term("author", queryString));
	    //TermQuery query3 = new TermQuery(new Term("contents", queryString));
	    Query boostedTermQuery1 = new BoostQuery(query1, (float) 2.5);
	    Query boostedTermQuery2 = new BoostQuery(query2, 3);
	    Query boostedTermQuery3 = new BoostQuery(query3, (float) 0.7);
	
	    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
	    booleanQuery.add(boostedTermQuery1, Occur.SHOULD);
	    booleanQuery.add(query2, Occur.SHOULD);
	    booleanQuery.add(boostedTermQuery3, Occur.SHOULD);
	        
	        
	       // TokenStream reader1 = null;
	        //TokenStream stream = analyzer.tokenStream(null, new StringReader("author"));
	    TopDocs docs = searcher.search(booleanQuery.build(), 15);
	        System.out.println ("length of top docs: " + docs.scoreDocs.length);
	    for( ScoreDoc doc : docs.scoreDocs) {
	        Document thisDoc = searcher.doc(doc.doc);
	            //System.out.println(thisDoc);
	        System.out.println(queryNo + ".   " + thisDoc.get("index") + "   " + doc.score);
	    }
	    
	reader.close();
  	}
}
