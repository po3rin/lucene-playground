package trycodec;

import org.apache.lucene.codecs.FilterCodec;
import org.apache.lucene.codecs.lucene86.Lucene86Codec;

public final class CustomCodec extends FilterCodec {
  
    public CustomCodec() {
        super("CustomCodec", new Lucene86Codec());
    }
}
