package weather

import (
	"fmt"
	"encoding/json"
	"sync"
	"math/rand"
	"generator/utils"
)

type WeatherData struct {
	Timestamp  string
	SensorId   string
	SensorType string
	Readings   map[string]any
	Location   map[string]float64
	Status     string
}

func generateWeatherId(current int) string {
	return fmt.Sprintf("weather_%04d", current)
}

func generateWeatherReadings() map[string]any {
	check := rand.Intn(100)
	missingValue := check < 5
	fields := []string{"pressure", "wind_speed", "wind_direction", "precipitation"}
	choice := rand.Intn(4)
	result := make(map[string]any)
	result["temperature"] = utils.TruncateFloat(rand.Float64()*36, 2)
	result["humidity"] = rand.Intn(100)
	result["pressure"] = rand.Intn(100) + 950
	result["wind_speed"] = utils.TruncateFloat(rand.Float64()*60, 2)
	result["wind_direction"] = []string{"N", "NE", "E", "SE", "S", "SW", "W", "NW"}[rand.Intn(8)]
	result["precipitation"] = utils.TruncateFloat(rand.Float64()*10, 2)
	if missingValue {
		result[fields[choice]] = nil
	}
	return result
}

func generateWeatherLocation() map[string]float64 {
	result := make(map[string]float64)
	latitudes := []float64{59.1674, 31.9381, -44.2196, 36.3214}
	longitudes := []float64{-74.6660, -6.0664, 170.3022, 139.5357}
	choice := rand.Intn(4)
	result["latitude"] = latitudes[choice]
	result["longitude"] = longitudes[choice]
	return result
}

func checkForEmptyValues(data WeatherData) bool {
	for k, v := range data.Readings {
		if len(k) == 0 || v == nil {
			return true
		}
	}
	return false
}

func generateWeatherDataStatus(data WeatherData) string {
	var result string
	longitude := data.Location["longitude"]
	if longitude > 30 || longitude < -25.0 {
		humidity := data.Readings["humidity"].(int)
		temperature := data.Readings["temperature"].(float64)
		hasAnomalies := humidity > 90.0 || temperature > 32.2
		if hasAnomalies {
			result = "WARNING"
		}
	}
	if checkForEmptyValues(data) {
		result = "ERROR"
	}
	if len(result) == 0 {
		result = "OK"
	}
	return result
}

func generateWeatherDataStruct(current int) WeatherData {
	var result WeatherData
	result.Timestamp = utils.GenerateTimestamp(utils.Weather, current, 0)
	result.SensorId = generateWeatherId(current)
	result.SensorType = "weather"
	result.Readings = generateWeatherReadings()
	result.Location = generateWeatherLocation()
	result.Status = generateWeatherDataStatus(result)
	return result
}

func GenerateWeatherData(current int, c chan string, wg *sync.WaitGroup) {
	defer wg.Done()
	data := generateWeatherDataStruct(current)
	result, err := json.Marshal(data)
	if err != nil {
		fmt.Println("Unable to marshal json data for weather.")
		return 
	}
	c <- string(result)
}

/*
Weather data template:
{
  "timestamp": "2025-03-21T10:00:00Z",
  "sensor_id": "weather_001",
  "sensor_type": "weather",
  "readings": {
    "temperature": 22.5,
    "humidity": 55,
    "pressure": 1013,
    "wind_speed": 5.2,
    "wind_direction": "NE",
    "precipitation": 0.0
  },
  "location": {
    "latitude": 40.7128,
    "longitude": -74.0060
  },
  "status": "OK"
}
*/




