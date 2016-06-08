package net.generalised.genedit.fileexport;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.Transition;

import org.eclipse.swt.graphics.Rectangle;

public class TexGraphicalFileExporter extends GraphicalStructureFileExporter {

	private boolean usedInstance = false;
	
	private GeneralizedNet gn;
	
	private Rectangle bounds;
	
	private int maxY;
	
	private static int gcd(int a, int b) {
		if (a < 0)
			a = -a;
		if (b < 0)
			b = -b;
		if (a == 0)
			return b;
		if (b == 0)
			return a;
		while (a != b) {
			if (a > b)
				a -= b;
			else
				b -= a;
		}
		return a;
	}
	
	private static void writeTeXLine(Writer writer, int x1, int y1, int x2, int y2) throws IOException {
		// Standard LaTeX can only draw lines with slope = x/y, where x and y have integer values from -6 through 6.
		// The smallest slanted line which LaTeX can draw is a line of length 10 points (about 3.5mm). LaTeX will draw nothing if you try to draw slanted lines less than this length.
		int xSlope = (x2 - x1);
		int ySlope = (y2 - y1);
		int length = Math.abs(xSlope);
		if (length == 0)
			length = Math.abs(ySlope);
		int gcd = gcd(xSlope, ySlope);
		xSlope /= gcd;
		ySlope /= gcd;
		writer.write("\\put(" + x1 + "," + y1 + "){\\line(" + xSlope + "," + ySlope + "){" + length + "}}\n");
	} 
	
	private static void writeTeXArrowTo(Writer writer, int x, int y) throws IOException {
		writer.write("\\put(" + (x - 1) + "," + y + "){\\vector(1,0){1}}\n");
	}

	private static void writeTeXText(Writer writer, int x, int y, String text) throws IOException {
		
		// TODO use ll, cc is for center-center!
		writer.write("\\put(" + x + "," + y + "){\\makebox(0,0)[cc]{$" + text + "$}}\n");
	}
	
	private static Rectangle getBoundingRectangle(GeneralizedNet gn) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		int x, y;

		// TODO triangles, labels, visual parameters...
		
		for (Transition t : gn.getTransitions()) {
			x = t.getVisualPositionX();
			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			
			y = t.getVisualPositionY();
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
			
			List<PlaceReference> refs = t.getInputsAndOutputs();
			for (PlaceReference ref : refs) {
				List<Point> arc = ref.getArc();
				for (Point p : arc) {
					x = p.getVisualPositionX();
					if (x < minX) {
						minX = x;
					}
					if (x > maxX) {
						maxX = x;
					}
					
					y = p.getVisualPositionY();
					if (y < minY) {
						minY = y;
					}
					if (y > maxY) {
						maxY = y;
					}
				}
			}
			
		}
		
		Rectangle result = new Rectangle(minX - 30, minY - 30, 
				maxX - minX + 60, maxY - minY + 60);
		return result;
	}
	
	@Override
	protected void init(GeneralizedNet generalizedNet) {
		super.init(generalizedNet);
		
		if (usedInstance) {
			throw new IllegalStateException("Cannot use the same exporter for different GNs.");
		}
		usedInstance = true;
		
		this.gn = generalizedNet;
		this.bounds = getBoundingRectangle(generalizedNet);
		this.maxY = bounds.height + bounds.y;
	}
	
	@Override
	protected void writeHeader(Writer writer) throws IOException {
		// use [!t] to align figure to top of page, [!b] to bottom, [h] - here
		// use \\tiny if the GN is too large
		
		writer.write("\\begin{figure}[h]\\centering\n\\unitlength 0.25mm\n\\linethickness{0.4pt}\n\\begin{picture}(" 
				+ this.bounds.width + "," + this.bounds.height + ")\n");
	}
	
	@Override
	protected void writeTransition(Writer writer, Transition transition) throws IOException {
		int x = transition.getVisualPositionX() - bounds.x;
		int y = maxY - transition.getVisualPositionY();
		writeTeXLine(writer, x, y + 1, x, y - transition.getVisualHeight());
		writer.write("\\put(" + x + "," + (y + 2) + "){\\vector(0,-1){1}}\n");
		//writeTeXLine(writer, x, y, x + 16, y + 20);
		//writeTeXLine(writer, x, y, x - 16, y + 20);
		//writeTeXLine(writer, x - 16, y + 20, x + 16, y + 20);
		
		writeTeXText(writer, x - 3, y + 25, transition.getName().toTexString());
	}

	@Override
	protected void writeArc(Writer writer, PlaceReference ref,
			Transition transition) throws IOException {
		
		Point prev = null;
		boolean isFirst = true;

		List<Point> arc = ref.getArc();
		int pointsCount = arc.size();
		if (pointsCount <= 1)
			return;
		for (int i = 0; i < pointsCount; i++) {
			Point point = arc.get(i);
			if (prev != null) {
				int x1 = prev.getVisualPositionX() - bounds.x;
				int y1 = maxY - prev.getVisualPositionY();
				int x2 = point.getVisualPositionX() - bounds.x;
				int y2 = maxY - point.getVisualPositionY();
				if (ref.isInputFromThisPlace()) {
					if (isFirst)
						x1 += 15;
				} else {
					if (i == pointsCount - 1) {
						x2 -= 17; //20 + 2 for the arrow
					}
				}
				writeTeXLine(writer, x1, y1, x2, y2);
				isFirst = false;
			}
			prev = point;
		}
		
		int arrowX;
		int arrowY;
		if (ref.isInputFromThisPlace()) {
			arrowX = prev.getVisualPositionX() - bounds.x;
			arrowY = maxY - prev.getVisualPositionY();
		} else {
			arrowX = prev.getVisualPositionX() - 15 - bounds.x;
			arrowY = maxY - prev.getVisualPositionY();
		}
		writeTeXArrowTo(writer, arrowX, arrowY);
	}
	
	@Override
	protected void writePlace(Writer writer, Place place) throws IOException {
		
		int x = place.getVisualPositionX() - bounds.x;
		int y = maxY - place.getVisualPositionY();
		
		writer.write("\\put(" + x + "," + y + "){\\circle{" + 30 + "}}\n");
		writeTeXText(writer, x - 12, y + 20, place.getName().toTexString());
	}
	
	@Override
	protected void writeFooter(Writer writer) throws IOException {
		writer.write("\n\\end{picture}\n\\caption{" + gn.getName() + "}\n\\label{fig:gn}\n\\end{figure}");
	}
}
