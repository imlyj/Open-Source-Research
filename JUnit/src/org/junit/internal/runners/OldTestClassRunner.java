package org.junit.internal.runners;

/**
 * For backward compatibility with those who have reached inside internal.runners in order to 
 * override JUnit's best guess about which runner to use.
 */
@Deprecated
public class OldTestClassRunner extends JUnit38ClassRunner {
	public OldTestClassRunner(Class<?> klass) {
		super(klass);
	}
}
