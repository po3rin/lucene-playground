package simpleflag;

import java.io.IOException;
import java.nio.file.Paths;
import java.io.StringReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

public class Searcher {
	
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   Query query;
   IndexReader reader;
   Analyzer analyzer;
   
   // indexSearcher, queryParserなどを初期化
   public Searcher(String indexDirectoryPath) throws IOException {
      Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));

      // IndexSearcher
      reader = DirectoryReader.open(indexDirectory);
      indexSearcher = new IndexSearcher(reader);

      analyzer = new StandardAnalyzer();
      // queryParser
      queryParser = new QueryParser("contents", analyzer);
   }
   
   // クエリをパースしてsearchの実行。そしてハイライトを標準出力
   public TopDocs search(String searchQuery) 
      throws IOException, ParseException, InvalidTokenOffsetsException {

         // 検索
         query = queryParser.parse(searchQuery);
         TopDocs hits = indexSearcher.search(query, 10);
         
         // ハイライト
         Formatter formatter = new SimpleHTMLFormatter();
         QueryScorer scorer = new QueryScorer(query, "contents");
         Highlighter highlighter = new Highlighter(formatter, scorer);

         Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
         highlighter.setTextFragmenter(fragmenter);

         for (int i = 0; i < hits.scoreDocs.length; i++) {
            int docid = hits.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docid);
            String text = doc.get("contents");

	    TokenStream stream = analyzer.tokenStream("contents", text);
	    stream.reset();

	    while (stream.incrementToken()){
		System.out.println(stream);
	    }

            String[] frags = highlighter.getBestFragments(stream, text, 1);
            for (String frag : frags) {
               System.out.println("    " + frag);
            }

	    stream.close();
	 }

	 analyzer.close();
         return hits;
   }

   // documentを取得
   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException {
      return indexSearcher.doc(scoreDoc.doc);	
   }
}
