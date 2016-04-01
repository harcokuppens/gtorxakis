package io.file.graph.exporter;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.w3c.dom.svg.SVGDocument;

public class GraphExporterPNG extends GraphExporter {

	@Override
	public void createImage(String path, SVGDocument document, Rectangle aoi) {
		PNGTranscoder t = new PNGTranscoder();
           
        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(aoi.width));
        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(aoi.height));
        t.addTranscodingHint(PNGTranscoder.KEY_AOI, aoi);
        
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
