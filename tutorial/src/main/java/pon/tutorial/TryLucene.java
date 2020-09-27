package pon.tutorial;

import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class TryLucene {
   String indexDir = "./index/";
   String dataDir = "./data/";
   Indexer indexer;
   Searcher searcher;

   // エントリポイント
   public static void main(String[] args) {
      TryLucene tester;
      try {
         tester = new TryLucene();
         tester.createIndex();
         tester.search("clock");
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }

   // Indexerを利用してindexを生成
   private void createIndex() throws IOException {
      indexer = new Indexer(indexDir);

      int numIndexed = indexer.createIndex(dataDir);
      indexer.close();

      System.out.println(numIndexed+" File indexed");		
   }

   // search
   private void search(String searchQuery) throws IOException, ParseException {
      searcher = new Searcher(indexDir);
      TopDocs hits = searcher.search(searchQuery);
   
      System.out.println(hits.totalHits + " documents found");

      for(ScoreDoc scoreDoc : hits.scoreDocs) {
         Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
            + doc.get("filepath"));
      }  
   }
}
