package com.rajavivek.weather_app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rajavivek.weather_app.entity.Weather;
import com.rajavivek.weather_app.service.WeatherService;

@Controller
public class WeatherController {
	
	@Autowired
	private WeatherService weatherService;


    @GetMapping("/")
    public String welcome() {
        return "welcome"; 
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam String city, ModelMap model) {
    	
    	Weather currentWeather = weatherService.getWeatherWithCity(city);
    	
        model.put("city", currentWeather.getCity());
        model.put("temp", currentWeather.getTemp());
        model.put("humidity", currentWeather.getHumidity());
        model.put("desc", currentWeather.getDescription());

        return "weather"; // weather.html
    }
    
    
    @GetMapping("/forecast")
    public String getForecast(@RequestParam("city") String city, ModelMap model) {
        try {
            Map<String, Object> forecastData = weatherService.getForecast(city);

            String country = (String) forecastData.get("country");
            
			List<Map<String, Object>> forecastsList = (List<Map<String, Object>>) forecastData.get("forecasts");

            model.addAttribute("city", city);
            model.addAttribute("country", country);
            model.addAttribute("forecastsList", forecastsList);

            return "forecast";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Could not fetch forecast for city: " + city);
            return "welcome";
        }
    }
    
}
