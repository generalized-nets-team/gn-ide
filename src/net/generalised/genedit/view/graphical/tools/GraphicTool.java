package net.generalised.genedit.view.graphical.tools;

import java.util.HashMap;
import java.util.Map;

import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.RichTextName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.cloudgarden.resource.SWTResourceManager;

public abstract class GraphicTool {
	
	protected final GC gc;
	protected final GeneralizedNet gn;
	protected final GnObject gnObject;
	protected final double zoom; //TODO: bad, bad...
	protected final Point canvasOrigin;
	
	public GraphicTool(GeneralizedNet gn, GnObject gnObject, GC gc,
			double zoom, Point canvasOrigin) {
		super();
		this.gc = gc;
		this.gn = gn;
		this.gnObject = gnObject;
		this.zoom = zoom;
		this.canvasOrigin = canvasOrigin;
	}

	public abstract void draw();
	
//	/**
//	 * Default implementation: just change the color
//	 */
//	public void drawMouseOver() {
//		Color defaultColor = gc.getForeground();
//		gc.setForeground(SWTResourceManager.getColor(32, 160, 240));
//		draw();
//		gc.setForeground(defaultColor);
//	}
	
	//TODO: difference not only in color - hooks, ...
	public void drawSelected() {
		Color defaultColor = gc.getForeground();
		gc.setForeground(SWTResourceManager.getColor(32, 64, 240));
		draw();
		gc.setForeground(defaultColor);
	}
	
	/**
	 * x, y - real GN coordinates, not screen coordinates, do not need conversion
	 */
	public abstract boolean isUnder(int x, int y);
	
	public abstract String getToolTipText();
	
	public Rectangle getRedrawArea() {
		return new Rectangle(0, 0, 0, 0);
	}

	protected int getX(double x) {
		return canvasOrigin.x + (int)(zoom*x);
	}
	
	protected int getY(double y) {
		return canvasOrigin.y + (int)(zoom*y);
	}

	private final static Map<String, Color> defaultColors = new HashMap<String, Color>();
	
	//TODO: tuk li mu e mqstoto?
	public static Color parseColor(String color) {
		Color result = null;
		if (defaultColors.size() == 0) {
			defaultColors.put("blue", SWTResourceManager.getColor(0, 0, 0xFF));
			defaultColors.put("green", SWTResourceManager.getColor(0, 0x80, 0));
			defaultColors.put("red", SWTResourceManager.getColor(0xFF, 0, 0));
			defaultColors.put("yellow", SWTResourceManager.getColor(0xFF, 0xFF, 0));
			defaultColors.put("white", SWTResourceManager.getColor(0xFF, 0xFF, 0xFF));
			defaultColors.put("black", SWTResourceManager.getColor(0, 0, 0));
			defaultColors.put("gray", SWTResourceManager.getColor(0x80, 0x80, 0x80));
			//TODO: more colors... must be accessible from menu...
		}
		result = defaultColors.get(color.toLowerCase());
		//FIXME: isDigit works only for 0-9; this should be isHexDigit; and why only for 1st?
		if (result == null && color.length() == 6/* && Character.isDigit(color.charAt(0))*/) {
			int v = Integer.valueOf(color, 16);
			int red = (v & 0xFF0000) >> 16;
			int green = (v & 0xFF00) >> 8;
			int blue = v & 0xFF;
			result = SWTResourceManager.getColor(red, green, blue);
		}
		return result;
	}
	
	/**
	 * @param text
	 * @param fontSize
	 * @param x already zoomed
	 * @param y
	 * @return the size of drawn text
	 */
	protected Point drawLabel(String text, int fontSize, int fontStyle, int x, int y) {
		Font currentFont = gc.getFont();
		String fontName = currentFont.getFontData()[0].getName();
		Font font = SWTResourceManager.getFont(fontName, (int) (zoom * fontSize), fontStyle);
		gc.setFont(font);
		gc.drawString(text, x, y, true);
		Point result = gc.textExtent(text);
		gc.setFont(currentFont);
		return result;
	}
	
	protected void drawRichLabel(RichTextName text, int x, int y) {
		
		final int mainFontSize = 9;
		final int subscriptFontSize = 7;
		final double offset = 3;
		
		Point mainPartSize = drawLabel(text.getMainPart(), mainFontSize, SWT.ITALIC, x, y);
		drawLabel(text.getSubscriptPart(), subscriptFontSize, 0, x + mainPartSize.x + 1, (int)(y + offset * zoom));
	}
}
