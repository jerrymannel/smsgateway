My SMS Gateway.

This application turns your Android phone into a simple SMS gateway. The application accepts SMS requests as simple HTTP GET calls and sends out the messages using the device's mobile connection.

Please be aware of the following,
[1] This app can only send messages. Incoming messages are not handled.
[2] If you happen to use this application, then the outbound messages might incur charges according to the SMS rates set by operator of your mobile connection.
[3] This application is not meant as a replacement for any existing SMS gateways, and should only be used for development and testing purposes.

So why did i write this app?
I needed an SMS gateway for one of my projects to send SMS. I didn't have a data card and was not patient enough to read through Kannel(http://www.kannel.org/). I thought it was easier to set up my phone for the purpose. The initial prototype of the app helped me complete the project i was working on. Once that was done, i decided to clean-up the prototype and publish it for general use.

What can this app do?
This app can only send messages based on HTTP GET calls.
This app does NOT handle incoming messages at this point.

How to use?
Open the app and start the server. The app displays the URL to which the request has to be made.
The HTTP GET URL is as shown below,

	http://<device ip>:18080/?<phone number>=<short message>

	e.g. http://192.168.2.3:18080/?phone=+919912345678&message=HelloWorld

The app MUST be running in the foreground to access the service. If the app closes or goes into the background, the server stops automatically.