package net.generalised.genedit.model.gn;

/**
 * Common GN properties.
 * 
 * @author Dimitar Dimitrov
 *
 */
public abstract class GnObjectCommon extends GnObjectWithId {

	private RichTextName name;

	private int priority;
	
	public RichTextName getName() {
		if (name != null && !name.isEmpty()) {
			return name;
		}
		else {
			String id = getId();
			RichTextName richTextName = new RichTextName(id);
//			if (! id.equals(richTextName.toString())) {
//				this.name = richTextName;
//			}
			return richTextName;
		}
	}

	public void setName(RichTextName name) {
		this.name = name;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		if (priority < 0)
			throw new IllegalArgumentException("priority cannot be negative");
		this.priority = priority;
	}
	
	public GnObjectCommon() {
		this("", "", 0);
	}
	
	public GnObjectCommon(String id, String name, int priority) {
		setId(id);
		this.name = new RichTextName("");
		this.priority = 0;
	}
}
