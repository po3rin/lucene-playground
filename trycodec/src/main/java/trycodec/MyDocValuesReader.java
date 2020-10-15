package trycodec;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.BinaryDocValues;
import org.apache.lucene.codecs.DocValuesProducer;
import org.apache.lucene.index.SegmentReadState;

public class MyDocValuesReader extends DocValuesProducer {

    final IndexInput data;
    final int maxDoc;

    public MyDocValuesReader(SegmentReadState state, String ext) throws IOException {
        System.out.println("dir=" + state.directory + " seg=" + state.segmentInfo.name + " file=" + IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, ext));
        data = state.directory.openInput(IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, ext), state.context);
        maxDoc = state.segmentInfo.maxDoc();
    }

    @Override
    public BinaryDocValues getBinary(FieldInfo field) throws IOException {
        return new BinaryDocValues();
    }
}
