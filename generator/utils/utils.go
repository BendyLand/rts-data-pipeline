package utils

import (
	"math"
	"math/rand"
	"fmt"
)

func TruncateFloat(val float64, decimals int) float64 {
	factor := math.Pow(10, float64(decimals))
	return math.Trunc(val*factor) / factor
}

func GenerateTimestamp() string {
	yearMax := 2050
	yearMin := 1900
	year := rand.Intn(yearMax-yearMin+1) + yearMin
	month := rand.Intn(12) + 1
	day := rand.Intn(30) + 1
	hour := rand.Intn(11) + 1
	minute := rand.Intn(58) + 1
	second := rand.Intn(58) + 1
	result := fmt.Sprintf("%d-%02d-%02dT%02d:%02d:%02dZ", year, month, day, hour, minute, second)
	return result
}


