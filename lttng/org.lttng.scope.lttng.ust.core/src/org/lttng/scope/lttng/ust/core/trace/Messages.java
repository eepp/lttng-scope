/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Khouzam - Initial API and implementation
 *******************************************************************************/

package org.lttng.scope.lttng.ust.core.trace;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle for lttng2.kernel.core.trace
 *
 * @author Matthew Khouzam
 * @noreference Messages class
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    public static String LttngUstTrace_DomainError;
    public static String LttngUstTrace_MalformedTrace;
    public static String LttngUstTrace_TraceReadError;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
