/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc. and others
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.lttng.scope.lttng.ust.ui.analysis.debuginfo.internal;

import static java.util.Objects.requireNonNull;
import static org.lttng.scope.common.core.NonNullUtils.nullToEmptyString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.tracecompass.tmf.ui.symbols.AbstractSymbolProviderPreferencePage;
import org.lttng.scope.lttng.ust.core.trace.LttngUstTrace;
import org.lttng.scope.lttng.ust.core.trace.LttngUstTrace.SymbolProviderConfig;

/**
 * Preference page to configure a path prefix from which to resolve all the
 * paths found the binary file's debug information.
 *
 * @author Alexandre Montplaisir
 */
public class UstDebugInfoSymbolProviderPreferencePage extends AbstractSymbolProviderPreferencePage {

    private @Nullable Button fUseCustomDirectoryCheckbox;
    private @Nullable Text fCustomDirectoryPath;
    private @Nullable Button fBrowseButton;
    private @Nullable Button fClearButton;

    /**
     * Creates a new object for the given provider
     *
     * @param provider
     *            a non-null provider
     */
    public UstDebugInfoSymbolProviderPreferencePage(UstDebugInfoSymbolProvider provider) {
        super(provider);
        setDescription(MessageFormat.format(Messages.PreferencePage_WindowDescription, provider.getTrace().getName()));
        setValid(true);
    }

    @Override
    public UstDebugInfoSymbolProvider getSymbolProvider() {
        /* Type enforced at constructor */
        return (UstDebugInfoSymbolProvider) super.getSymbolProvider();
    }

    @Override
    protected Control createContents(@Nullable Composite parent) {
        /*
         * We will use a grid layout with 3 columns :
         *
         * [checkbox+label][][]
         *
         * [text field][Browse][Clear]
         */
        Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new GridLayout(3, false));

        /* The first row: checkbox and itslabel */
        Button checkBox = new Button(composite, SWT.CHECK);
        checkBox.setText(Messages.PreferencePage_CheckboxLabel);
        checkBox.setToolTipText(Messages.PreferencePage_CheckboxTooltip);
        checkBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable SelectionEvent e) {
                updateContents();
            }
        });
        fUseCustomDirectoryCheckbox = checkBox;

        /* Nothing in the two following cells */
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);

        /* Second row: The text input box */
        Text text =  new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.setEditable(false);
        text.setToolTipText(Messages.PreferencePage_CheckboxTooltip);
        fCustomDirectoryPath = text;

        /* The "Browse..." button */
        Button browseButton = new Button(composite, SWT.NONE);
        browseButton.setText(Messages.PreferencePage_ButtonBrowse);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable SelectionEvent e) {
                browseDirectory(requireNonNull(fCustomDirectoryPath), Messages.PreferencePage_BrowseDialogTitle);
            }
        });
        fBrowseButton = browseButton;

        /* The "Clear" button */
        Button clearButton = new Button(composite, SWT.NONE);
        clearButton.setText(Messages.PreferencePage_ButtonClear);
        clearButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable SelectionEvent e) {
                requireNonNull(fCustomDirectoryPath).setText(""); //$NON-NLS-1$
                updateContents();
            }
        });
        fClearButton = clearButton;

        /* Load existing preferences */
        loadCurrentSettings();

        return composite;
    }

    private void browseDirectory(Text textField, @Nullable String dialogTitle) {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText(dialogTitle);
        String dirPath = dialog.open();
        if (dirPath != null) {
            textField.setText(dirPath);
            updateContents();
        }
    }

    private void loadCurrentSettings() {
        /* The settings are currently stored in the trace object */
        LttngUstTrace trace = getSymbolProvider().getTrace();
        SymbolProviderConfig config = trace.getSymbolProviderConfig();

        requireNonNull(fUseCustomDirectoryCheckbox).setSelection(config.useCustomRootDir());
        requireNonNull(fCustomDirectoryPath).setText(config.getCustomRootDirPath());

        updateContents();
    }

    @Override
    public void saveConfiguration() {
        SymbolProviderConfig config =
                new SymbolProviderConfig(getCurrentCheckBoxState(), getCurrentPathPrefix());

        LttngUstTrace trace = getSymbolProvider().getTrace();
        trace.setSymbolProviderConfig(config);
    }

    private boolean getCurrentCheckBoxState() {
        return (requireNonNull(fUseCustomDirectoryCheckbox).getSelection());
    }

    private String getCurrentPathPrefix() {
        return nullToEmptyString(requireNonNull(fCustomDirectoryPath).getText());
    }

    /**
     * Grey out the relevant controls if the checkbox is unchecked, and vice
     * versa. Also verify if the current settings are valid, and update the
     * window error message if needed.
     */
    private void updateContents() {
        boolean useCustomDirEnabled = getCurrentCheckBoxState();
        requireNonNull(fCustomDirectoryPath).setEnabled(useCustomDirEnabled);
        requireNonNull(fBrowseButton).setEnabled(useCustomDirEnabled);
        requireNonNull(fClearButton).setEnabled(useCustomDirEnabled);

        String errorMessage = null;

        if (useCustomDirEnabled) {
            String pathPrefix = getCurrentPathPrefix();
            Path path = Paths.get(pathPrefix);
            if (pathPrefix.equals("") || !Files.isDirectory(path)) { //$NON-NLS-1$
                errorMessage = Messages.PreferencePage_ErrorDirectoryDoesNotExists;
            }
        }
        setErrorMessage(errorMessage);
        setValid(errorMessage == null);
    }

}
