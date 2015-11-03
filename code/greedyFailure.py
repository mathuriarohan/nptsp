#Author: Rohan Mathuria
#GENERATES NP-TSP PROBLEMS ON WHICH GREEDY ALGORITHMS FAIL

import random

OUTPUT_PATH = "IO/"

def gen():
	#randomly generate optimal path
	bestPath = list(range(0, 50))
	random.shuffle(bestPath)
	a = 1

	#create a somewhat-random charachter assignment consistent with our optimal path
	charAss = [''] * 50
	l = ['R', 'B']
	for i in range(50):
		if (i%2 == 0):
			random.shuffle(l)
		charAss[bestPath[i]] = l[(i % 2)]

	#Create edge weights that will fail greedy algorithm users. 
	#Edges are divided into 4 groups: low, medium-low, medium-high, and high.
	weights = []

	for i in range(50):
		weights.append([0]*50)

	def getLow():
		return random.randint(1, 10)

	def getMedLow():
		return random.randint(11, 20)

	def getMedHigh():
		return random.randint(15, 50)

	def getHigh():
		return random.randint(81, 100)

	#Vertices are divided into two groups: low and high. Edges from low to low have low cost, whereas those from 
	#high to high have high cost. Edges from low to high have medium or high cost, and the optimal path alternates
	#from low to high repeatedly.

	A = [bestPath[i] for i in range(50) if (i%4) == 0]
	B = [bestPath[i] for i in range(50) if (i%4) == 1]
	C = [bestPath[i] for i in range(50) if (i%4) == 2]
	D = [bestPath[i] for i in range(50) if (i%4) == 3]

	for i in range(50):
		for j in range(50):
			bpI = bestPath[i]
			bpJ = bestPath[j]

			if (weights[bpJ][bpI] != 0):
				weights[bpI][bpJ] = weights[bpJ][bpI]

			elif (i == j):
				continue

			elif (j == i+1):
				weights[bpI][bpJ] = getMedLow()

			elif (((bpI in A) and (bpJ in A)) or ((bpI in B) and (bpJ in B))):
				weights[bpI][bpJ] = getLow()

			elif ((bpI in C) and (bpJ in C)):
				weights[bpI][bpJ] = getLow()

			elif ((bpI in D) and (bpJ in D)):
				weights[bpI][bpJ] = getHigh()

			elif (((bpI in A) and (bpJ in B)) or ((bpI in B) and (bpJ in C)) or ((bpI in C) and (bpJ in D)) or ((bpI in D) and (bpJ in A))):
				weights[bpI][bpJ] = getMedHigh()

			else:
				weights[bpI][bpJ] = getHigh()

	for i in range(50):
		bestPath[i] += 1

	return (bestPath, weights, charAss)
g = open(OUTPUT_PATH + 'output.out', 'w')
for j in range(1, 4):
	bestPath, weights, charAss = gen()
	f = open(OUTPUT_PATH + str(j) + '.in', 'w')
	outstring = '50\n'
	for i in range(50):
		outstring += ' '.join([str(elem) for elem in weights[i]]) + '\n'
	outstring += ''.join(charAss) + '\n'

	f.write(outstring)
	f.close()

	g.write(' '.join([str(elem) for elem in bestPath]) + '\n')

g.close()

