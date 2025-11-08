package com.rajavivek.weather_app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.rajavivek.weather_app.entity.Weather;

@Service
public class WeatherService {

	private final String apiKey = "9196899b0286e91018a1b63bb0dd9da4";
	private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

	public JsonNode getWeather(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, JsonNode.class);
	}

	public String getURL(String city) {

		String url = baseUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";
		System.out.println("URL => " + url);
		return url;

	}

	public Weather getWeatherWithCity(String city) {

		String url = getURL(city);

		JsonNode weather = getWeather(url);

		String cityName = weather.get("name").asText();
		double temperature = weather.get("main").get("temp").asDouble();
		int humidity = weather.get("main").get("humidity").asInt();
		String description = weather.get("weather").get(0).get("description").asText();

		return new Weather(cityName, temperature, humidity, description);
	}
}
