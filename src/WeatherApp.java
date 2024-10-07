//retrieve Weather data from API, fetch the latest weather

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){
        //Get Location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        //Extract latitude and longitude data
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: No location data found");
            return null;
        }
        JSONObject location = (JSONObject) locationData.get(0);

        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //API request url with location coordinates
        String urlString ="https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
        "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=auto";

        try{
            //Call api
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200){
                System.out.println("Error : Could not connect to API");
                return null;
            }

            //Store resulting json data
            StringBuilder resultJSON = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJSON.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            //Parse through the data
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

            //Retrieve Hourly data
            JSONObject hourly = (JSONObject) resultsJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //Get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //Get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //Get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //Building the weather json data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName){

        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{

            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200){
                System.out.println("Error : Could not connect to API");
                return null;
            }else{
                //Store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                //Parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //Get the list of location Data
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches our current time
        for (int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){

            //Get current date and time
            LocalDateTime currentDateTime = LocalDateTime.now();

            //format date to be 2024-10-07T00:00
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

            String formattedDateTime = currentDateTime.format(formatter);

            return formattedDateTime;


    }

    //Convert weather code to weather events (clear sky, fog,...)
    private static  String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0){
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <=3L){
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                    || (weathercode >= 80L && weathercode <= 99L)){
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
