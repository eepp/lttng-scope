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
import org.lttng.scope.lttng.kernel.core.trace.layout.ILttngKernelEventLayout;

import ca.polymtl.dorsal.libdelorean.ITmfStateSystemBuilder;
import ca.polymtl.dorsal.libdelorean.exceptions.AttributeNotFoundException;
import ca.polymtl.dorsal.libdelorean.statevalue.TmfStateValue;

/**
 * Irq Exit handler
 */
public class IrqExitHandler extends KernelEventHandler {

    /**
     * Constructor
     *
     * @param layout
     *            event layout
     */
    public IrqExitHandler(ILttngKernelEventLayout layout) {
        super(layout);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        Integer cpu = KernelEventHandlerUtils.getCpu(event);
        if (cpu == null) {
            return;
        }
        int currentThreadNode = KernelEventHandlerUtils.getCurrentThreadNode(cpu, ss);
        Integer irqId = ((Long) event.getContent().getField(getLayout().fieldIrq()).getValue()).intValue();
        /* Put this IRQ back to inactive in the resource tree */
        int quark = ss.getQuarkRelativeAndAdd(KernelEventHandlerUtils.getNodeIRQs(cpu, ss), irqId.toString());
        TmfStateValue value = TmfStateValue.nullValue();
        long timestamp = KernelEventHandlerUtils.getTimestamp(event);
        ss.modifyAttribute(timestamp, value, quark);

        /* Set the previous process back to running */
        KernelEventHandlerUtils.setProcessToRunning(timestamp, currentThreadNode, ss);

        /* Set the CPU status back to running or "idle" */
        KernelEventHandlerUtils.cpuExitInterrupt(timestamp, cpu, ss);
    }
}
