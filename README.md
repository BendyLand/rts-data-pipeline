# Local Real-Time Streaming Pipeline (RTS)

A self-contained real-time streaming data pipeline that:

 1) **Bootstraps** Kafka & ZooKeeper locally  
 2) **Generates** interleaved weather & air-quality JSON messages in Go  
 3) **Publishes** them to a Kafka topic (`sensor-data`)  
 4) **Consumes** the topic via Spark Structured Streaming (Scala + SBT)  
 5) **Writes** enriched & aggregated results to Parquet  
 6) **Combines** partitioned Parquets into single files for easy analysis  
 7) **Inspects** results in DuckDB  

---

## Key Features

 - **End-to-end orchestration** via simple Bash scripts  
 - **Graceful shutdown** of long-running generator & Spark job  
 - **Dynamic topic reset** (delete/create + cleanup)  
 - **Parquet output** for both enriched and aggregated streams  
 - **Combine step** to “compact” partitions into single files  
 - **Easy inspection** via `inspect_result.sh` and DuckDB  

---

## Tech Stack

 - **Generator:** Go → JSON → Kafka (`segmentio/kafka-go`)  
 - **Stream Processor:** Scala + Spark Structured Streaming → Parquet  
 - **Orchestration:** Bash scripts (`start.sh`, `demo.sh`, etc.)  
 - **Local Broker:** Apache Kafka & ZooKeeper (Homebrew paths on macOS)  
 - **Analytics:** DuckDB for ad-hoc querying  

---

## Prerequisites

 - **macOS** (or compatible Linux)  
 - **Go 1.18+** (generator)  
 - **Java 11+**, **SBT** (stream processor)  
 - **Apache Kafka & ZooKeeper** (e.g. via Homebrew)  
 - **DuckDB CLI** (for inspection)  
 - **GNU bash**, **pbcopy** (macOS clipboard)  

---

## Setup

 1) **Clone the repo**  
```bash
git clone https://github.com/BendyLand/rts-data-pipeline
cd rts-data-pipeline
```

 2) Install Kafka & ZooKeeper (via Homebrew)
```bash
brew install kafka # or your platform equivalent
```

 3) Build the Go generator
```bash
cd generator
go build -o generator_bin
cd ..
```

 4) Verify SBT project
```bash
cd rts_data_pipeline
sbt compile
cd ..
```

 5) Make scripts executable
```bash
chmod +x $(find . -name "*.sh" | paste -sd\  -)
```

## Running the Pipeline

From the project root:
```bash
./demo.sh
```

What happens:
 1) stop.sh -> tears down any old Kafka, ZK, consumers
 2) start.sh -> starts ZooKeeper -> Kafka
 3) reset_topic.sh -> deletes & recreates sensor-data + cleanup
 4) Go generator -> builds & runs for 30 s, then SIGINT shutdown
 5) Spark job -> sbt run (default mode: streaming -> Parquet)
 6) 30 s “collect” loop (dots)
 7) SIGINT to stop Spark consumer
 8) combine.sh -> runs Scala Main combine -> merges Parquet parts
 9) stop.sh & stop_jvm.sh -> tear down any remaining processes

## Script Details

 - ./demo.sh
    - Automates the full pipeline in one shot.
    - Tears down -> boots up Kafka/ZK -> resets topic -> runs generator & Spark -> combines output -> cleans up.

 - scripts/start.sh
    - Launches ZooKeeper → Kafka (Homebrew paths)
    - Writes PIDs to /tmp/zookeeper.pid & /tmp/kafka.pid

 - scripts/stop.sh
    - Reads PID files → sends SIGTERM → waits → SIGKILL if needed
    - Cleans up Kafka, ZK, consumer processes

 - scripts/reset_topic.sh
    - Deletes & recreates Kafka topic sensor-data
    - Clears Spark checkpoints & old Parquet directories
    - Empties consumer log

 - scripts/consumer.sh

    - Spawns kafka-console-consumer on sensor-data -> logs to /tmp/kafka-consumer.log (for manual debugging)

 - scripts/combine.sh
    - Invokes Scala Main combine via SBT
    - Moves part*.parquet into single enriched_data.parquet & aggregated_data.parquet
    - Stops all processes

 - scripts/inspect_result.sh
    - pbcopy a simple SELECT * … SQL for enriched vs aggregated
    - Launches DuckDB CLI in each directory

⸻

## Customization
 - Collection duration in demo.sh (currently 30 s + 10 s startup delay)
 - Kafka broker/port and topic name in reset_topic.sh & consumer.sh
 - Spark app name or master in SparkSessionFactory.scala
 - Parquet output paths in DataSink methods

## License

This project is licensed under the MIT License.

