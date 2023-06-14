# icntr - Integer-Based Distinct Count Problem Solver
*icntr* is a distinct occurrence counter for anything that can be turned into an unsigned integer

## IPv4 Counter Example
This repository contains an example for finding unique IPv4 addresses in a large file.

It takes 25-35 minutes to find 1 billion unique addresses within a 120GB (8 billion lines, one address per line) file on a 4-core ryzen-3 3200G,
with enabled input validation and live progress tracking.

Memory consumption depends heavily on the number of workers and the chunk size.
Chunk size is a portion of the file (in lines) read into memory by each worker.

## IPv4 Counter CLI
The syntax is:
```shell
icntr FILE [number-of-workers:4] [chunk-size:100000]
```
Gradle example:
```shell
# *nix
gradlew run --args='./ip_addresses 4 100000'
# Windows
gradlew run --args="ip_addresses 4 100000"
```
