package com.mycompany.highlighter;

import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexTexts {
    private static String datadir = "text";
    private static String idxdir = "index";

    public static void main(String args[]) {
        IndexTexts indexer = new IndexTexts();

        Directory dir = null;
        IndexWriter writer = null;
        try {
	    dir = FSDirectory.open(Paths.get(idxdir));
            writer = indexer.getWriter(dir);
            indexer.doIndexTexts(writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    private void doIndexTexts(IndexWriter writer) throws IOException {
        File dataDir = new File(datadir);
        for (File file : dataDir.listFiles()) {
            if (!file.isFile() || !file.getName().endsWith(".txt")) {
                continue;
            }
            String name = file.getName();
            int chapNum = Integer
                    .parseInt(name.substring(0, name.length() - 4));

            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                String title = reader.readLine();
                System.out.println(Integer.toString(chapNum) + " " + title);

                StringBuffer buf = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    buf.append(line + " ");
                }

                Document doc = makeDoc(Integer.toString(chapNum), title,
                        buf.toString());
                writer.addDocument(doc);
            } finally {
                reader.close();
            }
        }
    }

    // ハイライト対象フィールド用のフィールド定義
    static FieldType contentType = new FieldType();
    static {
        contentType.setStored(true);
        contentType.setTokenized(true);
        // for Highlighter, FastvectorHighlighter
        // contentType.setStoreTermVectors(true);
        // contentType.setStoreTermVectorPositions(true);
        // contentType.setStoreTermVectorOffsets(true);
        // for PostingsHighlighter
        // contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    }

    private Document makeDoc(String chapNum, String title, String text) {
        // chapter, title, text の 3 つのフィールドをもつドキュメント
        Document doc = new Document();
        doc.add(new StringField("chapter", chapNum, Store.YES));
        doc.add(new StringField("title", title, Store.YES));
        doc.add(new Field("text", text, contentType));
        return doc;
    }

    private IndexWriter getWriter(Directory dir) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(
                new WhitespaceAnalyzer());
        return new IndexWriter(dir, config);
    }
}
