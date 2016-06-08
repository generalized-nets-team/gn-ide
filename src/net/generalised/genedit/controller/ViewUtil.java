package net.generalised.genedit.controller;

import java.util.List;

import net.generalised.genedit.baseapp.view.BaseView;
import net.generalised.genedit.model.GnDocument;
import net.generalised.genedit.model.errors.ErrorChecker;
import net.generalised.genedit.model.errors.Problem;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.view.Constants;
import net.generalised.genedit.view.GnView;
import net.generalised.genedit.view.MainForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dimitar Dimitrov
 *
 */
public class ViewUtil {

	private ViewUtil() {
	}
	
	public static GeneralizedNet getCurrentlyViewedGn(BaseView view) {
		MainForm mainForm = view.getParent(MainForm.class);
		GnDocument document = mainForm.getDocument();
		if (document != null) {
			//FIXME we have another hierarchy now... also what if a function is opened?
			Object data = mainForm.getCTabFolder().getSelection().getData();
			if (data instanceof GeneralizedNet)
				return (GeneralizedNet)data;
			else return document.getModel();//TODO: ako redaktirame funkciq na podmreja - 6te varne root mrejata!
		} else
			return null;
	}

	public static void showErrorMessageBox(final Shell shell, final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				box.setText(Constants.APPLICATION_NAME);
				box.setMessage(message);
				box.open();
			}
		});
	}
	
	public static void showErrorMessageBox(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				showErrorMessageBox(Display.getDefault().getActiveShell(), message);
			}
		});
	}
	
	public static void showExceptionMessageBox(final Throwable throwable) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				showExceptionMessageBox(Display.getDefault().getActiveShell(), throwable);
			}
		});
	}
	
	public static void showExceptionMessageBox(Shell shell, Throwable throwable) {
		String message = "An unhandled error has occured. Please see the log for more details.\n" 
				+ throwable.getClass().getName();
		if (throwable.getMessage() != null) {
			message += ": " + throwable.getMessage();
		}
		showErrorMessageBox(shell, message);
	}

	//TODO: is this a good place?
	public static boolean checkErrorsWithoutMessage(MainForm view) {
		GnDocument document = view.getDocument();
		assert document != null;
		GnView composite = view.getCurrentGnView();
		//FIXME: if function view - NPE
		composite.clearProblems();
		ErrorChecker checker = new ErrorChecker(document);
		List<Problem> problems = checker.checkAll();
		boolean hasErrors = false;
		for (Problem problem : problems) {
			composite.addProblem(problem);
			if (problem.isError())
				hasErrors = true;
		}
		return !hasErrors;
	}
	

}
