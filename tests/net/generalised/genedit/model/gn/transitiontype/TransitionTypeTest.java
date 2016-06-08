package net.generalised.genedit.model.gn.transitiontype;

import junit.framework.Assert;
import net.generalised.genedit.model.gn.GeneralizedNet;
import net.generalised.genedit.model.gn.Place;
import net.generalised.genedit.model.gn.Token;
import net.generalised.genedit.model.gn.Transition;

import org.junit.Test;

public class TransitionTypeTest {

	@Test
	public void testGetNextToken() {
		String expression = "and(l1,l2,or(l3,l4))";
		
		Assert.assertEquals("and", TransitionTypeParser.getNextToken(expression, 0));
		Assert.assertEquals("(", TransitionTypeParser.getNextToken(expression, 3));
		Assert.assertEquals("l1", TransitionTypeParser.getNextToken(expression, 4));
		Assert.assertEquals(",", TransitionTypeParser.getNextToken(expression, 6));
		Assert.assertEquals("l2", TransitionTypeParser.getNextToken(expression, 7));
		Assert.assertEquals("or", TransitionTypeParser.getNextToken(expression, 10));
		Assert.assertEquals(")", TransitionTypeParser.getNextToken(expression, expression.length() - 1));
		Assert.assertNull(TransitionTypeParser.getNextToken(expression, expression.length()));
		Assert.assertNull(TransitionTypeParser.getNextToken(expression, expression.length() + 1));
	}
	
	private void testExpression(String expression) {
		TransitionTypeNode tree = TransitionTypeParser.parse(expression);
		String cleanExpression = expression.replaceAll(" ","");
		String treeToString = tree.toString();
		Assert.assertEquals(cleanExpression, treeToString);
	}
	
//	@Test
//	public void testTreeToString() {
//		
//	}
	
	@Test
	public void testParseSimple() {
		Assert.assertEquals("true", TransitionTypeParser.parse("").toString());
		
		testExpression("true");
		testExpression("l1");
		testExpression("and(l1,l2)");
		testExpression(" and(l1, l2) ");
		testExpression("or(l1,l2,l3,l4)");
	}
	
	@Test
	public void testParseComplex() {
		testExpression("and(l1,or(l2,l3))");
		testExpression("and(or(l2,l3),l1)");
		testExpression("and(l1,or(l2,l3),and(l1,l4))");
		testExpression("and(or(l1,l2),or(l3,and(l4,l5)))");
		testExpression("and(or(l1,and(l2,l6)),or(l3,and(l4,l5)),l6)");
		testExpression("or(and( l1, and(l2,   l6,l7)) , or(l3,and(l4,l5,or(l1, l2),l9),l8),l6)");
	}
	
	private void testInvalidExpression(String expression) {
		Assert.assertNull(TransitionTypeParser.parse(expression));
	}
	
	@Test
	public void testInvalidExpressions() {
		testInvalidExpression("false");
		// TODO testInvalidExpression("l1 and l2"); should fail
		testInvalidExpression("(l1 and l2)");
		testInvalidExpression("(l1 && l2)");
	}
	
	private void testEvaluate(String expression, GeneralizedNet gn, boolean expectedResult) {
		TransitionTypeNode tree = TransitionTypeParser.parse(expression);
		Assert.assertEquals(expectedResult, tree.evaluate(gn));
	}
	
	@Test
	public void testEvaluate() {
		GeneralizedNet gn = new GeneralizedNet("test");
		
		Transition transition = new Transition("Z1");
		gn.getTransitions().add(transition);
		
		Place l1 = new Place("l1");
		gn.getPlaces().add(l1);
		transition.getInputs().add(l1);
		
		Place l2 = new Place("l2");
		gn.getPlaces().add(l2);
		transition.getInputs().add(l2);
		
		Place l3 = new Place("l3");
		gn.getPlaces().add(l3);
		transition.getInputs().add(l3);

		Token alpha1 = new Token("alpha1", l2);
		gn.getTokens().add(alpha1);
		
		Token alpha2 = new Token("alpha2", l3);
		gn.getTokens().add(alpha2);

		Token alpha3 = new Token("alpha3", l3);
		gn.getTokens().add(alpha3);

		testEvaluate("l2", gn, true);
		testEvaluate("l1", gn, false);
		testEvaluate("or(l1,l2)", gn, true);
		testEvaluate("and(l1,l2)", gn, false);
		testEvaluate("and(l2,l3)", gn, true);
		testEvaluate("or(and(l1,l2),and(l2,l3))", gn, true);
	}
}
