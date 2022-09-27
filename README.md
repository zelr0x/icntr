# icntr - Integer-Based Distinct Count Problem Solver
*icntr* is a distinct occurrence counter for anything that can be turned into an unsigned integer

## Examples
This repository contains an example for finding unique IPv4 addresses in a large file.

It takes roughly 35 minutes to find all unique addresses within a 120GB file (8 billion lines) on my 4-core ryzen-3 3200G.
And that is with some input validation and a lot of live progress tracking.
```shell
icntr FILE [number-of-workers:4] [chunk-size:100000]
```
i.e.
```shell
icntr ./ip_addresses 4 100000
```
