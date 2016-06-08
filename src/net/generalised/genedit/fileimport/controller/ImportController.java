package net.generalised.genedit.fileimport.controller;

import java.io.IOException;

import net.generalised.genedit.baseapp.controller.Controller;
import net.generalised.genedit.baseapp.controller.Event;
import net.generalised.genedit.controller.ViewUtil;
import net.generalised.genedit.fileimport.GnTexImporter;
import net.generalised.genedit.fileimport.GnVgnImporter;
import net.generalised.genedit.fileimport.ImportFileSet;
import net.generalised.genedit.fileimport.view.ImportDialog;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.MainForm;
import net.generalised.genedit.view.MainMenu.CloseFileEvent;
import net.generalised.genedit.view.MainMenu.ImportGenneteFileEvent;

import org.eclipse.swt.SWT;

/**
 * @author Dimitar Dimitrov
 *
 */
public class ImportController {

	public class LoadGnEvent extends Event {
		private GeneralizedNet gn;
		
		public LoadGnEvent(GeneralizedNet gn) {
			this.gn = gn;
		}
		
		public GeneralizedNet getGn() {
			return this.gn;
		}
	}
	
	@Controller
	public void importFile(ImportGenneteFileEvent event, MainForm view) {
		GnDocument document = view.getDocument(); 
		if (document != null) {
			ImportDialog dialog = new ImportDialog(view.getUIComponent().getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
			dialog.setText(Constants.APPLICATION_NAME);
			ImportFileSet files = dialog.open();
			if (files == null)
				return;
			GeneralizedNet gn = null;
			if (! files.isImportToCurrentFile()) {
				view.dispatchEvent(CloseFileEvent.class); //TODO: is this call correct...
				gn = new GeneralizedNet("Untitled");
			} else {
				//TODO: must be undoable!
				gn = document.getModel();
			}
			try {
				if (files.getVgnFileName() != null && files.getVgnFileName().length() > 0) {
					GnVgnImporter importer = new GnVgnImporter();
					importer.importFileToGn(files.getVgnFileName(), gn);
				}
				//TODO: ...
				if (files.getTexGFileName() != null && files.getTexGFileName().length() > 0) {
					GnTexImporter importer = new GnTexImporter();
					importer.importFileToGn(files.getTexGFileName(), gn);
				}
				if (! files.isImportToCurrentFile()) {
					view.dispatchEvent(new LoadGnEvent(gn));
					//MainController.openNewGn(view, gn);//TODO: is this call correct...
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
				//TODO: message...
				ViewUtil.showErrorMessageBox("Invalid file format.");
			}
		}
	}

}
