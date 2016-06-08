package net.generalised.genedit.simulation.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import net.generalised.genedit.baseapp.SettingsManager;
import net.generalised.genedit.controller.ViewUtil;
import net.generalised.genedit.demo.DemoMain;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.errors.Problem;
import net.generalised.genedit.model.gn.Characteristic;
import net.generalised.genedit.model.gn.JavaScriptFunctionFactory;
import net.generalised.genedit.simulation.SimulationConfigProperties;
import net.generalised.genedit.simulation.model.RecordedSimulation;
import net.generalised.genedit.simulation.model.Simulation;
import net.generalised.genedit.simulation.model.SimulationException;
import net.generalised.genedit.simulation.model.embedded.EmbeddedSimulation;
import net.generalised.genedit.simulation.model.embedded.javascript.JavaScriptRunner;
import net.generalised.genedit.simulation.model.real.GntpComm;
import net.generalised.genedit.simulation.model.real.RealSimulation;
import net.generalised.genedit.simulation.model.real.TickerServer;
import net.generalised.genedit.simulation.watch.model.Watch;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.MainForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;


/**
 * @author Dimitar Dimitrov
 *
 */
public class SimulationController {

	private static TickerServer tickerServer; //TODO: here?
	
	private static GntpComm openGntpConnection() {
		SettingsManager settingsManager = SettingsManager.getInstance();
		String tickerServerHost = settingsManager.getProperty(SimulationConfigProperties.TICKER_SERVER_HOST);
		if (tickerServer == null) {
			//TODO: thread safe!
			int port = Integer.parseInt(settingsManager.getProperty(SimulationConfigProperties.TICKER_SERVER_PORT));
			tickerServer = new TickerServer(tickerServerHost, port);
		}
		GntpComm connection = null;
		try {
			connection = tickerServer.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			ViewUtil.showErrorMessageBox("Cannot find host " + tickerServerHost);
		} catch (IOException e) {
			e.printStackTrace();
			// TODO if run local is set to true, try to run local server
			ViewUtil.showErrorMessageBox("Cannot connect.\nCheck if the server is running.\nCheck your firewall settings.");
		}
		return connection;
	}
	
