package luceneproject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class FileIndexer {
  //private IndexWriter writer;
  private FileIndexer() {}

  /** Index all text files under a directory. */
  public static void main(String[] args) {

    String indexPath = "../lucene-assignment/index_file/";
    String docsPath = "../lucene-assignment/split_documents/";
    boolean create = true;
  
    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(Paths.get(indexPath));
      EnglishAnalyzer en_an = new EnglishAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(en_an);

      if (create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, docDir);

      // NOTE: if you want to maximize search performance,
      // you can optionally call forceMerge here.  This can be
      // a terribly costly operation, so generally it's only
      // worth it when your index is relatively static (ie
      // you're done adding documents to it):
      //
      // writer.forceMerge(1);

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  /**
   * Indexes the given file using the given writer, or if a directory is given,
   * recurses over files and directories found under the given directory.
   * 
   * NOTE: This method indexes one document per input file.  This is slow.  For good
   * throughput, put multiple documents into your input file(s).  An example of this is
   * in the benchmark module, which can create "line doc" files, one document per line,
   * using the
   * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
   * >WriteLineDocTask</a>.
   *  
   * @param writer Writer to the index where the given file/dir info will be stored
   * @param path The file to index, or the directory to recurse into to find files to index
   * @throws IOException If there is a low-level I/O error
   */
  static void indexDocs(final IndexWriter writer, Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          try {
            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
            System.out.println("visitFile: " + file);
          } catch (IOException ignore) {
            // don't index files that can't be read.
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else {
      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }

  /** Indexes a single document */
  
   	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
   		
	    //try (InputStream stream = Files.newInputStream(file)) {
//   		InputStream stream = Files.newInputStream(file);
		InputStream stream1 = Files.newInputStream(file);
		InputStream stream2 = Files.newInputStream(file);
		  
	    	BufferedReader br = null;
	    	BufferedReader br1 = null;
	    	  
	    	  
	    	String title = "";
      	String author = "";
      	String content = "";
	    	String line;
	    	String index = "";
	    	  
	    	br = new BufferedReader(new InputStreamReader(stream1));
	    	br1 = new BufferedReader(new InputStreamReader(stream2));
	    	int l = 0;
	    	int x = 0;
	    	while ((line = br1.readLine()) != null)
	    	{
	    		l++;
	    	}
	    	String[] lines = new String[l];
	    	while ((line = br.readLine()) != null)
	    	{
	    		lines[x] = line;
	    		x++;
	    	}
	    	for(int i = 0; i<lines.length; i++)
	    {
	    		if (lines[i].equals(".T"))
	      	{
	    			int j = i+1;
	        		  
	    			while(!lines[j].equals(".A"))
	      	  	{
	      	  		title = title + " " + lines[j];
	      	  		j++;
	      	  	}
	      	 }
	    		else if(lines[i].equals(".A"))
	        	{
	        	  int j = i+1;
	        	  while(!lines[j].equals(".B"))
	        	  {
	        	  	author = author + " " + lines[j];
	        	  	j++;
	        	  }
	        	 }
	        	 else if (lines[i].equals(".W"))
	        	 {
	        	 	int j = i+1;
	        	 	while(j<l)
	        	 	{
	        	 		content = content + " " + lines[j];
	        	 		j++;
	        	 	}
	        	 }
	        	 else if (lines[i].startsWith(".I"))
	        	 {
	        		 index = lines[i].substring(2);
	        	 }
	      }
	  //System.out.println(title);
      //System.out.println(author);
      //System.out.println(content);
      
      InputStream auth = new ByteArrayInputStream(author.getBytes(StandardCharsets.UTF_8));
      InputStream titl = new ByteArrayInputStream(title.getBytes(StandardCharsets.UTF_8));
      InputStream cont = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
      InputStream indx = new ByteArrayInputStream(index.getBytes(StandardCharsets.UTF_8));
      
	      // make a new, empty document
	      Document doc = new Document();
	      
	      // Add the path of the file as a field named "path".  Use a
	      // field that is indexed (i.e. searchable), but don't tokenize 
	      // the field into separate words and don't index term frequency
	      // or positional information:
	      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
	      doc.add(pathField);
	      
	      doc.add(new LongPoint("modified", lastModified));
	    	  String x3 = (new BufferedReader(new InputStreamReader(indx)).readLine());

	      if(x3 != null)
	      {
	    	  	Field ind = new StringField("index", x3, Field.Store.YES);
	    	  	doc.add(ind);
	    	  	//System.out.println("haha");
	      }
	      
	      doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(cont, StandardCharsets.UTF_8))));
	      doc.add(new TextField("title", new BufferedReader(new InputStreamReader(titl, StandardCharsets.UTF_8))));
	      doc.add(new TextField("author", new BufferedReader(new InputStreamReader(auth, StandardCharsets.UTF_8))));
	      
	      //doc.add(f);
	      //f.setBoost(2.0f);
	      
	      
	      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	        // New index, so we just add the document (no old document can be there):
	        System.out.println("adding " + file);
	        writer.addDocument(doc);
	      } else {
	        System.out.println("updating " + file);
	        writer.updateDocument(new Term("path", file.toString()), doc);
	      }
   	}
}
     
     

   	