
## Nearby Project

### About
This application allows users to discover nearby points of interest based on their location. Upon launch, it requests permission to access the user’s location. With access granted, it sends the coordinates to TomTom’s Nearby API to fetch relevant places. The results are displayed on the screen, and users can click on any point of interest to view additional details on a separate screen.
 
#### _Exceptions:_
* On access denied, users location can't be fetched, thus the application flow stops here with a toast notification, indicating that the access was not granted

* On access granted, if anything was wrong with the api request, an ERROR text will be shown on the screen after the progress indicator

### Tools, Libraries and Architecture:

* MVVM architecture is applied, meaning Repository, ViewModel and View are fully separated
* Intent
* LiveData 
* JetPack Compose
* Retrofit along with Moshi
* [TomTom nearby search service](https://developer.tomtom.com/search-api/documentation/search-service/nearby-search)
