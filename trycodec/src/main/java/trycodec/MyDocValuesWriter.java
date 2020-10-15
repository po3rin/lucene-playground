package trycodec;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesFormat;
import org.apache.lucene.codecs.BinaryDocValues;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.index.SegmentReadState;

public class MyDocValuesWriter extends DocValuesConsumer {

    IndexOutput data;

    public MyDocValuesWriter(SegmentWriteState state, String ext) throws IOException {
        System.out.println("WRITE: " + IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, ext) + " " + state.segmentInfo.maxDoc() + " docs");
        data = state.directory.createOutput(IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, ext), state.context);
    }

    @Override
    public void addBinaryField(FieldInfo field, DocValuesProducer valuesProducer) throws IOException {
        // write data
	System.out.println("write!!!!");
    }

}
