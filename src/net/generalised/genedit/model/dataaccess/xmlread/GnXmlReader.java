package net.generalised.genedit.model.dataaccess.xmlread;

import static net.generalised.genedit.view.Constants.RESOURCES_PATH;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.gn.GeneralizedNet;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.ParserAdapter;

/**
 * 
 * Provides methods for loading a GN model
 * 
 * @author Dimitar Dimitrov
 *
 */
public class GnXmlReader {
	
	private static GnXmlReader instance;
	
	private GnXmlReader() {
	}
	
	public static final synchronized GnXmlReader getInstance() {
		if (instance == null) {
			instance = new GnXmlReader();
		}
		return instance;
	}
	
	public static final String GN_XSD = RESOURCES_PATH + "GNschema.xsd";
	public static final String GN_EDIT_XSD = RESOURCES_PATH + "GNEditSchema.xsd";
		
	/**
	 * Validates the given XML file against the default XML Schema.
	 * 
	 * @param fileName XML file, containing a GN model
	 * @param editMode if true, .........................
	 * @throws IOException if the file cannot be open
	 * @throws GnParseException if the file is not valid
	 */
	public void validateXml(String fileName, boolean editMode) throws IOException, GnParseException {
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			URL schemaUrl;
			if (editMode) {
				schemaUrl = this.getClass().getClassLoader().getResource(GN_EDIT_XSD);
			} else {
				schemaUrl = this.getClass().getClassLoader().getResource(GN_XSD);
			}
			Schema schema = factory.newSchema(schemaUrl);
			Validator validator = schema.newValidator();
			String url = getUrlFromFileName(fileName);
			Source source = new StreamSource(url); //XXX requires Java 1.6? Use SAXSource? 
			validator.validate(source);
		} catch (SAXException e) {
			//FIXME: file not closed
			throw new GnParseException(e.getMessage(), e);
		} catch (NullPointerException e) {
			throw new GnParseException(e.getMessage(), e);
		}
	}
	
	/**
	 * Parses a given XML file and returns a {@link GeneralizedNet} instance.
	 * @param fileName XML file with GN model saved in it
	 * @return a {@link GeneralizedNet} object which represents the model
	 * @throws IOException if the file cannot be open
	 * @throws GnParseException if the file is not valid
	 */
	// TODO static?
	public GeneralizedNet xmlToGn(String fileName) throws IOException, GnParseException {
		GeneralizedNet gn = null;
		
//		try {
//			validateXml(fileName);
//			XMLInputFactory factory = XMLInputFactory.newInstance();
//			factory.setProperty("javax.xml.stream.isValidating", "true");//TODO: we already have validation?
//			FileReader fileReader = new FileReader(fileName);
//			XMLStreamReader reader = factory.createXMLStreamReader(fileReader);
//			
//		} catch (XMLStreamException e) {
//			throw new GnParseException(e);
//		}
		
		try {
			validateXml(fileName, true);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			GnHandler handler = new GnHandler();
			pa.setContentHandler(handler);
			// TODO: xml can be gn before simulation or snapshot
			String url = getUrlFromFileName(fileName);
			sp.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
			pa.parse(url);
			gn = handler.getGn();
		} catch (SAXException e) {//TODO: after validation this is not expected
			throw new GnParseException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new GnParseException(e.getMessage(), e);
		}
		return gn;
	}

	// TODO reuse code!
	public GeneralizedNet xmlStringToGn(String string) throws GnParseException {
		GeneralizedNet gn = null;
		
//		try {
//			validateXml(fileName);
//			XMLInputFactory factory = XMLInputFactory.newInstance();
//			factory.setProperty("javax.xml.stream.isValidating", "true");//TODO: we already have validation?
//			FileReader fileReader = new FileReader(fileName);
//			XMLStreamReader reader = factory.createXMLStreamReader(fileReader);
//			
//		} catch (XMLStreamException e) {
//			throw new GnParseException(e);
//		}
		
		try {
			//validateXml(fileName, true); TODO
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			GnHandler handler = new GnHandler();
			pa.setContentHandler(handler);
			// TODO: xml can be gn before simulation or snapshot
			sp.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
			pa.parse(new InputSource(new StringReader(string)));
			gn = handler.getGn();
		} catch (SAXException e) {//TODO: after validation this is not expected
			throw new GnParseException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new GnParseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new GnParseException(e.getMessage(), e);
		}
		return gn;
	}
	
	/**
	 * Constructs a URL from a given file name
	 * @param fileName local file name
	 * @return URL reprsenting the same file
	 */
	private String getUrlFromFileName(String fileName) {
		File file = new File(fileName);
		URI uri = file.toURI();
		return uri.toString();
	}
}
