import java.util.List;

import net.generalised.genedit.api.GeneralizedNetFacade;
import net.generalised.genedit.api.JavaFunction;
import net.generalised.genedit.api.JavaGeneralizedNet;
import net.generalised.genedit.api.JavaGnEvent;
import net.generalised.genedit.api.JavaSimulation;
import net.generalised.genedit.api.JavaToken;
import net.generalised.genedit.api.SimulationEventsListener;
import net.generalised.genedit.api.builder.GeneralizedNetBuilder;
import net.generalised.genedit.model.gn.GeneralizedNet;



public class Test {

	private static JavaGeneralizedNet constructSampleGn() {
		JavaGeneralizedNet gn = new GeneralizedNetBuilder("Test")
				.addTransition("Z1").addInput("l1").addToken("alpha").addCharacteristic("test", "string", 1)
				.addOutput("l2")
				.setPredicate("l1", "l2", new JavaFunction("w12") {

					@Override
					public Object run(GeneralizedNet net, JavaToken token) {
						System.out.println("nice!");
						return true;
					}
				}).build();

		return gn;
	}
	
	public static void main(String[] args) throws Exception {
		
		JavaGeneralizedNet gn = constructSampleGn();
		
//		JavaGeneralizedNet gn2 = new GeneralizedNetBuilder("dams")
//				.addTransition("Z1").setTransitionPriority(5).addInput("l1")
//				.setPlaceCapacity(1).addOutput("l2").addOutput("l3")
//				.setPredicate("l1", "l3", new JavaFunction("W13") {
//					@Override
//					public Object run(GeneralizedNet net, JavaToken token) {
//						return true;
//					}
//				}).addTransition("Z2").addInput("l2").addOutput("l4").build();
		
		JavaSimulation simulation = GeneralizedNetFacade.startSimulation(gn, new SimulationEventsListener() {
			@Override
			public void handleEvent(List<JavaGnEvent> events) {
				// System.out.println(events.size() + " events");
				for (JavaGnEvent event: events) {
					System.out.println("event: " + event.getClass().getSimpleName() + " " + event.getToken().getId() + " " + event.getChars().size());
				}
			}
		});
		
		simulation.step(1);

		System.out.println(gn.getToken("alpha").getHost().getId());
		
		simulation.step(1);
		
		System.out.println(gn.getToken("alpha").getHost().getId());
		
		simulation.step(1);
		
		System.out.println(gn.getToken("alpha").getHost()); // null
		
		simulation.stop();

	}

}
