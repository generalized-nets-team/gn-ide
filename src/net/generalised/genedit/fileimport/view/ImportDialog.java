package net.generalised.genedit.fileimport.view;

import net.generalised.genedit.fileimport.ImportFileSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ImportDialog extends Dialog {

	private Shell dialogShell;
	//static private Button buttonOK;

	private boolean clickedOk = false;
	private String vgnFileName;
	private String tknFileName;
	private String chrFileName;
	private String xmlFileName;
	private String texGFileName;
	private boolean importToCurrentFile;
	
	public ImportDialog(Shell parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}
	
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			ImportDialog inst = new ImportDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ImportFileSet open() {
		Shell parent = getParent();
		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		dialogShell.setLayout(layout);
		{
			dialogShell.setText("Import Models Created with Gennete");

			Listener browseListener = new Listener() {
				public void handleEvent(Event event) {
					//TODO: Open FileDialog
					//6antava ideq: buton.data = text, text.data = ".ext"
					//da, ama tex 6te se polzva 2 pati po razli4en na4in... vsa6tnost 6te e auto-detect
					//event.widget.getData() - tova e buton.data
				}
			};
			
			GridData textData = new GridData();
			textData.grabExcessHorizontalSpace = true;
			textData.minimumWidth = 200;

			Label vgnLabel = new Label(dialogShell, SWT.NONE);
			vgnLabel.setText("VGN file: ");
			vgnLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		
			final Text vgnFileName = new Text(dialogShell, SWT.BORDER);
			vgnFileName.setLayoutData(textData);
			
			final Button vgnFileButton = new Button(dialogShell, SWT.PUSH);
			vgnFileButton.setText("...");
			vgnFileButton.setData("VGN");
			vgnFileButton.addListener(SWT.Selection, browseListener);

			Label tknLabel = new Label(dialogShell, SWT.NONE);
			tknLabel.setText("TKN file (Tokens description):");
			tknLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			final Text tknFileName = new Text(dialogShell, SWT.BORDER);
			tknFileName.setLayoutData(textData);
			
			Button tknFileButton = new Button(dialogShell, SWT.PUSH);
			tknFileButton.setText("...");

			Label chrLabel = new Label(dialogShell, SWT.NONE);
			chrLabel.setText("CHR file (Characteristic functions): ");
			chrLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

			final Text chrFileName = new Text(dialogShell, SWT.BORDER);
			chrFileName.setLayoutData(textData);

			Button chrFileButton = new Button(dialogShell, SWT.PUSH);
			chrFileButton.setText("...");
			
			Label l;
			l = new Label(dialogShell, SWT.PUSH);
			l = new Label(dialogShell, SWT.PUSH);
			l = new Label(dialogShell, SWT.PUSH);

			Label xmlLabel = new Label(dialogShell, SWT.NONE);
			xmlLabel.setText("XML file (created during simulation): ");
			xmlLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		
			final Text xmlFileName = new Text(dialogShell, SWT.BORDER);
			xmlFileName.setLayoutData(textData);
			
			Button xmlFileButton = new Button(dialogShell, SWT.PUSH);
			xmlFileButton.setText("...");

			Label texGLabel = new Label(dialogShell, SWT.NONE);
			texGLabel.setText("TeX file (created via File->Export->Graphic Structure): ");
			texGLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		
			final Text texGFileName = new Text(dialogShell, SWT.BORDER);
			texGFileName.setLayoutData(textData);
			
			Button texGFileButton = new Button(dialogShell, SWT.PUSH);
			texGFileButton.setText("...");

//				l = new Label(dialogShell, SWT.PUSH);
//				l = new Label(dialogShell, SWT.PUSH);
//				l = new Label(dialogShell, SWT.PUSH);
			
			Label info = new Label(dialogShell, SWT.NONE);
			info.setText("You can provide one or more files of different formats, because none of these formats\n" +
					"alone describes full GN model.\n" +
					"To import full model description, please use one of the following combinations:\n" +
					"VGN + TKN + CHR\n" +
					"TeX (Graphical structure) + XML\n" +
					"VGN + XML\n" +
					"To import only graphical structure: select VGN or TeX(Graphical) file.");
			GridData infoData = new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1);
			infoData.verticalAlignment = SWT.CENTER;
			infoData.verticalIndent = 16;//?
			info.setLayoutData(infoData);
			
			final Button importToCurrentFile = new Button(dialogShell, SWT.CHECK);
			importToCurrentFile.setText("Import files to current GN model");
			
			Composite composite = new Composite(dialogShell, SWT.NONE);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 3, 1));
			
			GridData buttonData = new GridData();
			buttonData.grabExcessHorizontalSpace = true;
			buttonData.minimumWidth = 64;
			
			Listener okListener = new Listener() {
				public void handleEvent(Event event) {
					clickedOk = true;
					ImportDialog.this.vgnFileName = vgnFileName.getText();
					ImportDialog.this.tknFileName = tknFileName.getText();
					ImportDialog.this.chrFileName = chrFileName.getText();
					ImportDialog.this.xmlFileName = xmlFileName.getText();
					ImportDialog.this.texGFileName = texGFileName.getText();
					ImportDialog.this.importToCurrentFile = importToCurrentFile.getSelection();
					dialogShell.close();
				}
			};
			
			//TODO: napravi da se izvikva i pri Enter! mai trqbva da go zadade6 na vseki text?
			
			Button ok = new Button(composite, SWT.PUSH);
			ok.setText("OK");
			ok.addListener(SWT.Selection, okListener);
			ok.setLayoutData(buttonData);
			
			Button cancel = new Button(composite, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.setLayoutData(buttonData);
			cancel.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					dialogShell.close();
				}
			});
		}
		dialogShell.layout();
		dialogShell.pack();
		dialogShell.open();
		Display display = dialogShell.getDisplay();
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (clickedOk) {
			ImportFileSet result = new ImportFileSet();
			result.setVgnFileName(vgnFileName);
			result.setTknFileName(tknFileName);
			result.setChrFileName(chrFileName);
			result.setXmlFileName(xmlFileName);
			result.setTexGFileName(texGFileName);
			result.setImportToCurrentFile(importToCurrentFile);
			return result;
		} else
			return null;
	}
}
