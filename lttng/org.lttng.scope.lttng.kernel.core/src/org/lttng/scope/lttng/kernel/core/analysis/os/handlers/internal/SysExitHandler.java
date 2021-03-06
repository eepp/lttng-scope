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
import org.lttng.scope.lttng.kernel.core.analysis.os.Attributes;
import org.lttng.scope.lttng.kernel.core.analysis.os.StateValues;
import org.lttng.scope.lttng.kernel.core.trace.layout.ILttngKernelEventLayout;

import ca.polymtl.dorsal.libdelorean.ITmfStateSystemBuilder;
import ca.polymtl.dorsal.libdelorean.exceptions.AttributeNotFoundException;
import ca.polymtl.dorsal.libdelorean.statevalue.ITmfStateValue;
import ca.polymtl.dorsal.libdelorean.statevalue.TmfStateValue;

/**
 * System call exit handler
 */
public class SysExitHandler extends KernelEventHandler {

    /**
     * Constructor
     *
     * @param layout
     *            event layout
     */
    public SysExitHandler(ILttngKernelEventLayout layout) {
        super(layout);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        Integer cpu = KernelEventHandlerUtils.getCpu(event);
        if (cpu == null) {
            return;
        }
        /* Assign the new system call to the process */
        int currentThreadNode = KernelEventHandlerUtils.getCurrentThreadNode(cpu, ss);
        int quark = ss.getQuarkRelativeAndAdd(currentThreadNode, Attributes.SYSTEM_CALL);
        ITmfStateValue value = TmfStateValue.nullValue();
        long timestamp = KernelEventHandlerUtils.getTimestamp(event);
        ss.modifyAttribute(timestamp, value, quark);

        /* Put the process in system call mode */
        value = StateValues.PROCESS_STATUS_RUN_USERMODE_VALUE;
        ss.modifyAttribute(timestamp, value, currentThreadNode);

        /* Put the CPU in system call (kernel) mode */
        int currentCPUNode = KernelEventHandlerUtils.getCurrentCPUNode(cpu, ss);
        value = StateValues.CPU_STATUS_RUN_USERMODE_VALUE;
        ss.modifyAttribute(timestamp, value, currentCPUNode);
    }

}
