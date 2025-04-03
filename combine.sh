#!/bin/bash

echo "Combining files..."
cd rts_data_pipeline
sbt "runMain io.github.bendyland.rtsdatapipeline.Main combine"
echo "Files combined successfully!"
cd ..

mv rts_data_pipeline/data/parquet/enriched_combined/part*.parquet rts_data_pipeline/data/parquet/enriched_combined/enriched_data.parquet 
mv rts_data_pipeline/data/parquet/aggregated_combined/part*.parquet rts_data_pipeline/data/parquet/aggregated_combined/aggregated_data.parquet 

echo "Files can be found at:"
echo "rts_data_pipeline/data/parquet/enriched_combined/enriched_data.parquet"
echo "and"
echo "rts_data_pipeline/data/parquet/aggregated_combined/aggregated_data.parquet"

