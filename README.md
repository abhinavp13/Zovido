Zovido App ![LOGO] [99]
==========
Zovido is an Open Source android app allowing you to save some additional call details in .xls format to your phone's memory or to your linked spreadsheet(it uses SpreadSheet as a Database). (It was customized as per requirements, but can be modified or extended easily.)
 
Screenshots
===========
![Demo Screenshot 1][1] ![Demo Screenshot 2][2] ![Demo Screenshot 3][3]  ![Demo Screenshot 4][4] ![Demo Screenshot 5][5] ![Demo Screenshot 6][6] ![Demo Screenshot 7][7] 

Features
========
* It lists some recent(approx. 500) call logs, you can select any of your recent call logs and save additional details related to that call. 

* Additional details that can be saved for a particular call includes : adding the person's name, attendant's name, <del>customer type denoting whether the person was a new or existing customer</del>, <del>the product type which was related to in the call</del>, purpose of call, type of product related in call, type of sport related in call, ,  and even add some additional call remarks.

* All saved logs with their additional details can be exported in .xls format and can be saved to phone or can be appended to a linked spreadsheet entries. More details [here](https://zovido-firebaseapp.com)

* It is made to assist customer support people or people who attend a lot of calls, where such type of detail can help gather more data, which can be further fed into their data analysis systems to understand customer needs and trends. Highly customizable, one can easily add or change additional call details.

Android Components and Libraries used 
=====================================
* <del>play-services-drive</del>, gdata-client, gdata-spreadsheet, jackson client lib, google client lib, appcompat v7, recyclerview v7, cardview v7, design support library 23+
* [ACRA](https://github.com/ACRA/acra).
* [RippleEffect](https://github.com/traex/RippleEffect) created by [Robin Chutaux](http://blog.robinchutaux.com).
* [JExcel Api] (http://jexcelapi.sourceforge.net/)
* [Firebase api] (https://www.firebase.com/)

Instructions to use this app as a developer 
===========================================
* Authorize your app requests using OAuth 2.0 credentials in google developer console.
* Create a service crendential and download your .p12 file, and put it in assets folder.
* Add your Firebase dashboard link in gradle.properties file.
* Add your Spreadsheet file key and worksheet name for Zovido to use as database.([Explained Here](https://zovido.firebaseapp.com))

Developed By
------------
* Abhinav Puri - <pabhinav@iitrpr.ac.in>

License
-------

    Copyright 2016 Abhinav Puri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: ./demopics/1.png
[2]: ./demopics/2.png
[3]: ./demopics/3.png
[4]: ./demopics/4.png
[5]: ./demopics/5.png
[6]: ./demopics/6.png
[7]: ./demopics/7.png
[99]: ./demopics/logo_1.png