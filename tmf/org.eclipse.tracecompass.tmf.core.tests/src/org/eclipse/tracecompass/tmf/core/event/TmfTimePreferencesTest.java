/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marc-Andre Laperle - Initial API and implementation
 *******************************************************************************/

package org.eclipse.tracecompass.tmf.core.event;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.TimeZone;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.tracecompass.tmf.core.activator.internal.Activator;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimePreferencesConstants;
import org.eclipse.tracecompass.tmf.core.timestamp.TmfTimePreferences;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Test suite for the TmfTimePreferences class.
 */
public class TmfTimePreferencesTest {

    private static final String TIME_PATTERN = "HH:mm:ss.SSS SSS SSS";
    private static final String INTERVAL_PATTERN = "TTT.SSS SSS SSS";

    /**
     * Test that the preferences get initialized to the default
     */
    @Test
    public void testInit() {
        assertEquals(DefaultScope.INSTANCE.getNode(Activator.instance().getPluginId()).get(ITmfTimePreferencesConstants.DATIME, null), ITmfTimePreferencesConstants.TIME_HOUR_FMT);
    }

    /**
     * Test that getTimePattern returns the appropriate time pattern (from the default)
     */
    @Test
    public void testGetTimePattern() {
        assertEquals(TIME_PATTERN, TmfTimePreferences.getTimePattern());
    }

    /**
     * Test that getIntervalPattern returns the appropriate interval pattern (from the default)
     */
    @Test
    public void testGetIntervalPattern() {
        assertEquals(INTERVAL_PATTERN, TmfTimePreferences.getIntervalPattern());
    }

    /**
     * Test that getTimeZone returns the appropriate time zone (from the default)
     */
    @Test
    public void testGetTimeZone() {
        assertEquals(TimeZone.getDefault(), TmfTimePreferences.getTimeZone());
    }

    /**
     *  Test that getPreferenceMap returns the appropriate map even after preferences get modified
     *  and make sure it doesn't affect the defaults
     */
    @Test
    public void testGetPreferenceMap() {
        Map<String, String> defaultPreferenceMap = TmfTimePreferences.getDefaultPreferenceMap();
        assertEquals(ITmfTimePreferencesConstants.TIME_HOUR_FMT, defaultPreferenceMap.get(ITmfTimePreferencesConstants.DATIME));

        // Modify the preferences
        String testValue = ITmfTimePreferencesConstants.TIME_HOUR_FMT + "foo";
        IEclipsePreferences node = InstanceScope.INSTANCE.getNode(Activator.instance().getPluginId());
        node.put(ITmfTimePreferencesConstants.DATIME, testValue);
        try {
            node.flush();
        } catch (BackingStoreException e) {
        }
        // Make sure the modification is in the map
        Map<String, String> preferenceMap = TmfTimePreferences.getPreferenceMap();
        assertEquals(testValue, preferenceMap.get(ITmfTimePreferencesConstants.DATIME));

        // Make sure the default is still the same
        defaultPreferenceMap = TmfTimePreferences.getDefaultPreferenceMap();
        assertEquals(ITmfTimePreferencesConstants.TIME_HOUR_FMT, defaultPreferenceMap.get(ITmfTimePreferencesConstants.DATIME));
    }

    /**
     * Test that computeTimePattern computes the appropriate time pattern from a preference map (from the default)
     */
    @Test
    public void testComputeTimePattern() {
        assertEquals(TIME_PATTERN, TmfTimePreferences.computeTimePattern(TmfTimePreferences.getPreferenceMap()));
    }

}
