Class Project for CMPE 277
## Project Overview
<img width="366" alt="Screenshot 2023-12-08 at 11 42 32 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/ea030612-1fe9-4bc3-9130-39b81217429a">

Wander is a free android application that introduces an innovative, feature rich, assistant for vacation trip planning. The application leverages AI technology and Large Language Models to act as an intelligent travel planning guide. Recognizing the time-consuming nature of conventional travel planning, Wander aims to streamline this process by collecting location specific information along with user-specific preferences. The application is then able to facilitate efficient itinerary generation, taking into account factors such as travel season, interests, and suitability for different ranges of families. With the integration of AI and informative travel data APIs, Wander promises to provide users with effortless yet personalized travel experiences.

## Installation Instructions
Unfortunately, this application is not available on the Google Play Store currently. A method of local installation would be to use a phone with an Android OS and connect it to Android Studio to deploy the application on the hardware. 

![image](https://github.com/EA-RYD/CMPE-277-Wander-App/assets/32498849/5df19b1b-2614-47f8-ab16-1736ae3230da)

Once a device is connected either via USB or Wifi, the device will have to be set to developer mode and then the Android Studio Application can just be run normally so that it deploys to the local device. Note: Wifi connection only works for Android 11 and above versions of Android.

To be able to run the application as intended, TripAdvisor and OpenAI API keys are required. These can be substituted into the variables named after them in the DetailsActivity and ChatGptRepository files respectively. 


## Usage Instruction

**Note: The current version of Wander is limited by the data from ChatGPT and the TripAdvisor API, resulting in higher quality experiences only within locations in the United States.**

- A User can use text/voice input to specify a travel location or a preference. 
<img width="390" alt="01" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/c43f0f97-26f2-4a7a-8c89-25dec1b57889">

- A User can also provide one's current location by sharing the GPS sensor's data through a simple click.
<img width="374" alt="03" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/3ca89294-12db-45a0-84ce-093d0517438b">

- Choose a travel date.
<img width="366" alt="14" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/15abc347-f7d1-4146-b7c8-34c3ff64aa4c">


- By click Send Request, an intelligent suggustion list will be created.
<img width="366" alt="Screenshot 2023-12-08 at 11 42 32 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/0cf4adcf-0302-4c83-83b4-61ffdefd431e">
<img width="371" alt="Screenshot 2023-12-08 at 11 42 51 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/12bc27e5-48a2-4e45-ad94-09822e6f54fc">


- You can specify your preference in natural language to refine the suggestion list.
<img width="373" alt="13" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/26ba2d56-9935-4a6a-8bd9-4ae6c0641879">
<img width="374" alt="15" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/dd34af0e-6c27-4287-b7fa-88d62916fbbe">


- Click Details button to check more information of a place.
<img width="381" alt="Screenshot 2023-12-08 at 10 59 53 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/20e7ce5b-59fb-438f-8205-74a4a98cd006">
<img width="384" alt="Screenshot 2023-12-08 at 11 07 18 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/306cb3e3-7bba-4fc7-94c3-29498dd55e76">


- Select items from the suggestion list and add them to the itinerary.
<img width="370" alt="Screenshot 2023-12-08 at 11 00 24 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/9754719e-6d2c-4068-aede-70fab7c3b211">


- Click Save to Phone button to save the itinerary locally. You can load it back from the phone anytime.
<img width="390" alt="01 copy" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/8e62dd88-d9e2-4e8e-a266-bfec4daa4712">

- Send itinerary to your or your friends' email. Wander will send the itinerary as pdf to the mailbox.
 <img width="366" alt="10" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/356fa8dd-90ee-49f9-981d-3a1896d66c3a">
<img width="874" alt="Screenshot 2023-12-08 at 11 14 40 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/bbfd6409-7fd2-4adf-a0d5-076157a46787">


## Architecture
<img width="785" alt="Screenshot 2023-12-06 at 10 46 03 AM" src="https://github.com/EA-RYD/CMPE-277-Wander-App/assets/70813818/8ef60cf7-b758-425b-8752-5160f64b2f89">


## Contact Information
eric.arreola@sjsu.edu
qiong.wu@sjsu.edu




  
