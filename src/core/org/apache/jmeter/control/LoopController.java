/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.jmeter.control;

import java.io.Serializable;

import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.IntegerProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.StringProperty;

// NOTUSED import org.apache.jorphan.logging.LoggingManager;
// NOTUSED import org.apache.log.Logger;

/**
 * @author Michael Stover
 * @author Thad Smith
 * @version $Revision$
 */
public class LoopController extends GenericController implements Serializable {
	// NOTUSED private static Logger log = LoggingManager.getLoggerForClass();

	private final static String LOOPS = "LoopController.loops";

	private final static String CONTINUE_FOREVER = "LoopController.continue_forever";

	private transient int loopCount = 0;

	public LoopController() {
		setContinueForever(true);
	}

	public void setLoops(int loops) {
		setProperty(new IntegerProperty(LOOPS, loops));
	}

	public void setLoops(String loopValue) {
		setProperty(new StringProperty(LOOPS, loopValue));
	}

	public int getLoops() {
		try {
			JMeterProperty prop = getProperty(LOOPS);
			return Integer.parseInt(prop.getStringValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getLoopString() {
		return getPropertyAsString(LOOPS);
	}

	/**
	 * Determines whether the loop will return any samples if it is rerun.
	 * 
	 * @param forever
	 *            true if the loop must be reset after ending a run
	 */
	public void setContinueForever(boolean forever) {
		setProperty(new BooleanProperty(CONTINUE_FOREVER, forever));
	}

	public boolean getContinueForever() {
		return getPropertyAsBoolean(CONTINUE_FOREVER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.control.Controller#isDone()
	 */
	public boolean isDone() {
		if (getLoops() != 0) {
			return super.isDone();
		} else {
			return true;
		}
	}

	private boolean endOfLoop() {
		return (getLoops() > -1) && loopCount >= getLoops();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.control.GenericController#nextIsNull()
	 */
	protected Sampler nextIsNull() throws NextIsNullException {
		reInitialize();
		if (endOfLoop()) {
			if (!getContinueForever()) {
				setDone(true);
			} else {
				resetLoopCount();
			}
			return null;
		} else {
			return next();
		}
	}

	protected void incrementLoopCount() {
		loopCount++;
	}

	protected void resetLoopCount() {
		loopCount = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.control.GenericController#getIterCount()
	 */
	protected int getIterCount() {
		return loopCount + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.control.GenericController#reInitialize()
	 */
	protected void reInitialize() {
		setFirst(true);
		resetCurrent();
		incrementLoopCount();
		recoverRunningVersion();
	}
}