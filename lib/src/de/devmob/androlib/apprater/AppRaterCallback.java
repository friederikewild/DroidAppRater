/*
 * Copyright (C) 2012-2013 Friederike Wild <friederike.wild@devmob.de>
 * Created 06.05.2012
 * 
 * https://github.com/friederikewild/DroidAppRater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devmob.androlib.apprater;

/**
 * Interface to be informed about the app rater dialog input.
 * 
 * @author Friederike Wild
 */
public interface AppRaterCallback
{
    /**
     * Callback method when the user chose to not rate and be never asked again.
     */
    public void processNever();

    /**
     * Callback method when the user chose to proceed and rate now.
     */
    public void processRate();

    /**
     * Callback method when the user chose to not rate now but accepts to be asked later.
     */
    public void processRemindMe();
}
