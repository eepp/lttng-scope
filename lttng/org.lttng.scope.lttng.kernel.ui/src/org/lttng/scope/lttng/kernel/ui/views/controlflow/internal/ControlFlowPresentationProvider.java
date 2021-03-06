/*******************************************************************************
 * Copyright (c) 2012, 2015 Ericsson, École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Patrick Tasse - Initial API and implementation
 *   Geneviève Bastien - Move code to provide base classes for time graph view
 *******************************************************************************/

package org.lttng.scope.lttng.kernel.ui.views.controlflow.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.StateItem;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.TimeGraphPresentationProvider;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.ITimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.NullTimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeEvent;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils;
import org.lttng.scope.lttng.kernel.core.analysis.os.Attributes;
import org.lttng.scope.lttng.kernel.core.analysis.os.KernelAnalysisModule;
import org.lttng.scope.lttng.kernel.core.analysis.os.StateValues;
import org.lttng.scope.lttng.kernel.core.trace.IKernelTrace;
import org.lttng.scope.lttng.kernel.core.trace.layout.ILttngKernelEventLayout;
import org.lttng.scope.lttng.kernel.ui.activator.internal.Activator;

import ca.polymtl.dorsal.libdelorean.ITmfStateSystem;
import ca.polymtl.dorsal.libdelorean.exceptions.AttributeNotFoundException;
import ca.polymtl.dorsal.libdelorean.exceptions.StateSystemDisposedException;
import ca.polymtl.dorsal.libdelorean.exceptions.StateValueTypeException;
import ca.polymtl.dorsal.libdelorean.exceptions.TimeRangeException;
import ca.polymtl.dorsal.libdelorean.interval.ITmfStateInterval;
import ca.polymtl.dorsal.libdelorean.statevalue.ITmfStateValue;

/**
 * Presentation provider for the control flow view
 */
public class ControlFlowPresentationProvider extends TimeGraphPresentationProvider {

    private enum State {
        UNKNOWN        (new RGB(100, 100, 100)),
        WAIT_UNKNOWN   (new RGB(200, 200, 200)),
        WAIT_BLOCKED   (new RGB(200, 200, 0)),
        WAIT_FOR_CPU   (new RGB(200, 100, 0)),
        USERMODE       (new RGB(0,   200, 0)),
        SYSCALL        (new RGB(0,     0, 200)),
        INTERRUPTED    (new RGB(200,   0, 100));

        public final RGB rgb;

        private State(RGB rgb) {
            this.rgb = rgb;
        }

    }

    /**
     * Average width of the characters used for state labels. Is computed in the
     * first call to postDrawEvent(). Is null before that.
     */
    private Integer fAverageCharacterWidth = null;

    /**
     * Default constructor
     */
    public ControlFlowPresentationProvider() {
        super(Messages.ControlFlowView_stateTypeName);
    }

    private static State[] getStateValues() {
        return State.values();
    }

    @Override
    public StateItem[] getStateTable() {
        State[] states = getStateValues();
        StateItem[] stateTable = new StateItem[states.length];
        for (int i = 0; i < stateTable.length; i++) {
            State state = states[i];
            stateTable[i] = new StateItem(state.rgb, state.toString());
        }
        return stateTable;
    }

    @Override
    public int getStateTableIndex(ITimeEvent event) {
        if (event instanceof TimeEvent && ((TimeEvent) event).hasValue()) {
            int status = ((TimeEvent) event).getValue();
            return getMatchingState(status).ordinal();
        }
        if (event instanceof NullTimeEvent) {
            return INVISIBLE;
        }
        return TRANSPARENT;
    }

    @Override
    public String getEventName(ITimeEvent event) {
        if (event instanceof TimeEvent) {
            TimeEvent ev = (TimeEvent) event;
            if (ev.hasValue()) {
                return getMatchingState(ev.getValue()).toString();
            }
        }
        return Messages.ControlFlowView_multipleStates;
    }

    private static State getMatchingState(int status) {
        switch (status) {
        case StateValues.PROCESS_STATUS_WAIT_UNKNOWN:
            return State.WAIT_UNKNOWN;
        case StateValues.PROCESS_STATUS_WAIT_BLOCKED:
            return State.WAIT_BLOCKED;
        case StateValues.PROCESS_STATUS_WAIT_FOR_CPU:
            return State.WAIT_FOR_CPU;
        case StateValues.PROCESS_STATUS_RUN_USERMODE:
            return State.USERMODE;
        case StateValues.PROCESS_STATUS_RUN_SYSCALL:
            return State.SYSCALL;
        case StateValues.PROCESS_STATUS_INTERRUPTED:
            return State.INTERRUPTED;
        default:
            return State.UNKNOWN;
        }
    }

