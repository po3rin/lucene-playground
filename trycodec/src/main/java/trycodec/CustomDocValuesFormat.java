package trycodec;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public class CustomDocValuesFormat extends DocValuesFormat {

  public static final String MY_EXT = "mydv";

  protected CustomDocValuesFormat() {
    super("CustomCodec");
  }

  @Override
  public DocValuesConsumer fieldsConsumer(SegmentWriteState state) throws IOException {
    // return new MyDocValuesWriter(state, MY_EXT);
    return new SimpleTextDocValuesWriter(state, MY_EXT);
  }

  @Override
  public DocValuesProducer fieldsProducer(SegmentReadState state) throws IOException {
    // return new MyDocValuesReader(state, MY_EXT);
    return new SimpleTextDocValuesReader(state, MY_EXT);
  }
}
