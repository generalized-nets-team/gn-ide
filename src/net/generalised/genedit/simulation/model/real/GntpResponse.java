package net.generalised.genedit.simulation.model.real;

class GntpResponse {
	private byte protocolVersion;

	private short code;

	private String details;

	private int contentLength; // zasega nqma drugi sa6testveni headers

	private String body;
	
	public GntpResponse(byte protocolVersion, short responseCode, String responseDetails, int contentLength, String responseBody) {
		super();
		this.protocolVersion = protocolVersion;
		this.code = responseCode;
		this.details = responseDetails;
		this.contentLength = contentLength;
		this.body = responseBody;
	}

	public int getContentLength() {
		return contentLength;
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}
	
	public String getProtocolVersionAsString() {
		return "" + (protocolVersion / 16) + "." + (protocolVersion % 16); 
	}

	public String getBody() {
		return body;
	}

	public short getCode() {
		return code;
	}

	public String getDetails() {
		return details;
	}
}
