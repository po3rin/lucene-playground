package simpleflag;

import java.io.IOException;
import java.nio.file.Paths;

// import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.nodes.PathQueryNode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.BooleanClause.Occur;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.queries.CommonTermsQuery;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.dict.UserDictionary;

import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
// import simpleflag.fragmenter.SimpleSpanFragmenter;

public class Searcher {

	IndexSearcher indexSearcher;
	QueryParser queryParser;
	CommonTermsQuery query;
	IndexReader reader;
	JapaneseAnalyzer analyzer;
	Highlighter highlighter;
	Formatter formatter;

	// indexSearcher, queryParserなどを初期化
	public Searcher(String indexDirectoryPath) throws IOException {
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));

		// IndexSearcher
		reader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(reader);

		// ja analyzer
		UserDictionary userDict = null;
		analyzer = new JapaneseAnalyzer(userDict, JapaneseTokenizer.Mode.SEARCH, JapaneseAnalyzer.getDefaultStopSet(),
				JapaneseAnalyzer.getDefaultStopTags());

		// queryParser
		queryParser = new QueryParser("contents", analyzer);

		// formatter fot highlighter
		formatter = new SimpleHTMLFormatter();
	}

	// クエリをパースしてsearchの実行。そしてハイライトを標準出力
	public TopDocs search(String searchQuery) throws IOException, ParseException, InvalidTokenOffsetsException {

		// 検索
		// PhraseQuery.Builder builder = new PhraseQuery.Builder();
		// builder.add(new Term("contents", "日本"), 0);
		// builder.add(new Term("contents", "外科"), 1);
		// builder.add(new Term("contents", "学会"), 2);
		// PhraseQuery pq = builder.build();

		query = new CommonTermsQuery(Occur.MUST, Occur.SHOULD, 3);
		query.add(new Term("contents", "日本"));//stop-word
		query.add(new Term("contents", "外科"));
		query.add(new Term("contents", "学会"));
		// query = queryParser.parse(searchQuery);
		return indexSearcher.search(query, 10);
	}

	// documentを取得
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public String highlight(ScoreDoc scoreDoc) throws CorruptIndexException, IOException, InvalidTokenOffsetsException {
		QueryScorer scorer = new QueryScorer(query, "contents");
		Highlighter highlighter = new Highlighter(formatter, scorer);

		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		highlighter.setTextFragmenter(fragmenter);

		Document doc = indexSearcher.doc(scoreDoc.doc);

		String text = doc.get("contents");

		TokenStream stream = analyzer.tokenStream("contents", text);
		String[] frags = highlighter.getBestFragments(stream, text, 1);
		// for (String frag : frags) {
		// 	System.out.println("    " + frag);
		// }
		return frags[0];
	}
}
