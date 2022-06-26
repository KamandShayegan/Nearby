
## Nearby Project

#### _Application flow:_

* After the application is launched, it gets the users permission to access location
* On access granted, location (meaning longitude and latitude) are given to TomToms nearby API
* Request is sent in order to fetch nearby points of interest
* If data is correctly fetched, it will be displayed on the screen
* On click, user will be transfered to another screen, showing little more information about that place
 
#### _Exceptions:_
* On access denied, users location can't be fetched, thus the application flow stops here with a toast notification, indicating that the access was not granted

* On access granted, if anything was wrong with the api request, an ERROR text will be shown on the screen after the progress indicator

#### _Tools, Libraries and Architecture:_

* MVVM architecture is applied, meaning Repository, ViewModel and View are fully separated
* Intent
* LiveData 
* JetPack Compose
* Retrofit along with Moshi
* [TomTom nearby search service](https://developer.tomtom.com/search-api/documentation/search-service/nearby-search)
