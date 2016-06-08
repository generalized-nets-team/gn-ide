package net.generalised.genedit.model.dataaccess.xmlwrite;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.generalised.genedit.model.common.IntegerInf;
import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.dataaccess.xmlwrite.staxutils.IndentingXMLStreamWriter;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.GeneratorMode;
import net.generalised.genedit.model.gn.GnObjectCommon;
import net.generalised.genedit.model.gn.GnObjectWithDimensions;
import net.generalised.genedit.model.gn.GnObjectWithPosition;
import net.generalised.genedit.model.gn.TransitionMatrix;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Point;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.TokenGenerator;
import net.generalised.genedit.model.gn.Transition;

/**
 * Provides methods for serializing a GN model to XML.
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GnXmlWriter {
	
	private static GnXmlWriter instance;
	
	private GnXmlWriter() {
	}
	
	public static final synchronized GnXmlWriter getInstance() {
		if (instance == null) {
			instance = new GnXmlWriter();
		}
		return instance;
	}
	
	private void writeCommonAttributes(XMLStreamWriter xmlw,
			GnObjectCommon gnObject) throws XMLStreamException {
		xmlw.writeAttribute("id", gnObject.getId());
		//TODO: if name.equals(id) - moje da ne go e imalo pri 4eteneto...
		xmlw.writeAttribute("name", gnObject.getName().toString());
		xmlw.writeAttribute("priority", Integer.toString(gnObject.getPriority()));
	}

	private void writePositionAttributes(XMLStreamWriter xmlw,
			GnObjectWithPosition gnObject) throws XMLStreamException {
		xmlw.writeAttribute("positionX", "" + gnObject.getVisualPositionX());
		xmlw.writeAttribute("positionY", "" + gnObject.getVisualPositionY());
	}
	
	private void writeDimensionsAttributes(XMLStreamWriter xmlw,
			GnObjectWithDimensions gnObject) throws XMLStreamException {
		if (! (gnObject instanceof Transition)) {//
			xmlw.writeAttribute("sizeX", "" + gnObject.getVisualWidth());
		}
		xmlw.writeAttribute("sizeY", "" + gnObject.getVisualHeight());
	}
	
	private void writePlaceReference(XMLStreamWriter xmlw,
			String tag, List<PlaceReference> refs) throws XMLStreamException {
		xmlw.writeStartElement(tag + "s");
		for (PlaceReference r : refs) {
			List<Point> points = r.getArc();
			if (points.size() > 0) {
				xmlw.writeStartElement(tag);
				xmlw.writeAttribute("ref", r.getPlace().getId());
				xmlw.writeStartElement("arc");
				for (Point p : points) {
					xmlw.writeEmptyElement("point");
					writePositionAttributes(xmlw, p);
				}
				xmlw.writeEndElement();
				xmlw.writeEndElement();
			} else {
				xmlw.writeEmptyElement(tag);
				xmlw.writeAttribute("ref", r.getPlace().getId());
			}
		}
		xmlw.writeEndElement();
	}
	
	/**
	 * Serializes a GN model to a character {@link Writer}.
	 * @param gn a GN model
	 * ........................................
	 * @param writer a character write output stream
	 * @throws IOException if the content cannot be written to the stream
	 * @throws GnParseException if there is a problem with the GN model parsing
	 */
	public void gnToXml(GeneralizedNet gn, Writer writer, boolean saveState, boolean useCData) throws GnParseException {
		// TODO: if (saveState) save a snapshot

		try {
			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
			//xmlof.setProperty("javax.xml.stream.isPrefixDefaulting", Boolean.TRUE);
			XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(writer);
			xmlw = new IndentingXMLStreamWriter(xmlw);
			xmlw.writeStartDocument();
			
			xmlw.writeStartElement("gn");
			xmlw.writeDefaultNamespace("http://www.clbme.bas.bg/GN");//TODO: in XmlConstants.java
			xmlw.writeAttribute("name", gn.getName());
			xmlw.writeAttribute("time", Integer.toString(gn.getTime()));
			xmlw.writeAttribute("timeStart", Integer.toString(gn.getTimeStart()));
			xmlw.writeAttribute("timeStep", Integer.toString(gn.getTimeStep()));
			xmlw.writeAttribute("language", gn.getFunctionLanguage());
			xmlw.writeAttribute("root", Boolean.toString(gn.isRoot()));
			
			xmlw.writeStartElement("transitions");
			List<Transition> transitions = gn.getTransitions();
			for (Transition t: transitions) {
				xmlw.writeStartElement("transition");
				writeCommonAttributes(xmlw, t);
				xmlw.writeAttribute("startTime", Integer.toString(t.getStartTime()));
				xmlw.writeAttribute("lifeTime", Integer.toString(t.getLifeTime().getValue()));
				if (t.getType() != null && t.getType().length() > 0) {
					xmlw.writeAttribute("type", t.getType());
				}
				writePositionAttributes(xmlw, t);
				writeDimensionsAttributes(xmlw, t);
				
				List<PlaceReference> inputs = t.getInputs();
				writePlaceReference(xmlw, "input", inputs);
				List<PlaceReference> outputs = t.getOutputs();
				writePlaceReference(xmlw, "output", outputs);
				
				TransitionMatrix<FunctionReference> predicates = t.getPredicates();
				//if (! predicates.isEmpty()) {
					xmlw.writeStartElement("predicates");
					for (PlaceReference i : inputs)
						for (PlaceReference o : outputs) {
							FunctionReference p = predicates.getAt(i.getPlace(), o.getPlace()); 
							xmlw.writeStartElement("predicate");
							xmlw.writeAttribute("input", i.getPlace().getId());
							xmlw.writeAttribute("output", o.getPlace().getId());
							if (p != null) {
								//FIXME nqma6 takyvi metod...
								//xw.element("predicate", atts, p.getName());
								xmlw.writeCharacters(p.getFunctionName());
							} else {//xw.element("predicate", atts, "false");
								xmlw.writeCharacters("false");// TODO no longer needed
							}
							xmlw.writeEndElement();
						}
					xmlw.writeEndElement();
				//} else xw.element("predicates", null);
				
				TransitionMatrix<IntegerInf> capacities = t.getCapacities();
				boolean writeCapacities = false;//dali ne e po-dobre da gi ima vse pak? za po-lesno redaktirane?
				for (PlaceReference from : inputs) {
					if (! writeCapacities)
						for (PlaceReference to : outputs)
							if (! capacities.getAt(from.getPlace(), to.getPlace()).isPositiveInfinity()) {
								writeCapacities = true;
								break;
							}
				}
				if (writeCapacities) {
					StringBuffer c = new StringBuffer();
					for (PlaceReference from : inputs)
						for (PlaceReference to : outputs) {
							c.append(capacities.getAt(from.getPlace(), to.getPlace()).getValue());
							c.append(' ');
						}
					if (c.length() > 1)
						c.deleteCharAt(c.length() - 1);
					xmlw.writeStartElement("capacities");
					xmlw.writeCharacters(c.toString());
					xmlw.writeEndElement();
				}
				
				xmlw.writeEndElement();
			}
			xmlw.writeEndElement();
			
			xmlw.writeStartElement("places");
			List<Place> places = gn.getPlaces();
			for (Place p: places) {
				xmlw.writeEmptyElement("place");
				writeCommonAttributes(xmlw, p);
				xmlw.writeAttribute("capacity", "" + p.getCapacity().getValue());
				if (p.getCharFunction() != null) {
					xmlw.writeAttribute("char", p.getCharFunction().getFunctionName());
				}
				xmlw.writeAttribute("merge", Boolean.toString(p.isMerge()));
				if (p.getMergeRule() != null) {
					xmlw.writeAttribute("mergeRule", p.getMergeRule().getFunctionName());
				}
				writePositionAttributes(xmlw, p);
			}
			xmlw.writeEndElement();
			
			xmlw.writeStartElement("tokens");
			List<Token> tokens = gn.getTokens();
			for (Token t : tokens) {
				if (t instanceof TokenGenerator) {
					TokenGenerator generator = (TokenGenerator) t;
					xmlw.writeStartElement("generator");
					xmlw.writeAttribute("type", generator.getType().toString().toLowerCase());
					if (generator.getPredicate() != null && generator.getType() != GeneratorMode.PERIODIC) {
						xmlw.writeAttribute("predicate", generator.getPredicate().getFunctionName());
					}
					xmlw.writeAttribute("period", Integer.toString(generator.getPeriod()));
				} else {
					xmlw.writeStartElement("token");
				}
				writeCommonAttributes(xmlw, t);
				if (t.getHost() != null) {//TODO: when saving a snapshot... or host=""? schema does not allow this
					xmlw.writeAttribute("host", t.getHost().getId());
				}
				xmlw.writeAttribute("entering", Integer.toString(t.getEnteringTime()));
				xmlw.writeAttribute("leaving", Integer.toString(t.getLeavingTime().getValue()));
				Collection<Characteristic> chars = t.getChars();
				//TODO: if no chars -> xw.element
				for (Characteristic ch : chars) {
					xmlw.writeStartElement("char");
					xmlw.writeAttribute("name", ch.getName());
					xmlw.writeAttribute("type", ch.getType());
					xmlw.writeAttribute("history", Integer.toString(ch.getHistory().getValue()));
					xmlw.writeCharacters(ch.getValue());
					xmlw.writeEndElement();
					//TODO: how to save history? schema does not allow...
				}
				xmlw.writeEndElement();
			}
			xmlw.writeEndElement();
			
			//List<Function> functions = gn.getFunctions();
			String functions = gn.getFunctions();
			if (functions.length() > 0) {
				xmlw.writeStartElement("functions");
				if (useCData) {
//					for (Function f : functions) {
//						String definition = f.getDefinition();
//						xmlw.writeCData(definition); // TODO why separate CDATA for each function
//						//FIXME: In case of "]]>" in CDATA: break this into two CDATA sections
//						//<![CDATA[]]]]><![CDATA[>]]>
//						//The first <![CDATA[]]]]> has the ]]. The second <![CDATA[>]]> has the >
//					}
					xmlw.writeCData(functions);
				} else {
//					for (Function f : functions) {
//						String definition = f.getDefinition();
//						xmlw.writeCharacters(definition);
//					}
					xmlw.writeCharacters(functions);
				}
				xmlw.writeEndElement();
			} else {
				xmlw.writeEmptyElement("functions");
			}
			
//			xmlw.writeStartElement("visual-parameters");
//			xmlw.writeEndElement();
			
			xmlw.writeEndElement();
			
			xmlw.writeEndDocument();
			
			xmlw.close();
		} catch (XMLStreamException e) {
			throw new GnParseException(e);
		}
	}

	/**
	 * Serializes the given GN model to a XML file in UTF-8 format.
	 * @param gn a GN model
	 * ........................................
	 * @param fileName the file where the model should be saved
	 * @throws IOException if the file cannot be written
	 * @throws GnParseException if a GN parsing problem occurs
	 */
	public void gnToXml(GeneralizedNet gn, String fileName, boolean saveState, boolean useCData) throws IOException, GnParseException {
		//creates UTF-8, but does not put the BOM marker 0xEF 0xBB 0xBF
		Writer writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(fileName), "UTF-8"));
		gnToXml(gn, writer, saveState, useCData);
		writer.close();
	}
	
	/**
	 * Serializes the given GN model to a string with content in XML format (the
	 * same that is used in GN XML files)
	 * 
	 * @param gn a GN model
	 * ...................................................
	 * @return string with XML content
	 * @throws IOException if the file cannot be written
	 * @throws GnParseException if a GN parsing problem occurs
	 */
	public String gnToXml(GeneralizedNet gn, boolean saveState, boolean useCData) throws GnParseException {
		CharArrayWriter writer = new CharArrayWriter();
		gnToXml(gn, writer, saveState, useCData);
		String gnSerialized = writer.toString();
		return gnSerialized;
	}
}
