package net.generalised.genedit.model.gn;

public class TokenGenerator extends Token {
	private int period;

	private FunctionReference predicate;

	private GeneratorMode type;

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		if (period < 1)
			throw new IllegalArgumentException("period must be positive");
		this.period = period;
	}

	public FunctionReference getPredicate() {
		return predicate;
	}

	public void setPredicate(FunctionReference predicate) {
		this.predicate = predicate;
	}

	public GeneratorMode getType() {
		return type;
	}

	public void setType(GeneratorMode type) {
		this.type = type;
	}
	
	public TokenGenerator(String id, Place host, GeneratorMode type) {
		super(id, host);
		this.period = 1;
		this.type = type;
		// XXX
		this.predicate = new FunctionReference("true"); 
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		TokenGenerator result = (TokenGenerator)super.clone();
		return result;
	}
}
