/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.ctf.core.event;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.ctf.core.event.types.ICompositeDefinition;

/**
 * IEventDefinition, an interface for representing individual events.
 *
 * @author Matthew Khouzam
 * @since 2.0
 */
public interface IEventDefinition {

    /**
     * Value representing an unknown cpu number.
     */
    int UNKNOWN_CPU = -1;

    /**
     * Gets the declaration (the form) of the data
     *
     * @return the event declaration
     */
    IEventDeclaration getDeclaration();

    /**
     * Get the event header
     *
     * @return the event header
     */
    ICompositeDefinition getEventHeader();

    /**
     * Gets the fields of a definition
     *
     * @return the fields of a definition in struct form. Can be null.
     */
    ICompositeDefinition getFields();

    /**
     * Gets the context of this event without the context of the stream
     *
     * @return the context in struct form
     */
    ICompositeDefinition getEventContext();

    /**
     * Gets the context of this event within a stream
     *
     * @return the context in struct form
     */
    ICompositeDefinition getContext();

    /**
     * Gets the context of packet the event is in.
     *
     * @return the packet context
     */
    ICompositeDefinition getPacketContext();

    /**
     * gets the CPU the event was generated by. Slightly LTTng specific
     *
     * @return The CPU the event was generated by
     */
    int getCPU();

    /**
     * Get the timestamp, it is offsetted
     *
     * @return the timestamp
     */
    long getTimestamp();

    /**
     * Get the packet attributes.
     *
     * @return the packet attributes, such as "device" and "timestamp_begin"
     */
    @NonNull Map<@NonNull String, @NonNull Object> getPacketAttributes();

}