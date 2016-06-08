package net.generalised.genedit.fileexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.TransitionMatrix;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.PlaceReference;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

public class TexFormalDefinitionFileExporter implements FileExporter {

	private static interface WriteFunction<T> {
		public String write(T object);
	}
	
	private static <T> void writeCommaSeparatedList(Writer writer,
			List<T> objects, WriteFunction<T> function) throws IOException {
		int count = objects.size();
		for (int i = 0; i < count; i++) {
			writer.append(function.write(objects.get(i)));
			if (i < count - 1) {
				writer.append(",");
			}
		}
	}
	
	public void export(GeneralizedNet gn, File outputFile) throws IOException {
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(outputFile));
			
			writer.append("\\documentclass[12pt]{article}\n\\usepackage[utf8]{inputenc}\n\\begin{document}\n\n\n");
			
			// TODO: subscripts

			writer.append("\\newcommand{\\sq}{\n\\unitlength 1.00mm\n\\linethickness{0.4pt}\n\\begin{picture}(4.0,4.0)\n" 
					+ "\\put(1.00,0.00){\\line(0,1){2.00}}\n\\put(1.00,2.00){\\line(1,0){2.00}}\n" 
					+ "\\put(3.00,2.00){\\line(0,-1){2.00}}\n\\put(3.00,0.00){\\line(-1,0){2.00}}\n\\end{picture}}\n\n");
			
			writer.append("$E = \\langle \\langle \\{");
			writeCommaSeparatedList(writer, gn.getTransitions(), new WriteFunction<Transition>() {
				public String write(Transition object) {
					return object.getName().toTexString();
				}
			});
			writer.append("\\}, *, *, *, *, *, * \\rangle,");
			
			writer.append("\\langle \\{");
			writeCommaSeparatedList(writer, gn.getTokens(), new WriteFunction<Token>() {
				public String write(Token object) {
					return object.getName().toTexString();
				}
			});
			writer.append("\\}, *, * \\rangle,");

			writer.append("\\langle \\{");
			writeCommaSeparatedList(writer, gn.getTokens(), new WriteFunction<Token>() {
				public String write(Token object) {
					return "x^{" + object.getName() + "}_0";
				}
			});
			writer.append("\\}, *, * \\rangle, \\Phi, * \\rangle$ where:\n\n");
			
			for (Transition transition : gn.getTransitions()) {
				String transitionNameInTex = transition.getName().toTexString();
				
				writer.append("$").append(transitionNameInTex).append(" = \\langle \\{");
				WriteFunction<PlaceReference> writeFunction = new WriteFunction<PlaceReference>() {
					public String write(PlaceReference object) {
						Place place = object.getPlace();
						return place.getName().toTexString();
					}
				}; 				
				writeCommaSeparatedList(writer, transition.getInputs(), writeFunction);
				writer.append("\\}, \\{");
				writeCommaSeparatedList(writer, transition.getOutputs(), writeFunction);
				writer.append("\\}, *, *, r_{").append(transitionNameInTex)
						.append("}, *, \\sq_{").append(transitionNameInTex).append("} \\rangle$\n\n");
				
				writer.append("$r_{").append(transitionNameInTex)
						.append("}$ is the following index matrix:")
						.append("\\[\nr_{").append(transitionNameInTex)
						.append("} = \\begin{array}{l|");
				int outputsCount = transition.getOutputs().size();
				for (int i = 0; i < outputsCount; i++) {
					writer.append("l");
				}
				writer.append("}\n");
				for (int i = 0; i < outputsCount; i++) {
					Place output = transition.getOutputs().get(i).getPlace();
					writer.append("& ").append(output.getName().toTexString());
				}
				writer.append(" \\\\\n\\hline\n");
				int inputsCount = transition.getInputs().size();
				TransitionMatrix<FunctionReference> predicatesMatrix = transition.getPredicates();
				for (int i = 0; i < inputsCount; i++) {
					Place input = transition.getInputs().get(i).getPlace();
					writer.append(input.getName().toTexString());
					for (int j = 0; j < outputsCount; j++) {
						Place output = transition.getOutputs().get(j).getPlace();
						FunctionReference functionRef = predicatesMatrix.getAt(input, output);
						String functionName = "false"; // XXX
						if (functionRef != null) {
							functionName = functionRef.getFunctionName();
						}
						writer.append(" & ").append(functionName);
					}
					writer.append(" \\\\\n");
				}
				writer.append("\\end{array}\\]\n\n");
				
				// TODO TransitionTypeNode ! Visitor for TeX format...
				writer.append("$\\sq_{").append(transitionNameInTex).append("} = ...$\n");
			}
			
			writer.append("\n\n\n\\end{document}");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
