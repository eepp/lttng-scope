/*******************************************************************************
 * Copyright (c) 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Matthew Khouzam - Initial API and implementation
 *******************************************************************************/

package org.lttng.scope.lttng.kernel.core.analysis.os.handlers.internal;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;
import org.lttng.scope.lttng.kernel.core.analysis.os.Attributes;
import org.lttng.scope.lttng.kernel.core.trace.layout.ILttngKernelEventLayout;

import ca.polymtl.dorsal.libdelorean.ITmfStateSystemBuilder;
import ca.polymtl.dorsal.libdelorean.exceptions.AttributeNotFoundException;
import ca.polymtl.dorsal.libdelorean.statevalue.ITmfStateValue;
import ca.polymtl.dorsal.libdelorean.statevalue.TmfStateValue;

/**
 * Set Prio handler
 */
public class PiSetprioHandler extends KernelEventHandler {

    /**
     * Constructor
     * @param layout event layout
     */
    public PiSetprioHandler(ILttngKernelEventLayout layout) {
        super(layout);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        ITmfEventField content = event.getContent();
        Integer cpu = KernelEventHandlerUtils.getCpu(event);
        Integer tid = ((Long) content.getField(getLayout().fieldTid()).getValue()).intValue();
        Integer prio = ((Long) content.getField(getLayout().fieldNewPrio()).getValue()).intValue();

        String threadAttributeName = Attributes.buildThreadAttributeName(tid, cpu);
        if (threadAttributeName == null) {
            return;
        }

        Integer updateThreadNode = ss.getQuarkRelativeAndAdd(KernelEventHandlerUtils.getNodeThreads(ss), threadAttributeName);

        /* Set the current prio for the new process */
        int quark = ss.getQuarkRelativeAndAdd(updateThreadNode, Attributes.PRIO);
        ITmfStateValue value = TmfStateValue.newValueInt(prio);
        ss.modifyAttribute(KernelEventHandlerUtils.getTimestamp(event), value, quark);
    }
}
