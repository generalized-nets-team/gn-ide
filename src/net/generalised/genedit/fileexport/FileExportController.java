package net.generalised.genedit.fileexport;

import java.io.File;

import net.generalised.genedit.baseapp.controller.Controller;
import net.generalised.genedit.controller.ViewUtil;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.view.MainForm;
import net.generalised.genedit.view.MainMenu.ExportFormalDefToTexEvent;
import net.generalised.genedit.view.MainMenu.ExportGraphicalStructureToTexEvent;
import net.generalised.genedit.view.MainMenu.ExportToSvgEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileExportController {

	private static File openFileDialog(Shell shell, String title, String[] extensions) {
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setText(title);
		fileDialog.setFilterExtensions(extensions);
		//TODO: propose file name - escape(gn.name)+1st ext.
		String selectedFile = fileDialog.open();
		if (selectedFile != null) {
			//TODO: ask for replace confirmation!
			return new File(selectedFile);
		}
		return null;
	}
	
	private static void export(MainForm view, String title, String[] extensions, FileExporter exporter) {
		Shell shell = view.getUIComponent().getShell();
		
		File outputFile = openFileDialog(shell, title, extensions);

		if (outputFile != null) {
			try {
				GeneralizedNet gn = view.getDocument().getModel();
				exporter.export(gn, outputFile);
			} catch (Exception e) {
				e.printStackTrace();
				ViewUtil.showErrorMessageBox(shell, e.toString());
			}
		}

	}
	
	@Controller
	public void exportToSvg(ExportToSvgEvent event, MainForm view) {
		FileExporter exporter = new SvgFileExporter();
		export(view, "Export to SVG", new String[]{"*.svg", "*.*"}, exporter);
	}
	

	@Controller
	public void exportToTeX(ExportGraphicalStructureToTexEvent event, MainForm view) {
		FileExporter exporter = new TexGraphicalFileExporter();
		export(view, "Export Graphical Structure to TeX File", 
				new String[]{"*.tex", "*.*"}, exporter);
	}

	@Controller
	public void exportFormalDefinitionToTeX(ExportFormalDefToTexEvent event, MainForm view) {
		FileExporter exporter = new TexFormalDefinitionFileExporter();
		export(view, "Export Formal Definition to TeX File", 
				new String[]{"*.tex", "*.*"}, exporter);
	}
}
