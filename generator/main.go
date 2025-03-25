package main

import (
	"context"
	"fmt"
	"math/rand"
	"os"
	"os/signal"
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
	for {
		// Check for cancellation before starting an iteration.
		select {
		case <-ctx.Done():
			fmt.Println("Exiting main loop.")
			return
		default:
		}

		c := make(chan string)
		var wg sync.WaitGroup

		// Launch data generation concurrently.
		for i := range 1000 {
			wg.Add(1)
			go weather.GenerateWeatherData(i, c, &wg)
			wg.Add(1)
			go airquality.GenerateAirQualityData(i, c, &wg)
		}
		go func() {
			wg.Wait()
			close(c)
		}()

		// Buffer for batching messages.
		batchSize := 50
		batch := make([]kafka.Message, 0, batchSize)

		// Process and batch messages.
		for entry := range c {
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
	}
}
