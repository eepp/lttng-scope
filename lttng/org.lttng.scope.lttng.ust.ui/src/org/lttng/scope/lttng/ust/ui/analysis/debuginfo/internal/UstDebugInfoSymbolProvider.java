/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc. and others
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.lttng.scope.lttng.ust.ui.analysis.debuginfo.internal;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.ui.symbols.DefaultSymbolProvider;
import org.eclipse.tracecompass.tmf.ui.symbols.ISymbolProviderPreferencePage;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.BinaryCallsite;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.FunctionLocation;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.UstDebugInfoAnalysisModule;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.UstDebugInfoBinaryAspect;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.UstDebugInfoFunctionAspect;
import org.lttng.scope.lttng.ust.core.trace.LttngUstTrace;

/**
 * Symbol provider for UST traces with debug information.
 *
 * @author Alexandre Montplaisir
 * @see UstDebugInfoAnalysisModule
 */
public class UstDebugInfoSymbolProvider extends DefaultSymbolProvider {

    /**
     * Create a new {@link UstDebugInfoSymbolProvider} for the given trace
     *
     * @param trace
     *            A non-null trace
     */
    public UstDebugInfoSymbolProvider(LttngUstTrace trace) {
        super(trace);
    }

    /**
     * Sets the configured path prefix. Usually called from the preferences
     * page.
     *
     * @param newPathPrefix
     *            The new path prefix to use
     */
    void setConfiguredPathPrefix(LttngUstTrace.SymbolProviderConfig newConfig) {
        getTrace().setSymbolProviderConfig(newConfig);
    }

    @Override
    public @NonNull LttngUstTrace getTrace() {
        /* Type enforced at constructor */
        return (LttngUstTrace) super.getTrace();
    }

    @Override
    public @Nullable String getSymbolText(int pid, long timestamp, long address) {
        BinaryCallsite bc = UstDebugInfoBinaryAspect.getBinaryCallsite(getTrace(), pid, timestamp, address);
        if (bc == null) {
            return null;
        }

        FunctionLocation loc = UstDebugInfoFunctionAspect.getFunctionFromBinaryLocation(bc);
        return (loc == null ? null : loc.getFunctionName());
    }

    @Override
    public @NonNull ISymbolProviderPreferencePage createPreferencePage() {
        return new UstDebugInfoSymbolProviderPreferencePage(this);
    }

}
