DroidAppRater
=============

The DroidAppRater is an Android library project for an App Rater component that will help you getting better reviews on the Google Play Store. 

The idea is to prompt your users for a review when they are most likely to be happy using your app. A good time is for example when they challenge a new level in a game or when a couple of todo points got checked. Each app/game has unique rules and therefore to lib was build to enable you to easily configure it for each of your apps as needed and wait for a **defined number of happy events**.

As fallback or when there are no defined happy moments, the rating dialog will show up after a **defined amount of app starts**, too. To not trigger it to early and annoy a new user the **days since installation** are checked as well. Both triggers can be configured.

The DroidAppRater is inspired by the Appirater by Arash Payan [Appirater for iPhone](https://github.com/arashpayan/appirater). Find out more about his thoughts about including a rating dialog at [his blog](http://arashpayan.com/blog/2009/09/07/presenting-appirater).


Project overview
----------------
* [lib](https://github.com/friederikewild/DroidAppRater/tree/master/lib) - The library project that can be referenced from an Android project.
* [sample](https://github.com/friederikewild/DroidAppRater/tree/master/sample) - An Android demo app that demonstrates how to use and configure the DroidAppRater.


How to use
----------
* Easy steps enable developers to configure the DroidAppRater to match the best timing for prompting for a rating.
* A default configuration of a few days and app starts is provided to include the Rating Lib with minimal steps.


###Getting Started (Eclipse)
Import the DroidAppRater project to your workspace and add a reference to it from your project. All needed steps can be found at [developer.android: Referencing a library project](https://developer.android.com/tools/projects/projects-eclipse.html#ReferencingLibraryProject)


### Integration
Choose one or both of the following moments to have the DroidAppRater check if the rating dialog should be shown to the user at this point of usage:

* `onCreate` method of your main `Activity` class
```java
// Let the DroidAppRater check on each creation if the rating dialog should be shown:
AppraterUtils.checkToShowRatingOnStart(this);
```

* At any method (on the UI Thread) that is a 'happy' event
```java
// Let the DroidAppRater check on each positive event, if the rating dialog should be shown:
AppraterUtils.checkToShowRatingOnEvent(this);
```



### Configuration (Optional)

Configure the three possible parameters as needed by adding the following entries as sub-tags to `<application>` in the AndroidManifest.xml files.

All three parameters are needed to be fulfilled to show the rating dialog for the first time since the first time the app was used or since the last time the user postponed answering.

* Amount of app launches. Default value: 4
```xml
<meta-data android:name="de.devmob.launch_till_rate" android:value="40" />
```

* Amount of days. Default value: 4
```xml
<meta-data android:name="de.devmob.days_till_rate" android:value="7" />
```

* Amount of events. Default value: 2
```xml
<meta-data android:name="de.devmob.events_till_rate" android:value="6" />
```

* For debugging purposes activating logging of the DroidAppRater is possible
```xml
<meta-data android:name="de.devmob.verbose" android:value="true" />
```


### Integration Advanced

Sometimes getting the dialog is not enough for your needs. It may for example be of interest to you to log the users response to the rating dialog to included analytic tools.

To be able to do so, one can register a listener using the interface `AppraterCallback` with `AppraterUtils.checkToShowRatingOnStart` and `AppraterUtils.checkToShowRatingOnEvent`. This is demonstrated in the demo application.


Developed By
============

* The initial version was written by  <a href="https://plus.google.com/117518039262793648233?rel=author">Friederike Wild</a> - <friederike.wild@devmob.de>


License
=======

    Copyright 2012 Friederike Wild

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
