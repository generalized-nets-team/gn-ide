package net.generalised.genedit.fileexport;

import java.io.IOException;
import java.io.Writer;

import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.RichTextName;
import net.generalised.genedit.model.gn.Transition;

public class SvgFileExporter extends GraphicalStructureFileExporter {
	
	private static void writeSvgLine(Writer writer, int x1, int y1, int x2, int y2) throws IOException {
		writer.write("<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" stroke-width=\"1\"/>\n");
	}

	private static void writeSvgArrowTo(Writer writer, int x, int y) throws IOException {
		writeSvgLine(writer, x, y, x - 10, y - 5);
		writeSvgLine(writer, x, y, x - 10, y + 5);
	}

	private static void writeSvgText(Writer writer, int x, int y, RichTextName text) throws IOException {
		
		writer.write("<text x=\"" + x + "\" y=\"" + y + "\" font-style=\"italic\" font-family=\"Times New Roman\" font-size=\"12\" fill=\"black\" >");
		writer.write(text.getMainPart());
		writer.write("</text>\n");
		// XXX too ugly
		if (text.getMainPart().equals("Z")) x += 2;
		writer.write("<text x=\"" + (x + 6) + "\" y=\"" + (y + 3) + "\" font-family=\"Times New Roman\" font-size=\"9\" fill=\"black\" >");
		writer.write(text.getSubscriptPart());
		writer.write("</text>\n");
	}
	
	@Override
	protected void writeHeader(Writer writer) throws IOException {
		// FIXME use real bounding rectangle; adjust fonts; better subscripts management
		writer.write("<?xml version=\"1.0\" standalone=\"no\"?>\n");
		writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" ");
		writer.write("\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		writer.write("<svg width=\"44cm\" height=\"73cm\" viewBox=\"0 0 440 730\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
		writer.write("<g stroke=\"black\">");
	}

	@Override
	protected void writeTransition(Writer writer, Transition transition)
			throws IOException {
		
		int x = transition.getVisualPositionX();
		int y = transition.getVisualPositionY();
		writeSvgLine(writer, x, y, x, y + transition.getVisualHeight());
		writeSvgLine(writer, x, y, x + 10, y - 20);
		writeSvgLine(writer, x, y, x - 10, y - 20);
		writeSvgLine(writer, x - 10, y - 20, x + 10, y - 20);
		
		writeSvgText(writer, x - 10, y - 28, transition.getName());
	}
	
	@Override
	protected void writeArc(Writer writer, PlaceReference ref,
			Transition transition) throws IOException {
		
		Point prev = null;
		for (Point point : ref.getArc()) {
			if (prev != null) {
				writeSvgLine(writer, prev.getVisualPositionX(), prev.getVisualPositionY(), point.getVisualPositionX(), point.getVisualPositionY());
			}
			prev = point;
		}
		if (prev != null) {
			writeSvgArrowTo(writer, prev.getVisualPositionX(), prev.getVisualPositionY());
		}
	}
	
	@Override
	protected void writePlace(Writer writer, Place place) throws IOException {
		int x = place.getVisualPositionX();
		int y = place.getVisualPositionY();
		writer.write("<circle cx=\"" + x + "\" cy=\"" + y + "\" r=\"20\" style=\"stroke:#000000; fill:#FFFFFF\"/>");
		writeSvgText(writer, x - 20, y - 28, place.getName());
	}
	
	@Override
	protected void writeFooter(Writer writer) throws IOException {
		writer.write("</g>\n</svg>");
	}

}
