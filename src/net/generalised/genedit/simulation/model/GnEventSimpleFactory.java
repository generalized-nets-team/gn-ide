package net.generalised.genedit.simulation.model;

import java.util.ArrayList;
import java.util.List;

import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Token;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GnEventSimpleFactory {
	private GeneralizedNet gn;
	
	public GnEventSimpleFactory(GeneralizedNet gn) {
		this.gn = gn;
	}
	
	private List<Characteristic> parseCharacteristics(Element element) {
		NodeList charNodes = element.getElementsByTagName("char");
		List<Characteristic> chars = new ArrayList<Characteristic>();//initial capacity? nie go znaem, no da li 6te e optimalno? 
		for (int index = 0; index < charNodes.getLength(); ++index) {
			Element currentChar = (Element)charNodes.item(index);
			String charName = currentChar.getAttribute("name");
			String charType = currentChar.getAttribute("type");
			String charValue = currentChar.getTextContent();
			//TODO: moje da ima i drugi parametri?
			Characteristic characteristic = new Characteristic(charType, 1);//TODO: how deep history?
			characteristic.setName(charName);
			characteristic.setValue(charValue);
			chars.add(characteristic);
		}
		//vij default stoinosti za char parametrite... 4e moje da vleze char, koqto q e nqmalo?
		return chars;
	}
	
	public GnEvent createEvent(Node node) {
		GnEvent event = null;
		String type = node.getNodeName();
		Element element = (Element)node;
		if (type.equals("entrance") || type.equals("movement") || type.equals("exit")) {
			List<Characteristic> chars = parseCharacteristics(element);
			
			String placeId = element.getAttribute("place");
			String tokenId = element.getAttribute("token");
			//TODO: trqbva6e da ima i atribut step!!! ama serverat ne go dava!
			Token token = gn.getTokens().get(tokenId);
			if (type.equals("entrance")) {
				if (token == null) {
					token = new Token(tokenId, null);
					gn.getTokens().add(token);
				}
				event = new EnterEvent(gn, token, gn.getPlaces().get(placeId), chars);
			} else if (type.equals("movement"))
				event = new MoveEvent(gn, token, gn.getPlaces().get(placeId), chars);
			else if (type.equals("exit"))
				event = new LeaveEvent(gn, token, gn.getPlaces().get(placeId), chars);
		} else if (type.equals("merge")) {
			List<Characteristic> chars = parseCharacteristics(element);
			String newTokenId = element.getAttribute("newtoken");
			String oldTokenId = element.getAttribute("oldtoken");
			String placeId = element.getAttribute("place");
			//TODO: trqbva6e da ima i atribut step!!! ama serverat ne go dava!
			//TODO: kakvo se pravi sega? sazdava li se newtoken???
			event = new MergeEvent(gn, gn.getPlaces().get(placeId), gn.getTokens().get(newTokenId), 
					gn.getTokens().get(oldTokenId), chars);
		} else {
			throw new IllegalArgumentException("Invalid event " + type);
			//TODO: InvalidEventException?
		}
		return event;
	}
}
