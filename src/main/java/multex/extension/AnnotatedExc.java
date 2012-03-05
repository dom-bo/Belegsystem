package multex.extension;

import multex.Exc;


public abstract class AnnotatedExc extends Exc {

	private static final long serialVersionUID = -5893440945542943557L;
	
	@Override
	public String getDefaultMessageTextPattern() {		
		if (this.getClass().isAnnotationPresent(ExcMessage.class)) {	
			return this.getClass().getAnnotation(ExcMessage.class).value();
		} else {
			return super.getDefaultMessageTextPattern();
		}
	}	
	
}
