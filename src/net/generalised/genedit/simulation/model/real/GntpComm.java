package net.generalised.genedit.simulation.model.real;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class GntpComm {
	private final static String gntpHeader = "GNTP/0.1";
	private Socket connSocket;
	private OutputStream socketOutput;
	private BufferedReader socketInput;
	
	public GntpComm (Socket s) throws IOException {
		connSocket = s;
		socketOutput = connSocket.getOutputStream();
		socketInput = new BufferedReader(new InputStreamReader(connSocket.getInputStream())); 
	}
	
	public void performInit (String gnDoc) throws GntpException, IOException {

		GntpResponse response = null;
		
		sendRequest("INIT", gnDoc);
		
		response = readResponse();

		//da se nadqvame, 4e response ne e null?
		if (response.getCode() != 200) {
			//TODO: bi trqbvalo da ima spisak s gre6ki
			throw new GntpException(response.getCode()+" "+response.getDetails() + "\t" + response.getBody());//
			//TODO: ima li variant tova da ne e exception, a prosto spisak s gre6ki?
		}
	}
	
	public void performHalt() throws IOException {
		sendRequest("HALT", null);
	}

	//TODO: kak 6te vra6ta msg, 4e iska o6te danni (kod 300, kam nego i spisak s vars)
	public Node performStep(int count) throws GntpException, IOException {
		if (count == 1)
			sendRequest("STEP", null);
		else
			sendRequest("STEP " + count, null);//zasega tova nqma da var6i rabota, 6toto ne moje da se razgrani4i koi event ot koq stapka e
		
		GntpResponse response = readResponse();
		if (response.getCode() != 200) { //TODO: moje i da ne e gre6ka! 202(makar 4e tq ne idva sled STEP) i 300
			//...
			throw new GntpException(response.getCode()+" "+response.getDetails() + "\t" + response.getBody());//
		}
		
		Node node = parseXml(response.getBody());
		return node;
	}
	
/*	public Node performStepUntil(Class<T> eventType eventType, List<String> tokens,
			int maxSteps) throws GntpException, IOException {
		//TODO: da vra6ta Node, no eventualno s kod 202;
		//ama moje da doide i 300!!!...
		StringBuffer command = new StringBuffer("STEPUNTIL ");
		command.append(eventType.toString().toUpperCase());//
		command.append(' ');
		command.append(maxSteps);
		for (String token : tokens) {
			command.append(' ');
			command.append(token);
		}
		sendRequest(command.toString(), null);

		GntpResponse response = readResponse();
		if (response.getCode() != 200) { //TODO: moje i da ne e gre6ka! 202 i 300
			//...
			throw new GntpException(response.getCode()+" "+response.getDetails() + "\t" + response.getBody());//
		}
		
		Node node = parseXml(response.getBody());
		return node;
	}*/
	
	//TODO: performTokens; tokens as xml (string)
	
	//TODO: performInput; moje da vra6ta 201 ili 300?
	
	//TODO: performSave ?
	
	/**
	 * @param s - string to be sent. Should not contain Unicode characters.
	 * @throws IOException
	 */
	private void sendString (String s) throws IOException {
		//PrintWriter(socketOutput, true) ? leko, 4e ne hvarlq exceptions, a se proverqva s checkErrors
		socketOutput.write(s.getBytes(), 0, s.length());	
		socketOutput.flush();//dali ne pretovarvame taka???
	}
		
	private void sendRequest(String method, String body) throws IOException {
		sendString(gntpHeader + "\r\n");
		sendString(method+"\r\n");
		//pairs name-value
		if (body != null) {
			byte[] bytes = body.getBytes(Charset.forName("UTF-8"));
			sendString("Content-Length: " + bytes.length + "\r\n\r\n");
			socketOutput.write(bytes, 0, bytes.length);
			socketOutput.flush();
		} else {
			sendString("\r\n");
		}
	}

	private String readBytes(int contentLength) throws IOException {
		char[] responseBody = new char[contentLength];
		int bytesRead = 0;
		int offset = 0;
		do {
			bytesRead = socketInput.read(responseBody, offset, contentLength - offset);
			offset += bytesRead;
		} while (offset < contentLength && bytesRead > 0);
		return new String(responseBody);//TODO: encoding?
		
		//TODO: tickerserver\gntp01proxy.cpp, line 63
		//err = ::send (remoteSocket, msg, strlen(msg), 0);
		//dali ne trqbva i tam loop? s kratnost 1460 ili spored OS-a?
		//http://www.sockets.com/winsock.htm#Send - ne moje bezkraino dalgi raboti!
		//e dobre, ama za6to ne kazva, 4e sa prateni po-malko? (send vra6ta kolkoto trqbva, ne po-malko)
		//TODO: osven tova TickerServer, compiled in Debug mode, Run (not Debug) -
		//Debug Assertion Failed, L"Buffer is too small" && 0
		//when Debugging - no problems ?!?
	}
	
	private GntpResponse readResponse() throws IOException {
		String currentLine; 
		
		currentLine = socketInput.readLine();
		//byte protocolVersion =
		
		currentLine = socketInput.readLine();
		String responseCode = currentLine.substring(0, 3);//TODO: pri razpad na vrazkata hvarli NullPointerException
		String responseDetails = currentLine.substring(4);
		
		currentLine = socketInput.readLine();
		int contentLength = 0;
		if (currentLine.compareTo("") != 0) { //bad - use while instead
			String contentLengthName = "Content-Length: ";
			if (currentLine.startsWith(contentLengthName)) {
				String s = currentLine.substring(contentLengthName.length());
				contentLength = Integer.parseInt(s); 
			} //else - not implemented yet
			
			socketInput.readLine();
		}

		String responseBody = readBytes(contentLength);
		
		GntpResponse header = new GntpResponse((byte)0/**/, 
				Short.parseShort(responseCode), responseDetails, 
				contentLength, responseBody);
		return header;
	}
	
	//TODO: tuk li mu e mqstoto?
	private Node parseXml(String events) throws IOException, GntpException {
		Node node = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(events.getBytes());//getBytes("UTF-8");?
			Document document = builder.parse(is);
			//validate...
			node = document.getDocumentElement();
		} catch (SAXException e) {
			throw new GntpException("Cannot parse XML response:\n" + events, e);
		} catch (ParserConfigurationException e) {
			throw new GntpException("Cannot parse XML response:\n" + events, e);		}
		return node;
	}
}
