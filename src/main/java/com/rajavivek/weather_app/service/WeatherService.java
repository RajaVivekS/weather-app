package com.rajavivek.weather_app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajavivek.weather_app.entity.Weather;

@Service
public class WeatherService {

	private final String apiKey = "9196899b0286e91018a1b63bb0dd9da4";
	private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

	private final String forecastURL = "https://api.openweathermap.org/data/2.5/forecast";
	
	private final ObjectMapper mapper = new ObjectMapper();

	
	public JsonNode getWeather(String url) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, JsonNode.class);
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

	public Map<String, Object> getForecast(String city) throws Exception {
		String url = getForecaseURL(city);
		RestTemplate restTemplate = new RestTemplate();

		String response = restTemplate.getForObject(url, String.class);
		//ObjectMapper mapper;
		JsonNode root = mapper.readTree(response);

		String country = root.get("city").get("country").asText();
		JsonNode listNode = root.get("list");

		List<Map<String, Object>> forecastList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");

		for (JsonNode node : listNode) {
			long dt = node.get("dt").asLong() * 1000;
			String formattedDate = sdf.format(new Date(dt));

			Map<String, Object> map = new HashMap<>();
			map.put("formattedDate", formattedDate);
			map.put("main", node.get("main"));
			map.put("weather", node.get("weather"));

			forecastList.add(map);
		}

		// Optionally, reduce to one forecast every 8 data points (3-hour intervals → 1
		// per day)
		List<Map<String, Object>> dailyForecasts = forecastList.stream().filter(i -> forecastList.indexOf(i) % 8 == 0)
				.collect(Collectors.toList());

		Map<String, Object> result = new HashMap<>();
		result.put("country", country);
		result.put("forecasts", dailyForecasts);
		return result;
	}

	private String getForecaseURL(String city) {
		String url = forecastURL + "?q=" + city + "&appid=" + apiKey + "&units=metric";
		return url;
	}

	public String getURL(String city) {

		String url = baseUrl + "?q=" + city + "&appid=" + apiKey + "&units=metric";
		System.out.println("URL => " + url);
		return url;

	}

}
