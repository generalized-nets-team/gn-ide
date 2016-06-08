package net.generalised.genedit.model.gn;

public interface GnObjectWithPosition {

	// TODO: positions can be double? keep in mind that!
	
	public int getVisualPositionX();

	public int getVisualPositionY();

	public void setVisualPositionX(int visualPositionX);

	public void setVisualPositionY(int visualPositionY);
}
