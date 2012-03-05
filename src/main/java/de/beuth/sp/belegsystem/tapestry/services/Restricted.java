package de.beuth.sp.belegsystem.tapestry.services;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.beuth.sp.belegsystem.lg.Role;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Restricted {
	Class<? extends Role>[] allowedFor() default {};
	boolean onlyIfAssociatedToProgram() default false;
	boolean onlyForSuperAdmin() default false;
}
