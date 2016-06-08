package net.generalised.genedit.view.graphical.tools;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.Transition;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class TransitionTool extends GraphicTool {

	public TransitionTool(GeneralizedNet gn, GnObject gnObject, GC gc,
			double zoom, Point canvasOrigin) {
		super(gn, gnObject, gc, zoom, canvasOrigin);
	}

	@Override
	public void draw() {
		Transition t = (Transition)gnObject;
		int positionX = t.getVisualPositionX();
		int positionY = t.getVisualPositionY();
		int sizeY = t.getVisualHeight();
		net.generalised.genedit.model.gn.Point triangleSizeGnPoint = gn.getVisualParameters().getTransitionTriangleSize();
		int triangleSizeX = triangleSizeGnPoint.getVisualPositionX();
		int triangleSizeY = triangleSizeGnPoint.getVisualPositionY();
		gc.fillPolygon(new int[]{
				getX(positionX), getY(positionY),
				getX(positionX - triangleSizeX/2), getY(positionY - triangleSizeY),
				getX(positionX + triangleSizeX/2) /*TODO: if odd...*/, getY(positionY - triangleSizeY),
				});
		gc.drawPolyline(new int[]{
				getX(positionX), getY(positionY + sizeY),
				getX(positionX), getY(positionY),
				getX(positionX - triangleSizeX/2), getY(positionY - triangleSizeY),
				getX(positionX + triangleSizeX/2) /*TODO: if odd...*/, getY(positionY - triangleSizeY),
				getX(positionX), getY(positionY)
				});

		int textX = getX(positionX - triangleSizeX / 2);
		int textY = getY(positionY - triangleSizeY - 12);
		drawRichLabel(t.getName(), textX, textY);

		//TODO: y-coordinate?
		//TODO: if name L1, automatically type L<sub>1</sub> ???
	}

	@Override
	public Rectangle getRedrawArea() {
		Transition transition = (Transition)gnObject;
		net.generalised.genedit.model.gn.Point triangleSizeGnPoint = gn.getVisualParameters().getTransitionTriangleSize();
		int width = triangleSizeGnPoint.getVisualPositionX();
		int trHeight = triangleSizeGnPoint.getVisualPositionY() + 10;//10 sux
		int x = transition.getVisualPositionX() - width / 2 - 1;
		int y = transition.getVisualPositionY() - trHeight;
		Rectangle result = new Rectangle(x, y, width + 2, trHeight + transition.getVisualHeight() + 1);
		return result;
	}

	@Override
	public boolean isUnder(int x, int y) {
		Transition transition = (Transition)gnObject;
		//TODO: the little triangle...
		int x1 = transition.getVisualPositionX();
		int y1 = transition.getVisualPositionY();
		int y2 = transition.getVisualHeight() + y1; 
		y1 -= gn.getVisualParameters().getTransitionTriangleSize().getVisualPositionY();
		return Math.abs(x - x1) < 3 && y <= y2 && y >= y1;
	}

	@Override
	public String getToolTipText() {
		Transition transition = (Transition)gnObject;
		StringBuffer result = new StringBuffer();
		result.append(transition.getId());
		result.append("\nPriority: ");
		result.append(transition.getPriority());
		//...
		return result.toString();
	}
}
