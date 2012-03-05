package de.beuth.sp.belegsystem;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.test.PageTester;

public abstract class TestUtil {
		
	private static PageTester pageTester;
	
	private static void config() {
		pageTester = new PageTester("de.beuth.sp.belegsystem.tapestry", "app", "src/main/webapp");
	}
	
	public static PageTester getPageTester() {
		if (pageTester==null) config();
		return pageTester;
	}	
	
	public static Registry getRegistry() {
		if (pageTester==null) config();
		return pageTester.getRegistry();
	}
		
	public static <T> T getService(final Class<T> clazz) {
		if (pageTester==null) config();
		return pageTester.getService(clazz);
	}
		

}
