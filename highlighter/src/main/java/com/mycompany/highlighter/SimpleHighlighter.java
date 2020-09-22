package com.mycompany.highlighter;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SimpleHighlighter {
    private static String indexPath = "index";

    public static void main(String[] args) {
        try {
            // IndexSearcher 作成
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);

            // 検索
            Analyzer analyzer = new WhitespaceAnalyzer();
            QueryParser parser = new QueryParser("text", analyzer);
            Query query = parser.parse("java");
            // Query query = parser.parse("\"Peter Pan\"");
            TopDocs hits = searcher.search(query, 10);

            // Highlighter 作成
            // Formatter と Scorer を与える
            Formatter formatter = new SimpleHTMLFormatter();
            QueryScorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            // Fragmenter には SimpleSpanFragmenter を指定
            // 固定長(デフォルト100バイト)でフィールドを分割する。
            // ただしフレーズクエリなどの場合に、クエリが複数のフラグメントに分断されないようにする
            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            highlighter.setTextFragmenter(fragmenter);

            for (int i = 0; i < hits.scoreDocs.length; i++) {
                int docid = hits.scoreDocs[i].doc;
                Document doc = searcher.doc(docid);
                String chapNum = doc.get("chapter");
                String title = doc.get("title");
                System.out.println("Chapter " + chapNum + " : " + title);
                String text = doc.get("text");

                // Highlighter で検索キーワード周辺の文字列(フラグメント)を取得
                // デフォルトの SimpleHTMLFormatter は <B> タグで検索キーワードを囲って返す
                // TokenStream が必要なので取得
                TokenStream stream = TokenSources.getAnyTokenStream(reader,
                        docid, "text", analyzer);
                String[] frags = highlighter.getBestFragments(stream, text, 5);
                for (String frag : frags) {
                    System.out.println("    " + frag);
                }
            }

            dir.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
