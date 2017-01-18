## Android Lockscreen App Demo
This project shows how to make a minimal lock screen app for Android. The lock screen covers the entire screen including both the status bar and navigation bar.

The screenshot below shows how the lock screen looks like when active (Double tap closes the lock screen and it will be active again when the next screen on)

<img src="/../screenshots/doc/screenshots/screen02.png?raw=true" alt="Application screenshots" width="356px"/>

### Application Screen
The screen below shows the only switch of this application which turns the lock screen on/off.

<img src="/../screenshots/doc/screenshots/screen01.png?raw=true" alt="Application screenshots" width="356px"/>

### How the Lockscreen Works
This app is composed of two Android components - a full screen activity and a service with overlay view. The activity makes the screen go full screen while the service creates a overlay view to intercept inputs.
