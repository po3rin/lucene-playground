package trycodec;

import org.apache.lucene.codecs.FilterCodec;
import org.apache.lucene.codecs.lucene86.Lucene86Codec;
import org.apache.lucene.codecs.DocValuesFormat;

public final class CustomCodec extends FilterCodec {
  
    private final MyDocValuesFormat myDocValuesFormat = new MyDocValuesFormat();

    public CustomCodec() {
        super("CustomCodec", new Lucene86Codec());
    }

    @Override
    public DocValuesFormat docValuesFormat() {
        return myDocValuesFormat;
    }
}
