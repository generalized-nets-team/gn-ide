package net.generalised.genedit.view.graphical.tools;

import java.util.List;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class ArcTool extends GraphicTool {

	public ArcTool(GeneralizedNet gn, GnObject gnObject, GC gc, double zoom,
			org.eclipse.swt.graphics.Point canvasOrigin) {
		super(gn, gnObject, gc, zoom, canvasOrigin);
	}

	@Override
	public void draw() {
		PlaceReference ref = (PlaceReference)gnObject;
		List<Point> arc = ref.getArc();
		int pointsCount = arc.size();
		if (pointsCount > 1) {
			//first or last line - clipped by the circle
			for (int i = 1; i < pointsCount; i++) {
				gc.drawLine(getX(arc.get(i-1).getVisualPositionX()), getY(arc.get(i-1).getVisualPositionY()), 
						getX(arc.get(i).getVisualPositionX()), getY(arc.get(i).getVisualPositionY()));
			}
			int x = arc.get(pointsCount - 1).getVisualPositionX();
			int y = arc.get(pointsCount - 1).getVisualPositionY();

			//TODO: determine arc direction, calculate arrow size based on other visual properties?
			Place place = ref.getPlace();
			if (! ref.isInputFromThisPlace()) {
				x -= gn.getVisualParameters().getPlaceRadius();
			}
			
			gc.drawLine(getX(x), getY(y), getX(x - 8), getY(y - 4));
			gc.drawLine(getX(x), getY(y), getX(x - 8), getY(y + 4));
		} //else {
//			Color current = gc.getForeground();
//			gc.setForeground(SWTResourceManager.getColor(192, 192, 192));
//			Place place = ref.getPlace();
//			int x1, y1, x2, y2;
//			if (ref.isInputFromThisPlace()) {
//				x1 = place.getVisualPositionX();
//				y1 = place.getVisualPositionY();
//				x2 = place.getRightTransition().getVisualPositionX();
//				y2 = y1;
//			} else {
//				x2 = place.getVisualPositionX();
//				y2 = place.getVisualPositionY();
//				x1 = place.getLeftTransition().getVisualPositionX();
//				y1 = y2;
//			}
//			gc.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));
//			gc.setForeground(current);
//		}
	}

	@Override
	public Rectangle getRedrawArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUnder(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getToolTipText() {
		return null;
	}
}
