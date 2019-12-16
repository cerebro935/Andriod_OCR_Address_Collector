# Contributions

### Daniel Fonseca
I worked on the android application for the majority of the project, but I also slightly worked on the web application. 

At first, I helped Son and Marco set up the Camera functionality for the android app. Later on, we reimplemented
a different camera api that allowed a custom camera UI. This gave us more versatility for app and allowed us to add more features
on top of the camera.

I also implemented the OCR tools and the image processing tools. I originally added Tess-Two for OCR, but the results from the
OCR engine were not great. I ended up switching from Tess-Two to Firebases ML kit. This gave way better results. In addition,
I also added the OpenCV sdk to the project for image processing.

For the web application, I tried to add a graph that shows the amount of letters receieved monthly. I had to scrap the graph due to
issues with implementation.
### Angel Guzman
For this project, I implemented a couple things. To start off, I researched and set up a Mysql database server. I also setup an
Apache web server. In the database, I setup the tables as specified in our design document.

I setup PHP scripts that allowed insertions and data retrieval from the database. I used these scripts to connect the Android App to the
Apache web server. Scripts allowed data insertion from the Android application. In addition to this, I added an API that helped parse
infomation for the data tables.

I also setup an interactive map for the web application built in React. I used a GeoCode API that allowed to get coordinates from
addresses and then plotted these coordinates on the interactive map.
### Son Phan
I worked on the android application with Daniel and Marco. We setup an android API that allowed us to use the default camera app.
We ended up switching from that API to the custom camera API that allowed us to be more expressive with the android application.

In addition to this work, I helped with the React web application. I setup the weba pplication and made tables that mapped JSON data.
I also added a React component that helped sort the features of the tables.

### Marco Vazquez
I worked on the android application with Son and Daniel. I implemented the initial functionalityt for the photo capture button (listener
and handler). 
In addition to this, I created a popup UI that displays any extracted text from the OCR. It allowed a used to edit the extracted text.
This allowed a user to verify if the information that is displayed is correct.

I also collaborated with Son in creating search and filter components for the web application. However, we ended up scrapping this for 
MDB datatables.

# Reflection
Overall, we are happy with what we were able to accomplish, but we weren't not able to accomplish everything we wanted.
We needed more time and we needed to allocate more time to our web application. We also scrapped too much on the android app and that
killed a lot of time that could have been used elsewhere. More time in general would also have been appreciated, but that wouldn't
be possible since this is just a 10 week project.

What we could have done better is that we could have researched our topics better. This would allow us to better plan how we would proceed
with developing our project. It could have also let us spend more time on developing a better UI for the application too.
