package luceneproject;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class FileIndexer {
  //private IndexWriter writer;
  private FileIndexer() {}

  /** Index all text files under a directory. */
  public static void main(String[] args) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH that can be searched with SearchFiles";
    
    
    String indexPath = "../lucene-assignment/index_file/";
    String docsPath = "../lucene-assignment/split_documents/";
    boolean create = true;

//    if (docsPath == null) {
//      System.err.println("Usage: " + usage);
//      System.exit(1);
//    }

    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(Paths.get(indexPath));
      Analyzer analyzer = new StandardAnalyzer();
//      EnglishAnalyzer en_an = new EnglishAnalyzer();
      
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      //IndexWriterConfig iwc = new IndexWriterConfig(en_an);

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

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

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
      
      InputStream auth = new ByteArrayInputStream(author.getBytes(StandardCharsets.UTF_8));
      InputStream titl = new ByteArrayInputStream(title.getBytes(StandardCharsets.UTF_8));
      InputStream cont = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
      InputStream indx = new ByteArrayInputStream(index.getBytes(StandardCharsets.UTF_8));
      

	      Document doc = new Document();
	      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
	      doc.add(pathField);
	      
	      doc.add(new LongPoint("modified", lastModified));
	      
	      
	      BufferedReader reader = new BufferedReader(new InputStreamReader(auth, StandardCharsets.UTF_8));
	    	  String x1 = (new BufferedReader(new InputStreamReader(titl)).readLine());
	    	  String x2 = (new BufferedReader(new InputStreamReader(auth)).readLine());
	    	  String x3 = (new BufferedReader(new InputStreamReader(indx)).readLine());
	    	  String x4 = (new BufferedReader(new InputStreamReader(indx)).readLine());
	    	  	
	      if(x3 != null)
	      {
	    	  	Field ind = new StringField("index", x3, Field.Store.YES);
	    	  	doc.add(ind);
	      }
	      
	      doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(cont, StandardCharsets.UTF_8))));
	      doc.add(new TextField("title", new BufferedReader(new InputStreamReader(titl, StandardCharsets.UTF_8))));
	      doc.add(new TextField("author", new BufferedReader(new InputStreamReader(auth, StandardCharsets.UTF_8))));
	      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	        System.out.println("adding " + file);
	        writer.addDocument(doc);
	      } else {
	        System.out.println("updating " + file);
	        writer.updateDocument(new Term("path", file.toString()), doc);
	      }
   	}
   	
}
     
     

   	