package io.file.graph.exporter;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import org.w3c.dom.svg.SVGDocument;

public class GraphExporterJPEG extends GraphExporter {

	@Override
	public void createImage(String path, SVGDocument document, Rectangle aoi) {
		JPEGTranscoder t = new JPEGTranscoder();
        
        t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(aoi.width));
        t.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(aoi.height));
        t.addTranscodingHint(JPEGTranscoder.KEY_AOI, aoi);
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(0.8));

        TranscoderInput input = new TranscoderInput(document);
        OutputStream ostream;
		try {
			ostream = new FileOutputStream(path);
	        TranscoderOutput output = new TranscoderOutput(ostream);
	        t.transcode(input, output);
	        ostream.flush();
	        ostream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

