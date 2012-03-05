package multex.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import multex.Exc;

/**
 * Diese Annotation dient dazu für eine Exception einen Standard-Fehler-Text zu
 * definieren. <br/>
 * Beim Einsatz von Exceptions die von {@link AnnotatedExc} erben wird diese
 * Annotation ausgewertet und der {@link #value()} als DefaultMessageTextPattern
 * der multex-{@link Exc}-Klasse ausgegeben. <br/>
 * 
 * Außerdem kann spezifiziert werden ob der Inhalt der Annotation exportiert werden
 * darf (z.B. in eine Properties-Datei mittels {@link ExcMessagesToProperties})
 * und ob dabei ein früherer Eintrag überschrieben werden darf.
 * 
 * 
 */

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcMessage {
	String value();

	boolean export() default true;

	boolean overwritePreviousEntryInExport() default true;
}
