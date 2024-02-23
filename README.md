### This is a sample project for Ticket Master
#### It contains the following characteristics:
* Uses Hilt for dependecy injection
* Uses Room for DB
* Uses Jetpack Compose for UI
* Uses coroutines/flows

#### How to use
Just run the app. It will automatically fetch events for several cities (gets the result for empty city name).
The events available will be saved on the database and the UI will be notified and updated throug a StateFlow (on the ViewModel).
User can type the name of any city. In that case the app will fetch events for that city. There is a mechanism to fetch only after 800ms the user started typing, to avoid doing a network call on every keyboard stroke.

#### Improvements to be made
* Add more test cases
* Test UI
* Add Pagination
* Add a progress spinner when the app is open for the first time to show that it is loading content
* The app fetches the events and saves on the DB. A mechanism to clean up the DB can be added.