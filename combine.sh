#!/bin/bash


echo "Combining data..."
cd rts_data_pipeline
sbt run combine
cd ..

mv rts_data_pipeline/data/parquet/enriched_combined/part*.parquet rts_data_pipeline/data/parquet/enriched_combined/enriched_data.parquet 
mv rts_data_pipeline/data/parquet/aggregated_combined/part*.parquet rts_data_pipeline/data/parquet/aggregated_combined/aggregated_data.parquet 

echo "Data processed successfully!"
echo "Files can be found at:"
echo "rts_data_pipeline/data/parquet/enriched_combined/"
echo "and"
echo "rts_data_pipeline/data/parquet/aggregated_combined/"