    @Override
    public Map<String, String> getEventHoverToolTipInfo(ITimeEvent event) {
        Map<String, String> retMap = new LinkedHashMap<>();
        if (!(event instanceof TimeEvent) || !((TimeEvent) event).hasValue() ||
                !(event.getEntry() instanceof ControlFlowEntry)) {
            return retMap;
        }
        ControlFlowEntry entry = (ControlFlowEntry) event.getEntry();
        ITmfStateSystem ssq = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), KernelAnalysisModule.ID);
        if (ssq == null) {
            return retMap;
        }
        int tid = entry.getThreadId();

        try {
            // Find every CPU first, then get the current thread
            int cpusQuark = ssq.getQuarkAbsolute(Attributes.CPUS);
            List<Integer> cpuQuarks = ssq.getSubAttributes(cpusQuark, false);
            for (Integer cpuQuark : cpuQuarks) {
                int currentThreadQuark = ssq.getQuarkRelative(cpuQuark, Attributes.CURRENT_THREAD);
                ITmfStateInterval interval = ssq.querySingleState(event.getTime(), currentThreadQuark);
                if (!interval.getStateValue().isNull()) {
                    ITmfStateValue state = interval.getStateValue();
                    int currentThreadId = state.unboxInt();
                    if (tid == currentThreadId) {
                        retMap.put(Messages.ControlFlowView_attributeCpuName, ssq.getAttributeName(cpuQuark));
                        break;
                    }
                }
            }

        } catch (AttributeNotFoundException | TimeRangeException | StateValueTypeException e) {
            Activator.instance().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        } catch (StateSystemDisposedException e) {
            /* Ignored */
        }
        int status = ((TimeEvent) event).getValue();
        if (status == StateValues.PROCESS_STATUS_RUN_SYSCALL) {
            int syscallQuark;
            try {
                syscallQuark = ssq.getQuarkRelative(entry.getThreadQuark(), Attributes.SYSTEM_CALL);
            } catch (AttributeNotFoundException e) {
                return retMap;
            }
            try {
                ITmfStateInterval value = ssq.querySingleState(event.getTime(), syscallQuark);
                if (!value.getStateValue().isNull()) {
                    ITmfStateValue state = value.getStateValue();
                    retMap.put(Messages.ControlFlowView_attributeSyscallName, state.toString());
                }

            } catch (TimeRangeException e) {
                Activator.instance().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
            } catch (StateSystemDisposedException e) {
                /* Ignored */
            }
        }

        return retMap;
    }

    @Override
    public void postDrawEvent(ITimeEvent event, Rectangle bounds, GC gc) {
        if (fAverageCharacterWidth == null) {
            fAverageCharacterWidth = gc.getFontMetrics().getAverageCharWidth();
        }
        if (bounds.width <= fAverageCharacterWidth) {
            return;
        }
        if (!(event instanceof TimeEvent)) {
            return;
        }
        ControlFlowEntry entry = (ControlFlowEntry) event.getEntry();
        ITmfStateSystem ss = TmfStateSystemAnalysisModule.getStateSystem(entry.getTrace(), KernelAnalysisModule.ID);
        if (ss == null) {
            return;
        }
        int status = ((TimeEvent) event).getValue();

        if (status != StateValues.PROCESS_STATUS_RUN_SYSCALL) {
            return;
        }
        int syscallQuark = ss.getQuarkRelative(entry.getThreadQuark(), Attributes.SYSTEM_CALL);
        try {
            ITmfStateInterval value = ss.querySingleState(event.getTime(), syscallQuark);
            if (!value.getStateValue().isNull()) {
                ITmfStateValue state = value.getStateValue();
                gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));

                /*
                 * Remove the "sys_" or "syscall_entry_" or similar from what we
                 * draw in the rectangle. This depends on the trace's event layout.
                 */
                int beginIndex = 0;
                ITmfTrace trace = entry.getTrace();
                if (trace instanceof IKernelTrace) {
                    ILttngKernelEventLayout layout = ((IKernelTrace) trace).getKernelEventLayout();
                    beginIndex = layout.eventSyscallEntryPrefix().length();
                }

                Utils.drawText(gc, state.toString().substring(beginIndex), bounds.x, bounds.y, bounds.width, bounds.height, true, true);
            }
        } catch (TimeRangeException e) {
            Activator.instance().logError("Error in ControlFlowPresentationProvider", e); //$NON-NLS-1$
        } catch (AttributeNotFoundException | StateSystemDisposedException e) {
            /* Ignored */
        }
    }
}
