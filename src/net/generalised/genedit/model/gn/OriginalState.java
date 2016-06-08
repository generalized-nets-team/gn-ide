package net.generalised.genedit.model.gn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.generalised.genedit.model.common.IntegerInf;

public class OriginalState {
	//for runtimeState: (obsolete)
//	TODO: current places for each host (initial - null)
//	TODO: characteristic history (without original values, but with current)
//	TODO: getValue(token, characteristic), ...
	
	private List<Token> tokens;
	private Map<String, Integer> placeCapacities;
	// TODO Capacities matrices! 
	
	public OriginalState(GeneralizedNet gn) {
		//TODO: serialize...
		List<Token> originalTokens = gn.getTokens();
		tokens = new ArrayList<Token>(originalTokens.size());
		try {
			for (Token t : originalTokens) {
				tokens.add((Token) t.clone());
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.placeCapacities = new HashMap<String, Integer>();
		for (Place place : gn.getPlaces()) {
			this.placeCapacities.put(place.getId(), place.getCapacity().getValue());
		}
	}
	
	public void restore(GeneralizedNet gn) {
		GnList<Token> gnTokens = gn.getTokens();
		gnTokens.clear();
		for (Token t : tokens) {
			gnTokens.add(t);
		}
		
		for (Place place : gn.getPlaces()) {
			Integer capacity = this.placeCapacities.get(place.getId());
			place.setCapacity(new IntegerInf(capacity.intValue()));
		}		
	}
}
