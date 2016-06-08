package net.generalised.genedit.view.graphical.tools;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.Place;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class PlaceTool extends GraphicTool {

	public PlaceTool(GeneralizedNet gn, GnObject gnObject, GC gc, double zoom,
			Point canvasOrigin) {
		super(gn, gnObject, gc, zoom, canvasOrigin);
	}

	@Override
	public void draw() {
		Place p = (Place)gnObject;
		int positionX = p.getVisualPositionX();
		int positionY = p.getVisualPositionY();
		int radius = gn.getVisualParameters().getPlaceRadius();
		gc.fillOval(getX(positionX - radius), getY(positionY - radius),
				(int)(zoom*(2 * radius)), (int)(zoom*(2 * radius)));
		gc.drawOval(getX(positionX - radius), getY(positionY - radius),
				(int)(zoom*(2 * radius) - 1), (int)(zoom*(2 * radius) - 1));
		
		int textX = getX(positionX - radius);
		int textY = getY(positionY - radius - 12);
		drawRichLabel(p.getName(), textX, textY);
		
//		Font currentFont = gc.getFont();
//		Font font = SWTResourceManager.getFont(currentFont.getFontData()[0].getName(), (int)(zoom*8), 0);
//		gc.setFont(font);
//		// http://www.java2s.com/Tutorial/Java/0300__SWT-2D-Graphics/UsingFontMetricstogetcharwidth.htm
//		// get width of main part so to know where to place subscript!
//		gc.drawString(p.getName().getMainPart(), getX(positionX - radius),
//				getY(positionY - radius - 12), true);
//		//FontMetrics fm = gc.getFontMetrics();
//		//fm.getAverageCharWidth()
//		gc.textExtent(p.getName().getMainPart());
//		gc.setFont(currentFont);
//		//TODO: y-coordinate?
	}

	@Override
	public Rectangle getRedrawArea() {
		Place place = (Place)gnObject;
		//TODO: uf, ami texta?
		int radius = 1 + gn.getVisualParameters().getPlaceRadius();		
		int x = place.getVisualPositionX() - radius;
		int y = place.getVisualPositionY() - radius - 10;//tova 10 go opravi, zaradi texta e!
		int width = 2 * radius;
		int height = 2 * radius + 10;//i tova
		Rectangle result = new Rectangle(x, y, width, height);
		return result;
	}

	@Override
	public boolean isUnder(int x, int y) {
		Place place = (Place)gnObject;
		int placeX = place.getVisualPositionX();
		int placeY = place.getVisualPositionY();
		int radius = 3 + gn.getVisualParameters().getPlaceRadius();
		return (Math.abs((x - placeX)*(x - placeX))
				+ Math.abs((y - placeY)*(y - placeY))
				<= radius*radius);
	}

	//TODO: iterate over properties, show all annotated with @GnProperty
	// do this in the base class; override only if needed
	@Override
	public String getToolTipText() {
		Place place = (Place)gnObject;
		StringBuffer result = new StringBuffer();
		result.append(place.getId());
		result.append("\nCapacity: ");
		result.append(place.getCapacity());
		result.append("\nPriority: ");
		result.append(place.getPriority());
		//...
		
//		List<Object> values = GnPropertiesUtil.getGnPropertiesValues(gnObject);
//		// TODO: names?
//		for (Object value : values) {
//			result.append(value);
//			result.append('\n');//without the last
//		}
		
		return result.toString();
	}
}
