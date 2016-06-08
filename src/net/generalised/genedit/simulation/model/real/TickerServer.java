package net.generalised.genedit.simulation.model.real;

import java.net.*;
import java.io.*;

public class TickerServer {
	// autostart server (executable file specified in the configuration file?)

	public TickerServer(String address, int port) {

		this.address = address;
		this.port = port;
	}

	public GntpComm connect() throws UnknownHostException, IOException {
		//TODO: add more exceptions - to distinguish error creating socket or reading data
		Socket mySocket = new Socket(address, port);
		return new GntpComm(mySocket);
	}

	private String address;

	private int port;
}
