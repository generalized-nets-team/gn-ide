package net.generalised.genedit.model.gn;

public class FunctionReference {

	private final String functionName;
	
	public FunctionReference(String functionName) {
		if (functionName == null) {
			this.functionName = "";
		} else {
			this.functionName = functionName;
		}
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof FunctionReference && 
				this.functionName.equals(((FunctionReference) obj).functionName);
	}
	
	@Override
	public int hashCode() {
		return this.functionName.hashCode();
	}
}
