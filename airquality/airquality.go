package airquality

import (
	"fmt"
	"encoding/json"
	"sync"
	"generator/utils"
	"math/rand"
)

type AirQualityData struct {
	Timestamp  string
	SensorId   string
	SensorType string
	Readings   map[string]any
	Location   map[string]float64
	Status     string
}

func generateAirQualityId(current int) string {
	return fmt.Sprintf("air_quality_%04d", current)
}

func generateAirQualityReadings() map[string]any {
	check := rand.Intn(100)
	missingValue := check < 5
	fields := []string{"ozone", "CO"}
	choice := rand.Intn(2)
	result := make(map[string]any)
	result["PM2_5"] = rand.Intn(125)
	result["PM10"] = rand.Intn(375)
	result["ozone"] = rand.Intn(200)
	result["CO"] = rand.Intn(40)
	result["NO2"] = rand.Intn(115)
	if missingValue {
		result[fields[choice]] = nil
	}
	return result
}

func generateAirQualityLocation() map[string]float64 {
	result := make(map[string]float64)
	latitude := utils.TruncateFloat(rand.Float64()*90, 4)
	longitude := utils.TruncateFloat(rand.Float64()*180, 4)
	if rand.Float64() < 0.5 {
		latitude = -latitude
	}
	if rand.Float64() < 0.5 {
		longitude = -longitude
	}
	result["latitude"] = latitude
	result["longitude"] = longitude
	return result
}

func checkForEmptyValues(data AirQualityData) bool {
	for k, v := range data.Readings {
		if len(k) == 0 || v == nil {
			return true
		}
	}
	return false
}

func generateAirQualityDataStatus(data AirQualityData) string {
	var result string
	moderates := 0
	dangerous := 0
	for k, v := range data.Readings {
		switch k {
		case "PM2_5":
			if v.(int) > 120 {
				dangerous++
			} else if v.(int) > 60 {
				moderates++
			} 
		case "PM10":
			if v.(int) > 350 {
				dangerous++
			} else if v.(int) > 100 {
				moderates++
			} 
		case "ozone":
			if v != nil {
				if v.(int) > 175 {
					dangerous++
				} else if v.(int) > 100 {
					moderates++
				} 
			}
		case "CO":
			if v != nil {
				if v.(int) > 35 {
					dangerous++
				} else if v.(int) > 9 {
					moderates++
				} 
			}
		case "NO2":
			if v.(int) > 100 {
				dangerous++
			} else if v.(int) > 50 {
				moderates++
			} 
		}
	}
	switch {
	case dangerous >= 2:
		result = "DANGEROUS"
	case moderates >= 3:
		result = "MODERATE"
	case moderates > 1 || (dangerous > 0 && moderates < 3):
		result = "ACCEPTABLE"
	default:
		result = "SAFE"
	}
	return result
}

func generateAirQualityDataStruct(current int) AirQualityData {
	var result AirQualityData
	result.Timestamp = utils.GenerateTimestamp()
	result.SensorId = generateAirQualityId(current)
	result.SensorType = "air_quality"
	result.Readings = generateAirQualityReadings()
	result.Location = generateAirQualityLocation()
	result.Status = generateAirQualityDataStatus(result)
	return result
}

func GenerateAirQualityData(current int, c chan string, wg *sync.WaitGroup) {
	defer wg.Done()
	data := generateAirQualityDataStruct(current)
	result, err := json.Marshal(data)
	if err != nil {
		fmt.Println("Unable to marshal json data for air quality.")
		return 
	}
	c <- string(result)
}

/*
Air quality template:
{
  "timestamp": "2025-03-21T10:00:00Z",
  "sensor_id": "air_001",
  "sensor_type": "air_quality",
  "readings": {
    "PM2_5": 12.3,
    "PM10": 20.5,
    "ozone": 30,
    "CO": 0.5,
    "NO2": 18
  },
  "location": {
    "latitude": 40.7128,
    "longitude": -74.0060
  },
  "status": "OK"
}
*/

