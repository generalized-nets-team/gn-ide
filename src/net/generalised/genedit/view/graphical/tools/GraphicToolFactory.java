package net.generalised.genedit.view.graphical.tools;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

public class GraphicToolFactory {
	
	public static GraphicTool getTool(GeneralizedNet gn, GnObject gnObject, GC gc, double zoom,
			Point canvasOrigin) {
		GraphicTool tool = null;
		if (gnObject instanceof Place)
			tool = new PlaceTool(gn, gnObject, gc, zoom, canvasOrigin);
		else if (gnObject instanceof Transition)
			tool = new TransitionTool(gn, gnObject, gc, zoom, canvasOrigin);
		else if (gnObject instanceof PlaceReference)
			tool = new ArcTool(gn, gnObject, gc, zoom, canvasOrigin);
		else if (gnObject instanceof Token)
			tool = new TokenTool(gn, gnObject, gc, zoom, canvasOrigin);
		//else throw exception
		return tool;
	}
}
