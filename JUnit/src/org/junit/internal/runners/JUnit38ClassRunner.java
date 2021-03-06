package org.junit.internal.runners;

import junit.extensions.TestDecorator;
import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.JUnit4TestCaseFacade;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class JUnit38ClassRunner extends Runner implements Filterable, Sortable {
	private static my.Debug DEBUG=new my.Debug(my.Debug.JUnitCore);//我加上的

	private static final class OldTestClassAdaptingListener implements
			TestListener {
		private final RunNotifier fNotifier;

		private OldTestClassAdaptingListener(RunNotifier notifier) {
			fNotifier= notifier;
		}

		public void endTest(Test test) {
			fNotifier.fireTestFinished(asDescription(test));
		}

		public void startTest(Test test) {
			fNotifier.fireTestStarted(asDescription(test));
		}

		// Implement junit.framework.TestListener
		public void addError(Test test, Throwable t) {
			Failure failure= new Failure(asDescription(test), t);
			fNotifier.fireTestFailure(failure);
		}

		private Description asDescription(Test test) {
			if (test instanceof JUnit4TestCaseFacade) {
				JUnit4TestCaseFacade facade= (JUnit4TestCaseFacade) test;
				return facade.getDescription();
			}
			return Description.createTestDescription(test.getClass(), getName(test));
		}

		private String getName(Test test) {
			if (test instanceof TestCase)
				return ((TestCase) test).getName();
			else
				return test.toString();
		}

		public void addFailure(Test test, AssertionFailedError t) {
			addError(test, t);
		}
	}

	private Test fTest;
	
	@SuppressWarnings("unchecked")
	public JUnit38ClassRunner(Class<?> klass) {
		this(new TestSuite(klass.asSubclass(TestCase.class)));
	}

	public JUnit38ClassRunner(Test test) {
		super();

		DEBUG.P(this,"JUnit38ClassRunner(1)");
		DEBUG.P("test="+test);

		fTest= test;

		DEBUG.P(0,this,"JUnit38ClassRunner(1)");
	}

	@Override
	public void run(RunNotifier notifier) {
		try {//我加上的
		DEBUG.P(this,"run(1)");

		TestResult result= new TestResult();

		DEBUG.P("fTest.getClass()="+fTest.getClass());
		result.addListener(createAdaptingListener(notifier));
		fTest.run(result);

		}finally{//我加上的
		DEBUG.P(0,this,"run(1)");
		}
	}

	public static TestListener createAdaptingListener(final RunNotifier notifier) {
		return new OldTestClassAdaptingListener(notifier);
	}
	
	@Override
	public Description getDescription() {
		try {//我加上的
		DEBUG.P(this,"getDescription()");

		return makeDescription(fTest);

		}finally{//我加上的
		DEBUG.P(0,this,"getDescription()");
		}
	}

	private Description makeDescription(Test test) {
		try {//我加上的
		DEBUG.P(this,"makeDescription(1)");

		DEBUG.P("test="+test);
		DEBUG.P("(test instanceof TestCase)="+(test instanceof TestCase));
		DEBUG.P("(test instanceof TestSuite)="+(test instanceof TestSuite));
		DEBUG.P("(test instanceof JUnit4TestAdapter)="+(test instanceof JUnit4TestAdapter));
		DEBUG.P("(test instanceof TestDecorator)="+(test instanceof TestDecorator));

		if (test instanceof TestCase) {
			TestCase tc= (TestCase) test;
			return Description.createTestDescription(tc.getClass(), tc.getName());
		} else if (test instanceof TestSuite) {
			TestSuite ts= (TestSuite) test;
			String name= ts.getName() == null ? "" : ts.getName();
			Description description= Description.createSuiteDescription(name);
			int n= ts.testCount();
			for (int i= 0; i < n; i++)
				description.addChild(makeDescription(ts.testAt(i)));
			return description;
		} else if (test instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter= (JUnit4TestAdapter) test;
			return adapter.getDescription();
		} else if (test instanceof TestDecorator) {
			TestDecorator decorator= (TestDecorator) test;
			return makeDescription(decorator.getTest());
		} else {
			// This is the best we can do in this case
			return Description.createSuiteDescription(test.getClass());
		}

		}finally{//我加上的
		DEBUG.P(0,this,"makeDescription(1)");
		}
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		if (fTest instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter= (JUnit4TestAdapter) fTest;
			adapter.filter(filter);
		}
	}

	public void sort(Sorter sorter) {
		if (fTest instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter= (JUnit4TestAdapter) fTest;
			adapter.sort(sorter);
		}
	}
}
