package net.generalised.genedit.view.graphical.tools;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GnObject;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.cloudgarden.resource.SWTResourceManager;

public class TokenTool extends GraphicTool {

	public TokenTool(GeneralizedNet gn, GnObject gnObject, GC gc, double zoom,
			Point canvasOrigin) {
		super(gn, gnObject, gc, zoom, canvasOrigin);
	}

	static final double sqrt3 = Math.sqrt(3.0);
	static final double sqrt2minus1 = Math.sqrt(2.0) - 1.0;
	
	@Override
	public void draw() {
		Token t = (Token)gnObject;
		Place place = t.getHost();
		if (place != null) {
			int tokensCount = 0;
			int tokenIndex = 0; //first/second/etc. token in this place
			// TODO use gn.getTokensAt ?
			for (Token token : gn.getTokens()) {
				if (token.getHost() != null && token.getHost().getId().equals(place.getId())) {
					tokensCount++;
					if (token.getId().equals(t.getId())) {
						tokenIndex = tokensCount;
					}
				}
			}

			double positionX = place.getVisualPositionX();
			double positionY = place.getVisualPositionY();
			
			if (tokensCount > 4) {
				//TODO: centering
				if (tokenIndex == 1) {
					Font currentFont = gc.getFont();
					Font font = SWTResourceManager.getFont(currentFont.getFontData()[0].getName(), (int)(zoom*8), 0);
					gc.setFont(font);
					gc.drawString(Integer.toString(tokensCount), getX(positionX - 6), getY(positionY - 7), true);
					gc.setFont(currentFont);
				}
			} else {
				//TODO: this is calculated too many times...
				double tokenRadius = sqrt2minus1 * gn.getVisualParameters().getPlaceRadius();
				
				if (tokensCount == 2) {
					if (tokenIndex == 1)
						positionX -= tokenRadius;
					else positionX += tokenRadius;
				} else if (tokensCount == 3) {
					if (tokenIndex == 1) {
						positionY -= 2.0 / sqrt3 * tokenRadius;
					} else {
						positionY += tokenRadius / sqrt3;
						if (tokenIndex == 2)
							positionX -= tokenRadius;
						else positionX += tokenRadius;
					}
				} else if (tokensCount == 4) {
					if (tokenIndex % 2 == 1)
						positionX -= tokenRadius;
					else positionX += tokenRadius;
					if (tokenIndex <= 2)
						positionY -= tokenRadius;
					else positionY += tokenRadius;
				}
				
				Color prevBackground = gc.getBackground();
				Characteristic tokenColor = t.getChars().get("Color");
				Color tokenBackground = null;
				if (tokenColor != null) {
					String str = tokenColor.getValue();
					tokenBackground = GraphicTool.parseColor(str);
				}
				if (tokenBackground == null)
					tokenBackground = SWTResourceManager.getColor(0, 0, 0);
				gc.setBackground(tokenBackground);
				gc.fillOval(getX(positionX - tokenRadius), getY(positionY - tokenRadius),
						(int)(zoom*(2 * tokenRadius)), (int)(zoom*(2 * tokenRadius)));
				gc.setBackground(prevBackground);
				
				//TODO: tezi stoinosti mnogo zavisqt ot 6rifta
				Characteristic defaultChar = t.getDefaultCharacteristic();
				if (defaultChar != null) {
					//dali sa v red RGB ili BGR?
					double y = 0.299 * tokenBackground.getRed()
							+ 0.587 * tokenBackground.getGreen()
							+ 0.114 * tokenBackground.getRed();
					Color prevForeground = gc.getForeground();
					if (y > 128)
						gc.setForeground(SWTResourceManager.getColor(0, 0, 0));
					else
						gc.setForeground(SWTResourceManager.getColor(255, 255, 255));
					gc.setBackground(tokenBackground);
					String str;
					if (defaultChar.getValue().length() <= 2)
						str = defaultChar.getValue();
					else
						str = "..";
					
					Font currentFont = gc.getFont();
					Font font = SWTResourceManager.getFont(currentFont.getFontData()[0].getName(), (int)(zoom*8.5), 0);
					gc.setFont(font);
					gc.drawString(str, getX(positionX - ((str.length() == 2) ? 7 : 3)), getY(positionY - 7), true);
					gc.setFont(currentFont);
					
					gc.setForeground(prevForeground);
					gc.setBackground(prevBackground);
				}
				
				//if selected, this would be another color:
				//int linewidth = gc.getLineWidth(); kofti - ili 6te e 4erno, ili sinio, a i udebelqva tokena
				//gc.setLineWidth((int)(zoom * tokenRadius / 2));
				gc.drawOval(getX(positionX - tokenRadius), getY(positionY - tokenRadius),
						(int)(zoom*(2 * tokenRadius) - 1), (int)(zoom*(2 * tokenRadius) - 1));
				//gc.setLineWidth(linewidth);
			}
		}
	}

	@Override
	public Rectangle getRedrawArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUnder(int x, int y) {
		Token token = (Token) gnObject;
		Place host = token.getHost();
		if (host != null) {
			//FIXME: this is more complex - if we have more than one token, it is not centered!
			int placeX = host.getVisualPositionX();
			int placeY = host.getVisualPositionY();
			int radius = gn.getVisualParameters().getPlaceRadius() / 2;
			return (Math.abs((x - placeX)*(x - placeX))
					+ Math.abs((y - placeY)*(y - placeY))
					<= radius*radius);

		}
		return false;
	}
	
	@Override
	public String getToolTipText() {
		final int MAX_CHAR_LENGTH = 128;
		Token token = (Token) gnObject;
		StringBuilder result = new StringBuilder(token.getId());
		for (Characteristic ch : token.getChars()) {
			String charValue = ch.getValue();
			if (charValue.length() > MAX_CHAR_LENGTH)
				charValue = charValue.substring(0, MAX_CHAR_LENGTH) + "...";
			result.append('\n').append(ch.getName())
					.append(": ").append(charValue);
		}
		return result.toString();
	}
}
