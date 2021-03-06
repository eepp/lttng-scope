/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc. and others
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.lttng.scope.lttng.ust.ui.analysis.debuginfo.internal;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;
import org.eclipse.tracecompass.tmf.ui.symbols.ISymbolProvider;
import org.eclipse.tracecompass.tmf.ui.symbols.ISymbolProviderFactory;
import org.lttng.scope.lttng.ust.core.analysis.debuginfo.UstDebugInfoAnalysisModule;
import org.lttng.scope.lttng.ust.core.trace.LttngUstTrace;

/**
 * Factory to create {@link UstDebugInfoSymbolProvider}. Provided to TMF via
 * the extension point. Only works with LTTng-UST traces.
 *
 * @author Alexandre Montplaisir
 */
public class UstDebugInfoSymbolProviderFactory implements ISymbolProviderFactory {

    @Override
    public @Nullable ISymbolProvider createProvider(ITmfTrace trace) {
        /*
         * This applies only to UST traces that fulfill the DebugInfo analysis
         * requirements.
         */
        UstDebugInfoAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace,
                UstDebugInfoAnalysisModule.class, UstDebugInfoAnalysisModule.ID);

        if (module != null && trace instanceof LttngUstTrace) {
            return new UstDebugInfoSymbolProvider((LttngUstTrace) trace);
        }
        return null;
    }

}
