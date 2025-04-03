#!/bin/bash

cd rts_data_pipeline/data/parquet/enriched_combined
duckdb
# when you quit duckdb, it will automatically move on to the aggregated directory

cd ../aggregated_combined
duckdb

