# nptsp
The NP-TSP problem is a slight modification on the normal TSP, in which each city has a color - red or blue - and no more than 3 cities of the same color can be visited consecutively. The goal is to generate hard instances of NP-TSP and find good (but sometimes suboptimal) solutions to NP-TSP using NP-Complete heuristics. 

greedyFailure.py generates instances of NP-TSP where greedy algorithms would fail by partitioning the vertices into groups and leaving low-cost paths in some groups, while high-cost paths in others. A greedy algorithm would consume the low-cost groups immediately, and would be forced to take high-cost paths later, whereas an optimal algorithm would intersperse low-cost vertices with high-cost vertices in order to minimize the number of high-cost paths.

SimulatedAnnealing.java is a somewhat misleading name because it is a combination of both Simulated Annealing and a Genetic Algorithm. It solves instances of NP-TSP. A description of it is below:

Algorithm:


Start with a randomized greedy path. This is generated by starting at a random node, and from each node, branching to any child node with probability as a function of the edge length between the two nodes. Next run a modified combination of simulated annealing and a genetic algorithm as follows:


k-op: This is the standard shuffle method. Input is a path and the problem structure. Returns a new randomly shuffled path, shuffled with k chunks.


Crossover: This is a standard TSP-crossover implementation. Input is two paths and the problem structure. Takes a random chunk from the second path and adds it to a new path. From there, it adds in everything missing from the first chunk in order of appearance in the first path.


Now the simulated annealing algorithm will keep track of two paths, the path currently being manipulated and the best path seen by the algorithm. These paths are called path and bestPath respectively. Now begin with some temperature and some decay rate. At each iteration i, with probability equal to the temperature, choose to perform a k-op operation on the existing path. Otherwise, perform a crossover with the path and the best path seen so far. If the output paths for either of these steps violate the max 3 in a row constraint, dump the newly generated path and use the existing path. Now record if this path is the best path seen so far. Then modify the temperature by multiplying by decay rate. Repeat this process for a large number of iterations. Then return the best path seen so far.


Numbers: I chose to use 100 million iterations, since 10 million often lead to worse paths,
but 1 billion iterations did not seem to offer significant improvement over 100 million. The
initial temperature was set to 1 to force random flailing in the beginning, and lowered over
time to around .1 by the end.

Running Methodology: Overall, the algorithm took roughly 70 seconds to run per full-sized
input. It was manually (typing in start and stop values) multithreaded and was run locally over 4 threads (the number of cores in my laptop), taking roughly 3 hours.

Using these programs, I was able to get 7th in our university's competition (out of 120).
