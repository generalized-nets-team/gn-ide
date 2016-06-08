package net.generalised.genedit.fileexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Transition;

public abstract class GraphicalStructureFileExporter implements FileExporter {

	public void export(GeneralizedNet gn, File outputFile) throws IOException {
		
		init(gn);
		
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));

			writeHeader(writer);
			
			for (Transition tr : gn.getTransitions()) {
				writeTransition(writer, tr);
				
				for (PlaceReference ref : tr.getInputs()) {
					writeArc(writer, ref, tr);
				}
				for (PlaceReference ref : tr.getOutputs()) {
					writeArc(writer, ref, tr);
				}
			}
			
			for (Place place : gn.getPlaces()) {
				writePlace(writer, place);
			}
			
			writeFooter(writer);
			
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}
	
	// TODO better: functions should return graphical primitives and the concrete
	// implementation should draw them
	
	protected void init(GeneralizedNet gn) {
	}
	
	/**
	 * @throws IOException  
	 */
	protected void writeHeader(Writer writer) throws IOException {
	}
	
	/**
	 * @throws IOException  
	 */
	protected void writeTransition(Writer writer, Transition transition) throws IOException {
	}
	
	/**
	 * @throws IOException  
	 */
	protected void writeArc(Writer writer, PlaceReference ref, Transition transition) throws IOException {
	}

	/**
	 * @throws IOException  
	 */
	protected void writePlace(Writer writer, Place place) throws IOException {
	}
	
//	/**
//	 * @throws IOException  
//	 */
//	protected void writeLabel(Writer writer, String text, GnObjectWithPosition gnObject) throws IOException {
//	}

	/**
	 * @throws IOException  
	 */
	protected void writeFooter(Writer writer) throws IOException {
	}

}
