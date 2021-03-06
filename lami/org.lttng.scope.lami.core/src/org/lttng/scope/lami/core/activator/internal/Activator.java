/*
 * Copyright (C) 2017 EfficiOS Inc., Alexandre Montplaisir <alexmonthy@efficios.com>
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.lttng.scope.lami.core.activator.internal;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.tracecompass.tmf.core.analysis.ondemand.OnDemandAnalysisManager;
import org.lttng.scope.common.core.ScopeCoreActivator;
import org.lttng.scope.lami.core.LamiConfigUtils;
import org.lttng.scope.lami.core.module.LamiAnalysis;
import org.lttng.scope.lami.core.module.LamiAnalysisFactoryException;
import org.lttng.scope.lami.core.module.LamiAnalysisFactoryFromConfigFile;

/**
 * Plugin activator
 *
 * @noreference This class should not be accessed outside of this plugin.
 */
public class Activator extends ScopeCoreActivator {

    /**
     * Return the singleton instance of this activator.
     *
     * @return The singleton instance
     */
    public static Activator instance() {
        return ScopeCoreActivator.getInstance(Activator.class);
    }

    private void loadUserDefinedAnalyses() {
        final Path configDirPath = LamiConfigUtils.getConfigDirPath();

        try {
            final List<LamiAnalysis> analyses = LamiAnalysisFactoryFromConfigFile.buildFromConfigDir(configDirPath, true, trace -> true);

            OnDemandAnalysisManager manager = OnDemandAnalysisManager.getInstance();
            analyses.forEach(manager::registerAnalysis);

        } catch (LamiAnalysisFactoryException e) {
            logWarning("Cannot load user-defined external analyses", e); //$NON-NLS-1$
        }
    }

    @Override
    protected void startActions() {
        loadUserDefinedAnalyses();
    }

    @Override
    protected void stopActions() {
    }

}
