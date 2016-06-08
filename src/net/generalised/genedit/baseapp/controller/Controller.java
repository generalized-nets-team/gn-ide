package net.generalised.genedit.baseapp.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Dimitar Dimitrov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface Controller {
//	Class<? extends Event> eventType();
//	Class<? extends BaseView> sourceClass() default BaseView.class;
	//actually the controller method will have these two parameters, so with reflection will be determined the type...
	//event the 2nd arg is not needed - the first is enough!
}
