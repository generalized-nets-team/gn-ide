package net.generalised.genedit.baseapp.model;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author Dimitar Dimitrov
 */
public class BaseModelTest extends TestCase {

	@Test
	public void testGetGetter() {
		SampleClass sample = new SampleClass();
		
		final String sampleStringValue = "abcdef";
		final int sampleIntValue = 256;
		
		sample.setFoo(sampleStringValue);
		sample.setBar(sampleIntValue);
		
		assertEquals(sampleStringValue, sample.getFoo());
		assertEquals(sampleIntValue, sample.getBar());
		
		assertEquals(sampleStringValue, sample.get("foo", String.class));
		assertEquals(sampleIntValue, sample.get("bar", Integer.class).intValue());
		
	}
	
	@Test
	public void testGetSetter() {
		final String sampleStringValue = "abcdef";
		final int sampleIntValue = 256;
		
		SampleClass sample = new SampleClass();
		
		sample.setFoo("");
		sample.setBar(0);
		assertEquals("", sample.getFoo());
		assertEquals(0, sample.getBar());
		
		sample.set("foo", sampleStringValue);
		assertEquals(sampleStringValue, sample.getFoo());

		sample.set("bar", sampleIntValue);
		assertEquals(sampleIntValue, sample.getBar());
	}
	
	public static class SampleClass extends BaseModel {
		
		private String foo;

		private int bar;
		
		public void setFoo(String foo) {
			this.foo = foo;
		}

		public String getFoo() {
			return foo;
		}

		public void setBar(int bar) {
			this.bar = bar;
		}

		public int getBar() {
			return bar;
		} 
		
	}
}