	public static void startRealSimulation(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			if (document.isInSimulationMode()) {
				ViewUtil.showErrorMessageBox("Please stop the currently running simulation.");
				return;
			}

			Simulation simulation = null;
			
			if (ViewUtil.checkErrorsWithoutMessage(view)) {
				//XXX temp code:
				if (DemoMain.inDemoMode) {
					simulation = DemoMain.demo.createSimulation(document);
				} else
				try {
					if (! JavaScriptFunctionFactory.LANGUAGE.equals(view.getCurrentGnView().getGn().getFunctionLanguage())) {
						GntpComm connection = openGntpConnection();
						if (connection != null) {
							simulation = new RealSimulation(document, document.getModel(), connection);
							//TODO:handle xml validation exceptions! different schema is used!
						}
					} else {
						simulation = new EmbeddedSimulation(document, document.getModel(), JavaScriptRunner.getInstance());
					}
				} catch (SimulationException e) {
					e.printStackTrace();
					ViewUtil.showErrorMessageBox("Error in GN model.");//TODO: ili ne6to drugo?
					//TODO: tuk trqbva da moje da pokazva po-podroben spisak s errors
				}
				
				if (simulation != null) {
					document.setSimulation(simulation);
					document.resetCommandHistory();
					simulation.start();
					//TODO: pri gre6ka 4xx da zatvarq vrazkata! (pri 5xx i HALT tova e avtomati4no)
				}
			}
		}
	}

	public static void startRecordedSimulation(MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			MessageBox box = new MessageBox(view.getUIComponent().getShell(), SWT.OK | SWT.ICON_WARNING);
			box.setText(Constants.APPLICATION_NAME);
			box.setMessage("Warning! ...");//TODO: da e za sa6tiq xml i da ne e redaktiran!
			box.open();
			
			//TODO: duplicated code
			if (document.isInSimulationMode()) {
				ViewUtil.showErrorMessageBox("Please stop the currently running simulation.");
				return;
			}
			
			// TODO: if current simulation is RecordedSimulation (terminated), the user can re-run it with 'Go'
			String[] filterExtensions = {"*.xml", "*.*"};
			FileDialog fileDialog = new FileDialog(view.getUIComponent().getShell(), SWT.OPEN);
			fileDialog.setText("Open Recorded Simulation");
			fileDialog.setFilterExtensions(filterExtensions);
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				//fileDialog.open() allows to type a name of nonexistent file!
				//!Longer actions, such as doing file access, require separate threads (otherwise - "Not responding")
				//code repeat!
				//if success:
				try {
					Simulation simulation = new RecordedSimulation(document, document.getModel(), selectedFile);
					document.setSimulation(simulation);
					document.resetCommandHistory();
					simulation.start();
					//TODO: clear Messages list (also for RealSimulation) or Observer again? or button for manually cleaning?
				} catch (SimulationException e) {
					ViewUtil.showExceptionMessageBox(e);
				}
			}
		}
	}

	public static void simulationGo(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			if (! document.isInSimulationMode())
				startRealSimulation(view);
			final Simulation simulation = document.getSimulation();
			if (simulation != null)
				try {
					simulation.go();
				} catch (SimulationException e) {
					//TODO: add errors to ProblemsComposite
					e.printStackTrace();
					ViewUtil.showErrorMessageBox("Cannot execute step.");
				}
		}
	}
	
	public static void simulationStep(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			if (! document.isInSimulationMode())
				startRealSimulation(view);
			final Simulation simulation = document.getSimulation();
			if (simulation != null)
				try {
					simulation.step(1);
				} catch (SimulationException e1) {
					//TODO: add errors to ProblemsComposite
					e1.printStackTrace();
					ViewUtil.showErrorMessageBox("Cannot execute step.");
				}
		}
	}
	
	public static void simulationStepUntil(MainForm view) {
//		Document document = view.getDocument();
//		if (document != null) {
//			if (! document.isInSimulationMode())
//				startRealSimulation(view);
//			final Simulation simulation = document.getSimulation();
//			if (simulation != null)
				//try {
					//TODO: ...
					//TODO: UI for specifying event
					//if (! stepUntil(...)) MessageBox("event did not occurred");
//				} catch (SimulationException e1) {
//					//TODO: add errors to ProblemsComposite
//					showErrorMessageBox("Cannot execute step.");
//					e1.printStackTrace();
//				}
//		}
	}

	public static void simulationPauseResume(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			if (document.isInSimulationMode()) {
				Simulation simulation = document.getSimulation();
				if (simulation.isPaused())
					simulation.resume();
				else
					simulation.pause();
			}
		}
	}

	public static void simulationStop(MainForm view) {
		GnDocument document = view.getDocument();
		if (document != null) {
			if (document.isInSimulationMode())
				try {
					document.getSimulation().stop();
				} catch (SimulationException e) {
					view.getCurrentGnView()
						.addProblem(new Problem("Cannot stop the simulation.", false));
					e.printStackTrace();
				}
		}
	}
	
	private static final Color[] lineColors = {
			new Color(null, 0, 0, 255), // blue
			new Color(null, 255, 0, 0), // red
			new Color(null, 0, 128, 0), // green
			new Color(null, 0, 128, 128), // dark cyan
			new Color(null, 255, 255, 0), // yellow
			new Color(null, 0, 128, 255), // light blue
			new Color(null, 128, 0, 128), // violet
			new Color(null, 255, 128, 0), // orange
			new Color(null, 128, 128, 128), // gray
			new Color(null, 0, 0, 128), // dark blue
			new Color(null, 128, 128, 0), // gold
			new Color(null, 255, 0, 255), // magenta
			new Color(null, 0, 128, 0), // light green
			new Color(null, 255, 128, 255) // pink
	};

	public static void addWatch(MainForm view) {

		GnDocument document = view.getDocument();
		if (document != null) {
			
			List<Object> selection = view.getCurrentGnView().getSelection().getObjects();
			// TODO use Selection.containsOnly
			if (selection.size() == 0 || ! (selection.get(0) instanceof Characteristic)) {
				
				MessageBox box = new MessageBox(Display.getDefault()
						.getActiveShell(), SWT.OK | SWT.ICON_WARNING);
				box.setText(Constants.APPLICATION_NAME);
				box.setMessage("Please select one or more characteristics first.");
				box.open();

			} else {

				Shell watchWindow = new Shell(view.getUIComponent().getShell(), SWT.SHELL_TRIM); 
						//| SWT.H_SCROLL | SWT.V_SCROLL);
				watchWindow.setSize(512, 240); //XXX
				//watchWindow.setLocation(0, 500);
				watchWindow.setLayout(new FillLayout());
				// TODO close listener - deregister as observer; dispose?
				
				// TODO draw custom legend - default implementation in SWTChart is buggy
				
				// TODO? Drag'n'Drop chars over the window?

				Chart chart = new Chart(watchWindow, SWT.NONE);
				chart.getAxisSet().getXAxis(0).getTitle().setText("");
				chart.getAxisSet().getYAxis(0).getTitle().setText("");
				
				String title = "";

				int colorIndex = 0;
				
				for (Object object : selection) {
					if (object instanceof Characteristic) {
						
						Characteristic characteristic = (Characteristic) object;

						Color color = lineColors[colorIndex];
				        colorIndex = (colorIndex + 1) % lineColors.length;

						Watch watch = new Watch(characteristic, color);
		
						if (title.length() > 0) {
							title += ", ";
						}
						title += characteristic.getName();
		
						double[] data = watch.getActualData();
						
				        ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet()
		                		.createSeries(SeriesType.LINE, characteristic.getName());
				        lineSeries.setYSeries(data);

				        // XXX temporary ugly patch:
//				        if ("EColiFedBatchCultivationProcess".equals(view.getCurrentGnView().getGn().getName())) {
//							String[] stringArray = "6.69 6.692 6.694 6.696 6.698 6.7 6.702 6.704 6.706 6.708 6.71 6.712 6.714 6.716 6.718 6.72 6.722 6.724 6.726 6.728 6.73 6.732 6.734 6.736 6.738 6.74 6.742 6.744 6.746 6.748 6.75 6.752 6.754 6.756 6.758 6.76 6.762 6.764 6.766 6.768 6.77 6.772 6.774 6.776 6.778 6.78 6.782 6.784 6.786 6.788 6.79 6.792 6.794 6.796 6.798 6.8 6.802 6.804 6.806 6.808 6.81 6.812 6.814 6.816 6.818 6.82 6.822 6.824 6.826 6.828 6.83 6.832 6.834 6.836 6.838 6.84 6.842 6.844 6.846 6.848 6.85 6.852 6.854 6.856 6.858 6.86 6.862 6.864 6.866 6.868 6.87 6.872 6.874 6.876 6.878 6.88 6.882 6.884 6.886 6.888 6.89 6.892 6.894 6.896 6.898 6.9 6.902 6.904 6.906 6.908 6.91 6.912 6.914 6.916 6.918 6.92 6.922 6.924 6.926 6.928 6.93 6.932 6.934 6.936 6.938 6.94 6.942 6.944 6.946 6.948 6.95 6.952 6.954 6.956 6.958 6.96 6.962 6.964 6.966 6.968 6.97 6.972 6.974 6.976 6.978 6.98 6.982 6.984 6.986 6.988 6.99 6.992 6.994 6.996 6.998 7 7.002 7.004 7.006 7.008 7.01 7.012 7.014 7.016 7.018 7.02 7.022 7.024 7.026 7.028 7.03 7.032 7.034 7.036 7.038 7.04 7.042 7.044 7.046 7.048 7.05 7.052 7.054 7.056 7.058 7.06 7.062 7.064 7.066 7.068 7.07 7.072 7.074 7.076 7.078 7.08 7.082 7.084 7.086 7.088 7.09 7.092 7.094 7.096 7.098 7.1 7.102 7.104 7.106 7.108 7.11 7.112 7.114 7.116 7.118 7.12 7.122 7.124 7.126 7.128 7.13 7.132 7.134 7.136 7.138 7.14 7.142 7.144 7.146 7.148 7.15 7.152 7.154 7.156 7.158 7.16 7.162 7.164 7.166 7.168 7.17 7.172 7.174 7.176 7.178 7.18 7.182 7.184 7.186 7.188 7.19 7.192 7.194 7.196 7.198 7.2 7.202 7.204 7.206 7.208 7.21 7.212 7.214 7.216 7.218 7.22 7.222 7.224 7.226 7.228 7.23 7.232 7.234 7.236 7.238 7.24 7.242 7.244 7.246 7.248 7.25 7.252 7.254 7.256 7.258 7.26 7.262 7.264 7.266 7.268 7.27 7.272 7.274 7.276 7.278 7.28 7.282 7.284 7.286 7.288 7.29 7.292 7.294 7.296 7.298 7.3 7.302 7.304 7.306 7.308 7.31 7.312 7.314 7.316 7.318 7.32 7.322 7.324 7.326 7.328 7.33 7.332 7.334 7.336 7.338 7.34 7.342 7.344 7.346 7.348 7.35 7.352 7.354 7.356 7.358 7.36 7.362 7.364 7.366 7.368 7.37 7.372 7.374 7.376 7.378 7.38 7.382 7.384 7.386 7.388 7.39 7.392 7.394 7.396 7.398 7.4 7.402 7.404 7.406 7.408 7.41 7.412 7.414 7.416 7.418 7.42 7.422 7.424 7.426 7.428 7.43 7.432 7.434 7.436 7.438 7.44 7.442 7.444 7.446 7.448 7.45 7.452 7.454 7.456 7.458 7.46 7.462 7.464 7.466 7.468 7.47 7.472 7.474 7.476 7.478 7.48 7.482 7.484 7.486 7.488 7.49 7.492 7.494 7.496 7.498 7.5 7.502 7.504 7.506 7.508 7.51 7.512 7.514 7.516 7.518 7.52 7.522 7.524 7.526 7.528 7.53 7.532 7.534 7.536 7.538 7.54 7.542 7.544 7.546 7.548 7.55 7.552 7.554 7.556 7.558 7.56 7.562 7.564 7.566 7.568 7.57 7.572 7.574 7.576 7.578 7.58 7.582 7.584 7.586 7.588 7.59 7.592 7.594 7.596 7.598 7.6 7.602 7.604 7.606 7.608 7.61 7.612 7.614 7.616 7.618 7.62 7.622 7.624 7.626 7.628 7.63 7.632 7.634 7.636 7.638 7.64 7.642 7.644 7.646 7.648 7.65 7.652 7.654 7.656 7.658 7.66 7.662 7.664 7.666 7.668 7.67 7.672 7.674 7.676 7.678 7.68 7.682 7.684 7.686 7.688 7.69 7.692 7.694 7.696 7.698 7.7 7.702 7.704 7.706 7.708 7.71 7.712 7.714 7.716 7.718 7.72 7.722 7.724 7.726 7.728 7.73 7.732 7.734 7.736 7.738 7.74 7.742 7.744 7.746 7.748 7.75 7.752 7.754 7.756 7.758 7.76 7.762 7.764 7.766 7.768 7.77 7.772 7.774 7.776 7.778 7.78 7.782 7.784 7.786 7.788 7.79 7.792 7.794 7.796 7.798 7.8 7.802 7.804 7.806 7.808 7.81 7.812 7.814 7.816 7.818 7.82 7.822 7.824 7.826 7.828 7.83 7.832 7.834 7.836 7.838 7.84 7.842 7.844 7.846 7.848 7.85 7.852 7.854 7.856 7.858 7.86 7.862 7.864 7.866 7.868 7.87 7.872 7.874 7.876 7.878 7.88 7.882 7.884 7.886 7.888 7.89 7.892 7.894 7.896 7.898 7.9 7.902 7.904 7.906 7.908 7.91 7.912 7.914 7.916 7.918 7.92 7.922 7.924 7.926 7.928 7.93 7.932 7.934 7.936 7.938 7.94 7.942 7.944 7.946 7.948 7.95 7.952 7.954 7.956 7.958 7.96 7.962 7.964 7.966 7.968 7.97 7.972 7.974 7.976 7.978 7.98 7.982 7.984 7.986 7.988 7.99 7.992 7.994 7.996 7.998 8 8.002 8.004 8.006 8.008 8.01 8.012 8.014 8.016 8.018 8.02 8.022 8.024 8.026 8.028 8.03 8.032 8.034 8.036 8.038 8.04 8.042 8.044 8.046 8.048 8.05 8.052 8.054 8.056 8.058 8.06 8.062 8.064 8.066 8.068 8.07 8.072 8.074 8.076 8.078 8.08 8.082 8.084 8.086 8.088 8.09 8.092 8.094 8.096 8.098 8.1 8.102 8.104 8.106 8.108 8.11 8.112 8.114 8.116 8.118 8.12 8.122 8.124 8.126 8.128 8.13 8.132 8.134 8.136 8.138 8.14 8.142 8.144 8.146 8.148 8.15 8.152 8.154 8.156 8.158 8.16 8.162 8.164 8.166 8.168 8.17 8.172 8.174 8.176 8.178 8.18 8.182 8.184 8.186 8.188 8.19 8.192 8.194 8.196 8.198 8.2 8.202 8.204 8.206 8.208 8.21 8.212 8.214 8.216 8.218 8.22 8.222 8.224 8.226 8.228 8.23 8.232 8.234 8.236 8.238 8.24 8.242 8.244 8.246 8.248 8.25 8.252 8.254 8.256 8.258 8.26 8.262 8.264 8.266 8.268 8.27 8.272 8.274 8.276 8.278 8.28 8.282 8.284 8.286 8.288 8.29 8.292 8.294 8.296 8.298 8.3 8.302 8.304 8.306 8.308 8.31 8.312 8.314 8.316 8.318 8.32 8.322 8.324 8.326 8.328 8.33 8.332 8.334 8.336 8.338 8.34 8.342 8.344 8.346 8.348 8.35 8.352 8.354 8.356 8.358 8.36 8.362 8.364 8.366 8.368 8.37 8.372 8.374 8.376 8.378 8.38 8.382 8.384 8.386 8.388 8.39 8.392 8.394 8.396 8.398 8.4 8.402 8.404 8.406 8.408 8.41 8.412 8.414 8.416 8.418 8.42 8.422 8.424 8.426 8.428 8.43 8.432 8.434 8.436 8.438 8.44 8.442 8.444 8.446 8.448 8.45 8.452 8.454 8.456 8.458 8.46 8.462 8.464 8.466 8.468 8.47 8.472 8.474 8.476 8.478 8.48 8.482 8.484 8.486 8.488 8.49 8.492 8.494 8.496 8.498 8.5 8.502 8.504 8.506 8.508 8.51 8.512 8.514 8.516 8.518 8.52 8.522 8.524 8.526 8.528 8.53 8.532 8.534 8.536 8.538 8.54 8.542 8.544 8.546 8.548 8.55 8.552 8.554 8.556 8.558 8.56 8.562 8.564 8.566 8.568 8.57 8.572 8.574 8.576 8.578 8.58 8.582 8.584 8.586 8.588 8.59 8.592 8.594 8.596 8.598 8.6 8.602 8.604 8.606 8.608 8.61 8.612 8.614 8.616 8.618 8.62 8.622 8.624 8.626 8.628 8.63 8.632 8.634 8.636 8.638 8.64 8.642 8.644 8.646 8.648 8.65 8.652 8.654 8.656 8.658 8.66 8.662 8.664 8.666 8.668 8.67 8.672 8.674 8.676 8.678 8.68 8.682 8.684 8.686 8.688 8.69 8.692 8.694 8.696 8.698 8.7 8.702 8.704 8.706 8.708 8.71 8.712 8.714 8.716 8.718 8.72 8.722 8.724 8.726 8.728 8.73 8.732 8.734 8.736 8.738 8.74 8.742 8.744 8.746 8.748 8.75 8.752 8.754 8.756 8.758 8.76 8.762 8.764 8.766 8.768 8.77 8.772 8.774 8.776 8.778 8.78 8.782 8.784 8.786 8.788 8.79 8.792 8.794 8.796 8.798 8.8 8.802 8.804 8.806 8.808 8.81 8.812 8.814 8.816 8.818 8.82 8.822 8.824 8.826 8.828 8.83 8.832 8.834 8.836 8.838 8.84 8.842 8.844 8.846 8.848 8.85 8.852 8.854 8.856 8.858 8.86 8.862 8.864 8.866 8.868 8.87 8.872 8.874 8.876 8.878 8.88 8.882 8.884 8.886 8.888 8.89 8.892 8.894 8.896 8.898 8.9 8.902 8.904 8.906 8.908 8.91 8.912 8.914 8.916 8.918 8.92 8.922 8.924 8.926 8.928 8.93 8.932 8.934 8.936 8.938 8.94 8.942 8.944 8.946 8.948 8.95 8.952 8.954 8.956 8.958 8.96 8.962 8.964 8.966 8.968 8.97 8.972 8.974 8.976 8.978 8.98 8.982 8.984 8.986 8.988 8.99 8.992 8.994 8.996 8.998 9 9.002 9.004 9.006 9.008 9.01 9.012 9.014 9.016 9.018 9.02 9.022 9.024 9.026 9.028 9.03 9.032 9.034 9.036 9.038 9.04 9.042 9.044 9.046 9.048 9.05 9.052 9.054 9.056 9.058 9.06 9.062 9.064 9.066 9.068 9.07 9.072 9.074 9.076 9.078 9.08 9.082 9.084 9.086 9.088 9.09 9.092 9.094 9.096 9.098 9.1 9.102 9.104 9.106 9.108 9.11 9.112 9.114 9.116 9.118 9.12 9.122 9.124 9.126 9.128 9.13 9.132 9.134 9.136 9.138 9.14 9.142 9.144 9.146 9.148 9.15 9.152 9.154 9.156 9.158 9.16 9.162 9.164 9.166 9.168 9.17 9.172 9.174 9.176 9.178 9.18 9.182 9.184 9.186 9.188 9.19 9.192 9.194 9.196 9.198 9.2 9.202 9.204 9.206 9.208 9.21 9.212 9.214 9.216 9.218 9.22 9.222 9.224 9.226 9.228 9.23 9.232 9.234 9.236 9.238 9.24 9.242 9.244 9.246 9.248 9.25 9.252 9.254 9.256 9.258 9.26 9.262 9.264 9.266 9.268 9.27 9.272 9.274 9.276 9.278 9.28 9.282 9.284 9.286 9.288 9.29 9.292 9.294 9.296 9.298 9.3 9.302 9.304 9.306 9.308 9.31 9.312 9.314 9.316 9.318 9.32 9.322 9.324 9.326 9.328 9.33 9.332 9.334 9.336 9.338 9.34 9.342 9.344 9.346 9.348 9.35 9.352 9.354 9.356 9.358 9.36 9.362 9.364 9.366 9.368 9.37 9.372 9.374 9.376 9.378 9.38 9.382 9.384 9.386 9.388 9.39 9.392 9.394 9.396 9.398 9.4 9.402 9.404 9.406 9.408 9.41 9.412 9.414 9.416 9.418 9.42 9.422 9.424 9.426 9.428 9.43 9.432 9.434 9.436 9.438 9.44 9.442 9.444 9.446 9.448 9.45 9.452 9.454 9.456 9.458 9.46 9.462 9.464 9.466 9.468 9.47 9.472 9.474 9.476 9.478 9.48 9.482 9.484 9.486 9.488 9.49 9.492 9.494 9.496 9.498 9.5 9.502 9.504 9.506 9.508 9.51 9.512 9.514 9.516 9.518 9.52 9.522 9.524 9.526 9.528 9.53 9.532 9.534 9.536 9.538 9.54 9.542 9.544 9.546 9.548 9.55 9.552 9.554 9.556 9.558 9.56 9.562 9.564 9.566 9.568 9.57 9.572 9.574 9.576 9.578 9.58 9.582 9.584 9.586 9.588 9.59 9.592 9.594 9.596 9.598 9.6 9.602 9.604 9.606 9.608 9.61 9.612 9.614 9.616 9.618 9.62 9.622 9.624 9.626 9.628 9.63 9.632 9.634 9.636 9.638 9.64 9.642 9.644 9.646 9.648 9.65 9.652 9.654 9.656 9.658 9.66 9.662 9.664 9.666 9.668 9.67 9.672 9.674 9.676 9.678 9.68 9.682 9.684 9.686 9.688 9.69 9.692 9.694 9.696 9.698 9.7 9.702 9.704 9.706 9.708 9.71 9.712 9.714 9.716 9.718 9.72 9.722 9.724 9.726 9.728 9.73 9.732 9.734 9.736 9.738 9.74 9.742 9.744 9.746 9.748 9.75 9.752 9.754 9.756 9.758 9.76 9.762 9.764 9.766 9.768 9.77 9.772 9.774 9.776 9.778 9.78 9.782 9.784 9.786 9.788 9.79 9.792 9.794 9.796 9.798 9.8 9.802 9.804 9.806 9.808 9.81 9.812 9.814 9.816 9.818 9.82 9.822 9.824 9.826 9.828 9.83 9.832 9.834 9.836 9.838 9.84 9.842 9.844 9.846 9.848 9.85 9.852 9.854 9.856 9.858 9.86 9.862 9.864 9.866 9.868 9.87 9.872 9.874 9.876 9.878 9.88 9.882 9.884 9.886 9.888 9.89 9.892 9.894 9.896 9.898 9.9 9.902 9.904 9.906 9.908 9.91 9.912 9.914 9.916 9.918 9.92 9.922 9.924 9.926 9.928 9.93 9.932 9.934 9.936 9.938 9.94 9.942 9.944 9.946 9.948 9.95 9.952 9.954 9.956 9.958 9.96 9.962 9.964 9.966 9.968 9.97 9.972 9.974 9.976 9.978 9.98 9.982 9.984 9.986 9.988 9.99 9.992 9.994 9.996 9.998 10 10.002 10.004 10.006 10.008 10.01 10.012 10.014 10.016 10.018 10.02 10.022 10.024 10.026 10.028 10.03 10.032 10.034 10.036 10.038 10.04 10.042 10.044 10.046 10.048 10.05 10.052 10.054 10.056 10.058 10.06 10.062 10.064 10.066 10.068 10.07 10.072 10.074 10.076 10.078 10.08 10.082 10.084 10.086 10.088 10.09 10.092 10.094 10.096 10.098 10.1 10.102 10.104 10.106 10.108 10.11 10.112 10.114 10.116 10.118 10.12 10.122 10.124 10.126 10.128 10.13 10.132 10.134 10.136 10.138 10.14 10.142 10.144 10.146 10.148 10.15 10.152 10.154 10.156 10.158 10.16 10.162 10.164 10.166 10.168 10.17 10.172 10.174 10.176 10.178 10.18 10.182 10.184 10.186 10.188 10.19 10.192 10.194 10.196 10.198 10.2 10.202 10.204 10.206 10.208 10.21 10.212 10.214 10.216 10.218 10.22 10.222 10.224 10.226 10.228 10.23 10.232 10.234 10.236 10.238 10.24 10.242 10.244 10.246 10.248 10.25 10.252 10.254 10.256 10.258 10.26 10.262 10.264 10.266 10.268 10.27 10.272 10.274 10.276 10.278 10.28 10.282 10.284 10.286 10.288 10.29 10.292 10.294 10.296 10.298 10.3 10.302 10.304 10.306 10.308 10.31 10.312 10.314 10.316 10.318 10.32 10.322 10.324 10.326 10.328 10.33 10.332 10.334 10.336 10.338 10.34 10.342 10.344 10.346 10.348 10.35 10.352 10.354 10.356 10.358 10.36 10.362 10.364 10.366 10.368 10.37 10.372 10.374 10.376 10.378 10.38 10.382 10.384 10.386 10.388 10.39 10.392 10.394 10.396 10.398 10.4 10.402 10.404 10.406 10.408 10.41 10.412 10.414 10.416 10.418 10.42 10.422 10.424 10.426 10.428 10.43 10.432 10.434 10.436 10.438 10.44 10.442 10.444 10.446 10.448 10.45 10.452 10.454 10.456 10.458 10.46 10.462 10.464 10.466 10.468 10.47 10.472 10.474 10.476 10.478 10.48 10.482 10.484 10.486 10.488 10.49 10.492 10.494 10.496 10.498 10.5 10.502 10.504 10.506 10.508 10.51 10.512 10.514 10.516 10.518 10.52 10.522 10.524 10.526 10.528 10.53 10.532 10.534 10.536 10.538 10.54 10.542 10.544 10.546 10.548 10.55 10.552 10.554 10.556 10.558 10.56 10.562 10.564 10.566 10.568 10.57 10.572 10.574 10.576 10.578 10.58 10.582 10.584 10.586 10.588 10.59 10.592 10.594 10.596 10.598 10.6 10.602 10.604 10.606 10.608 10.61 10.612 10.614 10.616 10.618 10.62 10.622 10.624 10.626 10.628 10.63 10.632 10.634 10.636 10.638 10.64 10.642 10.644 10.646 10.648 10.65 10.652 10.654 10.656 10.658 10.66 10.662 10.664 10.666 10.668 10.67 10.672 10.674 10.676 10.678 10.68 10.682 10.684 10.686 10.688 10.69 10.692 10.694 10.696 10.698 10.7 10.702 10.704 10.706 10.708 10.71 10.712 10.714 10.716 10.718 10.72 10.722 10.724 10.726 10.728 10.73 10.732 10.734 10.736 10.738 10.74 10.742 10.744 10.746 10.748 10.75 10.752 10.754 10.756 10.758 10.76 10.762 10.764 10.766 10.768 10.77 10.772 10.774 10.776 10.778 10.78 10.782 10.784 10.786 10.788 10.79 10.792 10.794 10.796 10.798 10.8 10.802 10.804 10.806 10.808 10.81 10.812 10.814 10.816 10.818 10.82 10.822 10.824 10.826 10.828 10.83 10.832 10.834 10.836 10.838 10.84 10.842 10.844 10.846 10.848 10.85 10.852 10.854 10.856 10.858 10.86 10.862 10.864 10.866 10.868 10.87 10.872 10.874 10.876 10.878 10.88 10.882 10.884 10.886 10.888 10.89 10.892 10.894 10.896 10.898 10.9 10.902 10.904 10.906 10.908 10.91 10.912 10.914 10.916 10.918 10.92 10.922 10.924 10.926 10.928 10.93 10.932 10.934 10.936 10.938 10.94 10.942 10.944 10.946 10.948 10.95 10.952 10.954 10.956 10.958 10.96 10.962 10.964 10.966 10.968 10.97 10.972 10.974 10.976 10.978 10.98 10.982 10.984 10.986 10.988 10.99 10.992 10.994 10.996 10.998 11 11.002 11.004 11.006 11.008 11.01 11.012 11.014 11.016 11.018 11.02 11.022 11.024 11.026 11.028 11.03 11.032 11.034 11.036 11.038 11.04 11.042 11.044 11.046 11.048 11.05 11.052 11.054 11.056 11.058 11.06 11.062 11.064 11.066 11.068 11.07 11.072 11.074 11.076 11.078 11.08 11.082 11.084 11.086 11.088 11.09 11.092 11.094 11.096 11.098 11.1 11.102 11.104 11.106 11.108 11.11 11.112 11.114 11.116 11.118 11.12 11.122 11.124 11.126 11.128 11.13 11.132 11.134 11.136 11.138 11.14 11.142 11.144 11.146 11.148 11.15 11.152 11.154 11.156 11.158 11.16 11.162 11.164 11.166 11.168 11.17 11.172 11.174 11.176 11.178 11.18 11.182 11.184 11.186 11.188 11.19 11.192 11.194 11.196 11.198 11.2 11.202 11.204 11.206 11.208 11.21 11.212 11.214 11.216 11.218 11.22 11.222 11.224 11.226 11.228 11.23 11.232 11.234 11.236 11.238 11.24 11.242 11.244 11.246 11.248 11.25 11.252 11.254 11.256 11.258 11.26 11.262 11.264 11.266 11.268 11.27 11.272 11.274 11.276 11.278 11.28 11.282 11.284 11.286 11.288 11.29 11.292 11.294 11.296 11.298 11.3 11.302 11.304 11.306 11.308 11.31 11.312 11.314 11.316 11.318 11.32 11.322 11.324 11.326 11.328 11.33 11.332 11.334 11.336 11.338 11.34 11.342 11.344 11.346 11.348 11.35 11.352 11.354 11.356 11.358 11.36 11.362 11.364 11.366 11.368 11.37 11.372 11.374 11.376 11.378 11.38 11.382 11.384 11.386 11.388 11.39 11.392 11.394 11.396 11.398 11.4 11.402 11.404 11.406 11.408 11.41 11.412 11.414 11.416 11.418 11.42 11.422 11.424 11.426 11.428 11.43 11.432 11.434 11.436 11.438 11.44 11.442 11.444 11.446 11.448 11.45 11.452 11.454 11.456 11.458 11.46 11.462 11.464 11.466 11.468 11.47 11.472 11.474 11.476 11.478 11.48 11.482 11.484 11.486 11.488 11.49 11.492 11.494 11.496 11.498 11.5 11.502 11.504 11.506 11.508 11.51 11.512 11.514 11.516 11.518 11.52 11.522 11.524 11.526 11.528 11.53 11.532 11.534 11.536 11.538 11.54 11.542 11.544 11.546 11.548 11.55 11.552 11.554 11.556 11.558 11.56 11.562 11.564 11.566 11.568 11.57".split(" ");
//							double[] xSeries = new double[stringArray.length];
//							for (int i = 0; i < stringArray.length; i++) {
//								xSeries[i] = new Double(stringArray[i]).doubleValue();
//							}
//							lineSeries.setXSeries(xSeries);
//				        }
				        
				        lineSeries.setSymbolType(PlotSymbolType.NONE); // remove squares
				        //lineSeries.setVisibleInLegend(false); // FIXME bug in Linux
				        lineSeries.setLineWidth(2);
				        if (watch.getLineColor() != null) {
				        	lineSeries.setLineColor(watch.getLineColor());
				        }
					}
				}
				
				chart.getTitle().setText("");
				watchWindow.setText(title);

		        chart.getAxisSet().adjustRange();

				watchWindow.open();

				// should be closed on Close GN
			}
		}
	}
}
