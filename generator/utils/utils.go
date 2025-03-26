package utils

import (
	"math"
	"math/rand"
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

func generateWeatherTimestamp(current int) string {
	// Define a fixed starting point (hardcoded for reproducibility)
	baseTime := time.Date(2020, time.January, 1, 0, 0, 0, 0, time.UTC)
	// Generate 10 - 30 second offset for slight randomness in data
	offset := rand.Intn(21) + 10
	// Increment the base time by 'current' seconds (adjust the multiplier if needed)
	newTime := baseTime.Add(time.Duration(current+offset) * time.Second)
	// Format the timestamp in ISO8601 format (RFC3339)
	return newTime.Format(time.RFC3339)
}

func generateAirQualityTimestamp(current int) string {
	baseTime := time.Date(2020, time.January, 1, 0, 0, 0, 0, time.UTC)
	offset := rand.Intn(21) + 10
	newTime := baseTime.Add(time.Duration(current+offset) * time.Second)
	return newTime.Format(time.RFC3339)
}

func GenerateTimestamp(op Option, curWeather int, curAirQuality int) string {
	switch op {
	case Weather:
		return generateWeatherTimestamp(curWeather)
	case AirQuality:
		return generateAirQualityTimestamp(curAirQuality)
	}
	return ""
}

