package net.generalised.genedit.model.gn.transitiontype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class TransitionTypeMultiNode implements TransitionTypeNode {

	private final List<TransitionTypeNode> children;
	private List<TransitionTypeNode> readOnlyChildren;
	
	protected abstract String getOperationSymbol();
	
	public TransitionTypeMultiNode() {
		this.children = new ArrayList<TransitionTypeNode>(); 
		this.readOnlyChildren = Collections.unmodifiableList(this.children);
	}
	
	public void addChild(TransitionTypeNode node) {
		this.children.add(node);
		this.readOnlyChildren = Collections.unmodifiableList(this.children);
	}
	
	public List<TransitionTypeNode> getChildren() {
		return this.readOnlyChildren;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(getOperationSymbol()).append('(');
		int childrenCount = this.children.size();
		for (int i = 0; i < childrenCount; i++) {
			TransitionTypeNode child = this.children.get(i);
			result.append(child.toString());
			if (i < childrenCount - 1) {
				result.append(',');
			}
		}
		result.append(')');
		return result.toString();
	}
}
