package net.generalised.genedit.simulation.model.real;

import java.io.IOException;

import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.dataaccess.xmlwrite.GnXmlWriter;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.simulation.model.GnEvent;
import net.generalised.genedit.simulation.model.GnEventSimpleFactory;
import net.generalised.genedit.simulation.model.GnEvents;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RealSimulation extends Simulation {
	private GntpComm connection;
	
	public RealSimulation(GnDocument document, GeneralizedNet gn, GntpComm connection)
			throws SimulationException {
		super(document, gn);
		this.connection = connection;
		String gnSerialized;
		try {
			gnSerialized = GnXmlWriter.getInstance().gnToXml(gn, false, false);
			//TODO: validate xml with GNSchema!
			//Reader reader = new StringReader(gnSerialized);
			//GnXmlReader.getInstance().validateXml(reader, false);
		} catch (GnParseException e) {
			throw new SimulationException(e);
		} 
		try {
			connection.performInit(gnSerialized);
		} catch (GntpException e) {
			//TODO: parse error messages...
			throw new SimulationException(e);
		} catch (IOException e) {
			throw new SimulationException("Connection broken", e);//TODO: this message is used in many places
		}
	}
	
	@Override
	public void closeEventSource() throws SimulationException {
		try {
			connection.performHalt(); //This does not close the connection!
		} catch (IOException e) {
			throw new SimulationException("Cannot stop the simulation.", e);
		}
	}
	
	@Override
	protected GnEvents getNextEvents(int count) throws SimulationException {
		Node node = null;
		try {
			node = connection.performStep(count);
		} catch (GntpException e) {
			//TODO: parse error messages...
			throw new SimulationException(e);
		} catch (IOException e) {
			throw new SimulationException("Connection broken", e);//TODO: this message is used in many places		}
		}
		
		NodeList eventsNodes = node.getChildNodes();
		GnEventSimpleFactory factory = new GnEventSimpleFactory(getGn());
		GnEvents events = new GnEvents(getGn());
		for (int i = 0; i < eventsNodes.getLength(); ++i) {
			Node currentNode = eventsNodes.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				GnEvent currentEvent = factory.createEvent(currentNode);
				events.add(currentEvent);
			}
		}
		return events;
	}
	
	//TODO: implement STEPUNTIL in TickerServer; future: STEPUNTIL <Predicate> - this should be server-side
//	@Override
//	public boolean stepUntil(Class<T> eventType eventType, int maxSteps, List<Token> tokens) {
//		List<String> tokenIds = new ArrayList<String>();
//		for (Token token : tokens) {
//			tokenIds.add(token.getId());
//		}
//		//TODO: ...
//		return true;
//	}
	
	//TODO: insertTokens; hmm, List<Token> li da idva, ili serialized???; response idva li?
	
	//TODO: input; moje da vra6ta 201 s List<Node> ili 300? s List of vars
	
	//TODO: save ?
}
