/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc., Philippe Proulx
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.tmf.ui.project.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.graphics.Image;
import org.eclipse.tracecompass.tmf.core.analysis.ondemand.IOnDemandAnalysis;

/**
 * User-defined on-demand analysis element.
 *
 * @author Philippe Proulx
 */
public class TmfUserDefinedOnDemandAnalysisElement extends TmfOnDemandAnalysisElement {

    /**
     * Constructor
     *
     * @param analysisName
     *            Name of the element
     * @param resource
     *            Workspace resource
     * @param parent
     *            Parent element, should be the "on-demand analyses" one
     * @param analysis
     *            The actual analysis represented by this element
     */
    protected TmfUserDefinedOnDemandAnalysisElement(@NonNull String analysisName, @NonNull IResource resource, @NonNull TmfOnDemandAnalysesElement parent, @NonNull IOnDemandAnalysis analysis) {
        super(analysisName, resource, parent, analysis);
    }

    @Override
    public @NonNull Image getIcon() {
        return TmfProjectModelIcons.USER_DEFINED_ONDEMAND_ICON;
    }

}
