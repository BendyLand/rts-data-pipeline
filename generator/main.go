package main

import (
	"context"
	"strings"
	"fmt"
	"math/rand"
	"os"
	"os/signal"
	"encoding/json"
	"sync"
	"syscall"
	"time"

	"generator/airquality"
	"generator/weather"
	"github.com/segmentio/kafka-go"
)

func main() {
	// Create a context that is canceled on an interrupt signal.
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Set up a channel to listen for interrupt signals.
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)
	go func() {
		sig := <-sigChan
		fmt.Printf("Received signal: %s. Initiating graceful shutdown...\n", sig)
		cancel()
	}()

	// Initialize Kafka writer outside of the loop for reuse.
	writer := kafka.NewWriter(kafka.WriterConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "sensor-data",
	})
	// Ensure the writer is closed on exit.
	defer func() {
		writer.Close()
		fmt.Println("Kafka writer closed.")
	}()

	// Infinite loop that periodically generates data.
	// outerOffset := 10
	for {
		// Check for cancellation before starting an iteration.
		select {
		case <-ctx.Done():
			fmt.Println("Exiting main loop.")
			return
		default:
		}

		var wg sync.WaitGroup

		// Launch data generation concurrently.
		weatherData := make([]string, 0, 1000)
		airData := make([]string, 0, 1000)

		var muWeather sync.Mutex
		var muAirQuality sync.Mutex

		for i := range 1000 {
			offset := rand.Intn(21) + 10 // + outerOffset
			wg.Add(2)
			go func(i int, o int) {
				defer wg.Done()
				data := weather.GenerateWeatherDataStruct(i, offset)
				jsonData, err := json.Marshal(data)
				if err != nil {
					fmt.Println("Failed to marshal weather data:", err)
					return
				}
				muWeather.Lock()
				weatherData = append(weatherData, string(jsonData))
				muWeather.Unlock()
			}(i, offset)
			go func(i int, o int) {
				defer wg.Done()
				data := airquality.GenerateAirQualityDataStruct(i, offset)
				jsonData, err := json.Marshal(data)
				if err != nil {
					fmt.Println("Failed to marshal air quality data:", err)
					return
				}
				muAirQuality.Lock()
				airData = append(airData, string(jsonData))
				muAirQuality.Unlock()
			}(i, offset)
		}
		wg.Wait()

		// Interleave the data in randomized 1:1 order
		interleaved := make([]string, 0, 2000)
		for i := range 1000 {
			if rand.Intn(2) == 0 {
				interleaved = append(interleaved, weatherData[i], airData[i])
			} else {
				interleaved = append(interleaved, airData[i], weatherData[i])
			}
		}

		weatherCount := 0
		airCount := 0
		for _, entry := range interleaved {
			if strings.Contains(entry, `"SensorType":"weather"`) {
				weatherCount++
			} else if strings.Contains(entry, `"SensorType":"air_quality"`) {
				airCount++
			}
		}
		fmt.Printf("Batch sensor distribution: Weather=%d, Air Quality=%d\n", weatherCount, airCount)

		// Buffer for batching messages.
		batchSize := 50
		batch := make([]kafka.Message, 0, batchSize)

		// Process and batch messages.
		for _, entry := range interleaved {
			msg := kafka.Message{
				Key:   []byte("sensor-key"),
				Value: []byte(entry),
			}
			batch = append(batch, msg)

			if len(batch) >= batchSize {
				if err := writer.WriteMessages(ctx, batch...); err != nil {
					fmt.Println("Error writing batch:", err)
				} else {
					fmt.Println("Batch sent successfully!")
				}
				batch = batch[:0]
			}
		}

		// Send any remaining messages.
		if len(batch) > 0 {
			if err := writer.WriteMessages(ctx, batch...); err != nil {
				fmt.Println("Error writing final batch:", err)
			} else {
				fmt.Println("Final batch sent successfully!")
			}
		}

		// Delay before the next iteration.
		delay := rand.Intn(5) + 1
		select {
		case <-ctx.Done():
			return
		case <-time.After(time.Second * time.Duration(delay)):
		}
		// outerOffset += rand.Intn(10) + 1
	}
}
