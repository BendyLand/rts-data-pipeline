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
	baseTime := time.Now()
	// Increment the base time by 'current' seconds (adjust the multiplier if needed)
	newTime := baseTime.Add(time.Duration(current+offset) * time.Millisecond)
	// Format the timestamp in ISO8601 format (RFC3339)
	return newTime.Format("2006-01-02T15:04:05.000Z")
}

func generateAirQualityTimestamp(current int, offset int) string {
	baseTime := time.Now()
	newTime := baseTime.Add(time.Duration(current+offset) * time.Millisecond)
	return newTime.Format("2006-01-02T15:04:05.000Z")
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

