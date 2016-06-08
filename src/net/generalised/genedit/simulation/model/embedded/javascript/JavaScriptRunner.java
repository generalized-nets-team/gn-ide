package net.generalised.genedit.simulation.model.embedded.javascript;

import java.lang.reflect.InvocationTargetException;

import net.generalised.genedit.model.gn.FunctionReference;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.JavaScriptFunction;
import net.generalised.genedit.model.gn.JavaScriptFunctionFactory;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.simulation.model.embedded.FunctionRunner;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.BaseJSAdapter;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.JSCharacteristic;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.JSGeneralizedNet;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.JSPlace;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.JSToken;
import net.generalised.genedit.simulation.model.embedded.javascript.adapters.JSTransition;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JavaScriptRunner implements FunctionRunner {

	public static String GN_GLOBAL_VAR_NAME = "gn";
	
	private JavaScriptRunner() {}
	
	private static JavaScriptRunner instance;
	
	public static JavaScriptRunner getInstance() {
		if (instance == null) {
			instance = new JavaScriptRunner();
		}
		return instance;
	}
	
	public Object run(FunctionReference function, GeneralizedNet gn, Token token) {
		String functionCall = function.getFunctionName();
		if (token != null)
			functionCall += '(' + JavaScriptRunner.GN_GLOBAL_VAR_NAME
				+ ".tokens[\"" + token.getId() + "\"])";
		else
			functionCall += "()";
		return JavaScriptRunner.runJavaScript(gn, gn.getFunctions(), functionCall, false); // TODO is false OK in all cases?
	}

	public String getFunctionLanguage() {
		return JavaScriptFunctionFactory.LANGUAGE;
	}

	/**
	 * Executes JavaScript code.
	 * 
	 * @param gn
	 *            Can be null - only standard JS object will be available.
	 * @param script
	 *            The whole JavaScript source code
	 * @param expression
	 *            Function call or another expression to evaluate. Can be an
	 *            empty string.
	 * @param readOnly
	 * 			  Indicates whether the script can modify the GN.
	 * @return result as standard Java object (String, Boolean, etc.)
	 */
	public static Object runJavaScript(GeneralizedNet gn, String script, String expression, boolean readOnly) {
		
		// Creates and enters a Context. The Context stores information about
		// the execution environment of a script.
		final Context context = ContextFactory.getGlobal().enterContext();
		Object result = null;
		
		try {
			final Scriptable scope;
			if (gn != null) {
				scope = new ImporterTopLevel(context);
			} else {
				// Initializes the standard objects (Object, Function, etc.)
				scope = context.initStandardObjects();
			}

			if (gn != null) {
				//context.setClassShutter(DEFAULT_CLASS_SHUTTER);

				// Add here all classes exposed to scripts!
				defineClasses(scope, JSGeneralizedNet.class, JSTransition.class, JSPlace.class, 
						JSToken.class, JSCharacteristic.class);
	
				// TODO GNTicker uses PascalCase... define both variables?
				BaseJSAdapter<GeneralizedNet> gnGlobalVar = defineVariable(context, scope,
						JSGeneralizedNet.CLASS_NAME, GN_GLOBAL_VAR_NAME,
						JSGeneralizedNet.class, gn);
				gnGlobalVar.setReadOnly(readOnly);
				
				// TODO! define global function matlab :)

				//context.evaluateString(scope, INIT_JAVASCRIPT, "", 1, null);
			}
			
			result = context.evaluateString(scope, script + "\n" + expression, "", 1, null);
			
		} /*catch (EcmaError e) {
			// TODO
		} catch (EvaluatorException e) {
			// TODO
		}*/ finally {
			Context.exit();
		}
		
		return result;
	}
	
//	/**
//	 * Filter for classes that are visible to scripts.
//	 */
//	static final ClassShutter DEFAULT_CLASS_SHUTTER = new ClassShutter() {
//		public boolean visibleToScripts(String fullClassName) {
//			return fullClassName.startsWith("org.mozilla.javascript")
//			|| fullClassName.equals("java.lang.Object")
//			|| fullClassName.startsWith("org...util.position.Imm"); 
//		}
//	};
//
//	/**
//	 * Initialization JavaScript code before a script is launched.
//	 */
//	static final String INIT_JAVASCRIPT = "importPackage(org...util.position);";

	@SuppressWarnings("unchecked")
	private static void defineClasses(Scriptable scope, Class<?>... classes) {
		try {
			for (Class cl : classes) {
				ScriptableObject.defineClass(scope, cl);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends BaseJSAdapter<S>, S> BaseJSAdapter<S> defineVariable(Context context,
			Scriptable scope, String jsClassName, String varName, Class<T> cl, S adaptedObject) {
		
		Scriptable scriptableJsObject = context.newObject(scope, jsClassName);
		ScriptableObject.putProperty(scope, varName, scriptableJsObject); 
		BaseJSAdapter<S> jsAdapter = (BaseJSAdapter<S>) 
				Context.jsToJava(scriptableJsObject, BaseJSAdapter.class);
		assert jsAdapter.getClassName().equals(jsClassName);
		jsAdapter.setAdaptedObject(adaptedObject);
		return jsAdapter;
	}
	
	// TODO move to unit test
	public static void main(String[] args) {
		Object result = runJavaScript(null,"function f() { return true; } function g() {return -1;}", "f(1)", false);
		System.out.println(result.getClass().getName());
		
		GeneralizedNet gn = GeneralizedNet.generateMinimalValidGn();
		//gn.getTransitions().add(new Transition("tr"));
		gn.getPlaces().add(new Place("l1"));
		gn.setName("jee-en");
		result = runJavaScript(gn, 
				"function f() {if (gn.name == ['jee-en','aaa'].slice(0,1)) return gn.places['l1'].id;}", 
				"f()", false);
		System.out.println(Context.toString(result));
	}
}
