package net.generalised.genedit.simulation.model;

import java.util.List;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.dataaccess.GnCloneUtil;
import net.generalised.genedit.model.dataaccess.GnParseException;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.SimulationConfigProperties;

public abstract class Simulation {
	private final GnDocument document; //TODO: is this needed?
	private final GeneralizedNet gn;
	private boolean running;
	private Thread stepThread;
	private boolean paused;
	private int delay;
	protected final GeneralizedNet backupGn; // XXX

	public Simulation(GnDocument document, GeneralizedNet gn) throws SimulationException {
		this.document = document;
		this.gn = gn;
		this.running = false;
		this.delay = Integer.parseInt(SettingsManager.getInstance()
				.getProperty(SimulationConfigProperties.DEFAULT_DELAY_TIME));

		try {
			// XXX keep in mind that cloning via XML serialization followed by deserialization will not clone JavaFunction definitions 
			this.backupGn = GnCloneUtil.cloneValidGn(gn);
		} catch (GnParseException e) {
			throw new SimulationException(e);
		}
	}
	
	public void start() {
		if (running)
			return;
		
		// TODO: serialize to xml, restore into another GN object and use it for the simulation? hmm
		gn.prepareForSimulation(); //this cannot be called by the constructor, because all hosts become null before serializing to XML
		
		gn.setCurrentTime(gn.getTimeStart());
		running = true;
		paused = false;
		document.updateViews();
	}

	//start, stop, step, current step, go, step n...

	public void step(int count) throws SimulationException {
		if (stepThread != null && stepThread.isAlive())
			return;
		threadSafeStep(count);
	}
	
	protected synchronized void threadSafeStep(int count) throws SimulationException {
		if (paused)
			return;
		GnEvents events = getNextEvents(count);
		document.execute(events);
	}
	
	//http://java.sun.com/j2se/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
	
	public void go() throws SimulationException {
		if (stepThread != null && stepThread.isAlive())//if (paused)
			return;//TODO: stop the thread
		//TODO: if paused, the step loop should be changeable - for example: stepUntil, pause, go - run infinite loop
		//TODO: combine with stepUntil(condition:null, maxSteps:MAX_INT?)
		
		if (stepThread == null || ! stepThread.isAlive()) {
			stepThread = new Thread(new Runnable() {
				
				public void run() {
					//while (true) {
					int currentTime = gn.getCurrentTime();//TODO: leko s ierarhi4nite...
					int maxTime = gn.getTime(); // TODO start + time?
					int timeStep = gn.getTimeStep();
					for (int i = currentTime + timeStep; i <= maxTime; i += timeStep) {
						try {
							while(paused && running) {//TODO: something smarter
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									throw new RuntimeException(e); // TODO catch it, re-throw it as SimulationException
								}
							}
							
							if (!running)
								return;
							
							threadSafeStep(1);
							
						} catch (SimulationException e1) {
							//http://en.allexperts.com/q/Java-1046/2008/2/checked-exception-anonymous-classes.htm
							e1.printStackTrace();
							throw new RuntimeException(e1);
							//break;
						}
						try {
							Thread.sleep(delay);//TODO: dali e koqto trqbva ni6ka?
							//TODO: tuk li mu e mqstoto? tova e presentation logic!
						} catch (InterruptedException e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
			}, "Go");
			stepThread.start();
			//TODO: 6te ima eventualno i nujda ot input
		}
	}
	
	/**
	 * @param eventType
	 * @param maxSteps
	 * @param tokens
	 * @return true, iff the event occurs before reaching the maximum number of steps.
	 * @throws SimulationException 
	 */
	public <T extends GnEvent> boolean stepUntil(Class<T> eventType, int maxSteps, 
			List<Token> tokens) throws SimulationException {
		boolean result = false;
		for (int step = 0; step < maxSteps; ++step) {//TODO: && currTime < gn.getTime
			threadSafeStep(1);//TODO: delay?
			//TODO: passive input
			//stop on exception...
			//TODO: by default - client side event detection? 
			//	required for recorded simulation;
			//	also because TickerServer does not implement STEPUNTIL method
			//TODO: multithreading
		}
		return result;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void pause() {
		//TODO if running loop - go / stepuntil...
		if (stepThread != null && stepThread.isAlive()) {
			paused = true;
		}
	}
	
	public void resume() {
		if (stepThread != null && stepThread.isAlive()) {
			paused = false;
		}
	}
	
	public void stop() throws SimulationException {
		//TODO: if (stepThread != null && stepThread.isAlive()) - stop it
		gn.restoreState(); //FIXME: in NeftochimDemo current time was not reset after simulation stop!
		gn.setCurrentTime(gn.getTimeStart());
		running = false;
		document.updateViews();
		closeEventSource();
	}
	
	//TODO: can be confused with Pause... when paused, Simulation is still running
	public boolean isRunning() {
		return running;
	}
	
	public int getDelay() {
		return delay;
	}

	public void setDelay(int milliseconds) {
		this.delay = milliseconds;
	}

	public GeneralizedNet getGn() {
		return gn;
	}
	
	public GnDocument getDocument() {
		return document;
	}
	
	//Template Design Pattern
	
	protected abstract GnEvents getNextEvents(int count) throws SimulationException;
	
	protected abstract void closeEventSource() throws SimulationException;
}
