package com.goldin.plugins.timestamp

import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * Timestamp container
 */
class Timestamp
{
    String property
    String pattern
    String timezone = 'GMT'
    String locale   = Locale.US.toString()


    /**
     * Formats date provided
     *
     * @param d date to format
     * @return date formatted using {@link #pattern}, {@link #timezone}, and {@link #locale} of this instance
     */
    public String format( Date d )
    {
        DateFormat dateFormat = new SimpleDateFormat( pattern, new Locale( locale ))
        dateFormat.timeZone = TimeZone.getTimeZone( timezone )
        dateFormat.format( d )
    }
}
