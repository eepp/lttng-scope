/*
 * Copyright (C) 2017 EfficiOS Inc., Alexandre Montplaisir <alexmonthy@efficios.com>
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.lttng.scope.rcp.ui.activator.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.Nullable;
import org.lttng.scope.common.ui.ScopeUIActivator;
import org.lttng.scope.rcp.ui.cli.internal.CliParser;
import org.lttng.scope.rcp.ui.cli.internal.TracingRCPCliException;

/**
 * Plugin activator
 *
 * @noreference This class should not be accessed outside of this plugin.
 */
public class Activator extends ScopeUIActivator {

    private static @Nullable CliParser fCli;

    /**
     * Return the singleton instance of this activator.
     *
     * @return The singleton instance
     */
    public static Activator instance() {
        return ScopeUIActivator.getInstance(Activator.class);
    }

    /**
     * Get the command-line parser.
     *
     * @return The CLI parser
     */
    public @Nullable CliParser getCli() {
        return fCli;
    }

    @Override
    protected void startActions() {
        String args[] = Platform.getCommandLineArgs();
        fCli = null;
        try {
            fCli = new CliParser(args);
        } catch (TracingRCPCliException e) {
            logError(e.getMessage());
        }
    }

    @Override
    protected void stopActions() {
        fCli = null;
    }

}
