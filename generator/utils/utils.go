package utils

import (
	"math"
	"time"
)

type Option int

const (
	Weather Option = iota
	AirQuality
)

func TruncateFloat(val float64, decimals int) float64 {
	factor := math.Pow(10, float64(decimals))
	return math.Trunc(val*factor) / factor
}

func generateWeatherTimestamp(current int, offset int) string {
	// Define a fixed starting point (hardcoded for reproducibility)
	baseTime := time.Date(2020, time.January, 1, 0, 0, 0, 0, time.UTC)
	// Increment the base time by 'current' seconds (adjust the multiplier if needed)
	newTime := baseTime.Add(time.Duration(current+offset) * time.Second)
	// Format the timestamp in ISO8601 format (RFC3339)
	return newTime.Format(time.RFC3339)
}

func generateAirQualityTimestamp(current int, offset int) string {
	baseTime := time.Date(2020, time.January, 1, 0, 0, 0, 0, time.UTC)
	newTime := baseTime.Add(time.Duration(current+offset) * time.Second)
	return newTime.Format(time.RFC3339)
}

func GenerateTimestamp(op Option, curWeather int, curAirQuality int, offset int) string {
	switch op {
	case Weather:
		return generateWeatherTimestamp(curWeather, offset)
	case AirQuality:
		return generateAirQualityTimestamp(curAirQuality, offset)
	}
	return ""
}

