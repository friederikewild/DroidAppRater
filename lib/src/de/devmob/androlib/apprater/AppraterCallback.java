/*
 * $Id$
 *
 * Copyright 2012 Friederike Wild, created 06.05.2012
 */
package de.devmob.androlib.apprater;

/**
 * Interface to be informed about the dialog input.
 * 
 * @author friederike
 * @version $Rev$ $Date$
 */
public interface AppraterCallback
{
    public void processNever();
    
    public void processRate();
    
    public void processRemindMe();
}
