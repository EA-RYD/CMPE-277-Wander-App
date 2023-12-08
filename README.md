# CMPE-277-Wander-App
Class Project for CMPE 277
## Project Overview
Wander is a free android application that introduces an innovative, feature rich, assistant for vacation trip planning. The application leverages AI technology and Large Language Models to act as an intelligent travel planning guide. Recognizing the time-consuming nature of conventional travel planning, Wander aims to streamline this process by collecting location specific information along with user-specific preferences. The application is then able to facilitate efficient itinerary generation, taking into account factors such as travel season, interests, and suitability for different ranges of families. With the integration of AI and informative travel data APIs, Wander promises to provide users with effortless yet personalized travel experiences.

## Installation Instructions
Unfortunately, this application is not available on the Google Play Store currently. A method of local installation would be to use a phone with an Android OS and connect it to Android Studio to deploy the application on the hardware. 

![image](https://github.com/EA-RYD/CMPE-277-Wander-App/assets/32498849/5df19b1b-2614-47f8-ab16-1736ae3230da)

Once a device is connected either via USB or Wifi, the device will have to be set to developer mode and then the Android Studio Application can just be run normally so that it deploys to the local device. Note: Wifi connection only works for Android 11 and above versions of Android.

To be able to run the application as intended, TripAdvisor and OpenAI API keys are required. These can be substituted into the variables named after them in the DetailsActivity and ChatGptRepository files respectively. 


## Usage Instructions
The application is designed to integrate both AI and traditional travel agent api to provide user
intelligent and accurate travel suggestions. A User can use text/voice input to specify a travel
location or a preference. A User can also provide one's current location by sharing the GPS
sensor's data through a simple click. Based on the user input, the WanderApp calls a
pre-trained chatGpt assistant API to get suggestions on places to visit. Once the app gets the
suggestions, WanderApp calls the Tripadvisor(TA) Location Search API to find the matching
location_id in TA's data for each place. Then the app calls the TA Photo Search API and
open-meteo API to get photos, reviews, weather and other detailed information for each place.
After verifying the information, a user can further apply more locations(for example: a city next to
the current city) or preference (Good for kids, for example) to update the suggestions from
chatGpt.
A user can choose places from the suggestion list to add to one's itinerary, delete places from
the itinerary. A user can save the itinerary to the phone's database. A user can load the saved
itinerary from the database any time. A user can also send the itinerary to one's email.
WanderApp sends a pdf file of the user's itinerary to the specified email address.

**Note: The current version of Wander is limited by the data from ChatGPT and the TripAdvisor API, resulting in higher quality experiences only within locations in the United States.**

- A User can use text/voice input to specify a travel location or a preference. 
<img width="390" alt="01" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/c43f0f97-26f2-4a7a-8c89-25dec1b57889">

- A User can also provide one's current location by sharing the GPS sensor's data through a simple click.
<img width="374" alt="03" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/3ca89294-12db-45a0-84ce-093d0517438b">

- Choose a travel date.
<img width="366" alt="14" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/15abc347-f7d1-4146-b7c8-34c3ff64aa4c">


- By click Send Request, an intelligent suggustion list will be created.
<img width="373" alt="04" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/54ea0b0c-594a-44ad-93b2-8602e05cbd6d">
<img width="374" alt="05" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/979e3482-3d75-4548-8cba-116fc1bcbf04">

- You can specify your preference in natural language to refine the suggestion list.
<img width="373" alt="13" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/26ba2d56-9935-4a6a-8bd9-4ae6c0641879">
<img width="374" alt="15" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/dd34af0e-6c27-4287-b7fa-88d62916fbbe">


- Click Details button to check more information of a place.
<img width="363" alt="06" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/7b37be46-85d5-429b-96be-e83de0430ba5">
<img width="374" alt="07" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/9111979d-6209-417a-a829-7b34a7c2f9b7">

- Select items from the suggestion list and add them to the itinerary.
<img width="382" alt="08" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/e2074f91-3ef3-4e2a-a667-d0657d026a0e">

- Click Save to Phone button to save the itinerary locally. You can load it back from the phone anytime.
<img width="373" alt="09" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/351537ab-3e3f-4ed0-987e-7d1a8ac38028">
<img width="390" alt="01 copy" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/8e62dd88-d9e2-4e8e-a266-bfec4daa4712">

- Send itinerary to your or your friends' email. Wander will send the itinerary as pdf to the mailbox.
 <img width="366" alt="10" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/356fa8dd-90ee-49f9-981d-3a1896d66c3a">
 <img width="608" alt="12" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/a60ccc2d-df92-434b-9852-785346e65a0c">

## Architecture
<img width="785" alt="Screenshot 2023-12-06 at 10 46 03 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/8ef60cf7-b758-425b-8752-5160f64b2f89">


## Contact Information
eric.arreola@sjsu.edu
qiong.wu@sjsu.edu




  
