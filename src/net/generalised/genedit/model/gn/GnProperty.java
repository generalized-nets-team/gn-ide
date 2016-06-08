package net.generalised.genedit.model.gn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that given property is part of the GN definition. Annotated methods must be
 * getters ("getXxx()", for boolean properties "isXxx()" is allowed too).
 * The corresponding property name is "xxx" 
 * 
 * 
 * @author Dimitar Dimitrov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD })
public @interface GnProperty {
	// nothing
}
