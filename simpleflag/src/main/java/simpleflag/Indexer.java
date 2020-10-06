package simpleflag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;

public class Indexer {
   private IndexWriter writer;

   // writer の初期化
   // Analyzer、Writerの設定など..
   public Indexer(String indexDirectoryPath) throws IOException {
      Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
      StandardAnalyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);

	// policyの設定
	LogMergePolicy policy = new LogDocMergePolicy();

	// 一度にマージされるセグメントの数であるマージ係数
	policy.setMergeFactor(10);
	config.setMergePolicy(policy);

	// メモリバッファに格納できる最大ドキュメント数
	config.setMaxBufferedDocs(10);

      writer = new IndexWriter(indexDirectory, config);
   }

   public void close() throws CorruptIndexException, IOException {
      writer.close();
   }

   // FileからDocumentを取得
   private Document getDocument(File file) throws IOException {
      Document document = new Document();
      TextField contentField = new TextField("contents", new FileReader(file));
      TextField fileNameField = new TextField("filename", file.getName(), TextField.Store.YES);
      TextField filePathField = new TextField("filepath", file.getCanonicalPath(), TextField.Store.YES);
      document.add(contentField);
      document.add(fileNameField);
      document.add(filePathField);
      return document;
   }   

   // fileからdocument生成してwrite
   private void indexFile(File file) throws IOException {
      System.out.println("Indexing "+file.getCanonicalPath());
      Document document = getDocument(file);
      writer.addDocument(document);
   }

   // ディレクトリとファイルフィルターを指定して転値インデックスを作成
   public int createIndex(String dataDirPath) throws IOException {
      File[] files = new File(dataDirPath).listFiles();
      for (File file : files) {
         if(!file.isDirectory() && file.exists() && file.canRead()){
            indexFile(file);
         }
      }
      return writer.numRamDocs();
   }
} 
