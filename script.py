from confluent_kafka import Consumer

conf = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': 'my-consumer-group',
    'auto.offset.reset': 'earliest'  # start at the beginning if no offset is committed
}

consumer = Consumer(conf)
consumer.subscribe(['sensor-data'])

print("Waiting for messages...")

try:
    while True:
        msg = consumer.poll(1.0)  # timeout in seconds

        if msg is None:
            continue  # no message yet
        if msg.error():
            print(f"Error: {msg.error()}")
        else:
            print(f"Received: {msg.value().decode('utf-8')}")

except KeyboardInterrupt:
    print("Stopping consumer.")

finally:
    consumer.close()

