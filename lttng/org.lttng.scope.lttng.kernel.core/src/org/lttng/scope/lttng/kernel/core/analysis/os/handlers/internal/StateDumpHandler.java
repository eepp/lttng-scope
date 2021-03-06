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

import static java.util.Objects.requireNonNull;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;
import org.lttng.scope.lttng.kernel.core.analysis.os.Attributes;
import org.lttng.scope.lttng.kernel.core.analysis.os.LinuxValues;
import org.lttng.scope.lttng.kernel.core.analysis.os.StateValues;
import org.lttng.scope.lttng.kernel.core.trace.layout.ILttngKernelEventLayout;

import ca.polymtl.dorsal.libdelorean.ITmfStateSystemBuilder;
import ca.polymtl.dorsal.libdelorean.exceptions.AttributeNotFoundException;
import ca.polymtl.dorsal.libdelorean.exceptions.StateValueTypeException;
import ca.polymtl.dorsal.libdelorean.statevalue.ITmfStateValue;
import ca.polymtl.dorsal.libdelorean.statevalue.TmfStateValue;

/**
 * LTTng Specific state dump event handler
 */
public class StateDumpHandler extends KernelEventHandler {

    /**
     * Constructor
     *
     * @param layout
     *            event layout
     */
    public StateDumpHandler(ILttngKernelEventLayout layout) {
        super(layout);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        ITmfEventField content = event.getContent();
        Integer cpu = KernelEventHandlerUtils.getCpu(event);
        int tid = ((Long) content.getField("tid").getValue()).intValue(); //$NON-NLS-1$
        int pid = ((Long) content.getField("pid").getValue()).intValue(); //$NON-NLS-1$
        int ppid = ((Long) content.getField("ppid").getValue()).intValue(); //$NON-NLS-1$
        int status = ((Long) content.getField("status").getValue()).intValue(); //$NON-NLS-1$
        String name = requireNonNull((String) content.getField("name").getValue()); //$NON-NLS-1$
        /*
         * "mode" could be interesting too, but it doesn't seem to be populated
         * with anything relevant for now.
         */

        String threadAttributeName = Attributes.buildThreadAttributeName(tid, cpu);
        if (threadAttributeName == null) {
            return;
        }

        int curThreadNode = ss.getQuarkRelativeAndAdd(KernelEventHandlerUtils.getNodeThreads(ss), threadAttributeName);
        long timestamp = KernelEventHandlerUtils.getTimestamp(event);
        /* Set the process' name */
        setProcessName(ss, name, curThreadNode, timestamp);

        /* Set the process' PPID */
        setPpid(ss, tid, pid, ppid, curThreadNode, timestamp);

        /* Set the process' status */
        setStatus(ss, status, curThreadNode, timestamp);
    }

    private static void setStatus(ITmfStateSystemBuilder ss, int status, int curThreadNode, long timestamp)
            throws StateValueTypeException, AttributeNotFoundException {
        ITmfStateValue value;
        if (ss.queryOngoingState(curThreadNode).isNull()) {
            switch (status) {
            case LinuxValues.STATEDUMP_PROCESS_STATUS_WAIT_CPU:
                value = StateValues.PROCESS_STATUS_WAIT_FOR_CPU_VALUE;
                break;
            case LinuxValues.STATEDUMP_PROCESS_STATUS_WAIT:
                /*
                 * We have no information on what the process is waiting on
                 * (unlike a sched_switch for example), so we will use the
                 * WAIT_UNKNOWN state instead of the "normal" WAIT_BLOCKED
                 * state.
                 */
                value = StateValues.PROCESS_STATUS_WAIT_UNKNOWN_VALUE;
                break;
            default:
                value = StateValues.PROCESS_STATUS_UNKNOWN_VALUE;
            }
            ss.modifyAttribute(timestamp, value, curThreadNode);
        }
    }

    private static void setPpid(ITmfStateSystemBuilder ss, int tid, int pid, int ppid, int curThreadNode, long timestamp)
            throws StateValueTypeException, AttributeNotFoundException {
        ITmfStateValue value;
        int quark;
        quark = ss.getQuarkRelativeAndAdd(curThreadNode, Attributes.PPID);
        if (ss.queryOngoingState(quark).isNull()) {
            if (pid == tid) {
                /* We have a process. Use the 'PPID' field. */
                value = TmfStateValue.newValueInt(ppid);
            } else {
                /* We have a thread, use the 'PID' field for the parent. */
                value = TmfStateValue.newValueInt(pid);
            }
            ss.modifyAttribute(timestamp, value, quark);
        }
    }

    private static void setProcessName(ITmfStateSystemBuilder ss, String name, int curThreadNode, long timestamp)
            throws StateValueTypeException, AttributeNotFoundException {
        ITmfStateValue value;
        int quark = ss.getQuarkRelativeAndAdd(curThreadNode, Attributes.EXEC_NAME);
        if (ss.queryOngoingState(quark).isNull()) {
            /* If the value didn't exist previously, set it */
            value = TmfStateValue.newValueString(name);
            ss.modifyAttribute(timestamp, value, quark);
        }
    }
}
