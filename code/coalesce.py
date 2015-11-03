#small script to coalesce results into one result.

T = 495
f = open("answer.out", 'w')
for i in range(1, T+1):
	fi = open("outputTest/answer"+str(i)+".out")
	f.write(fi.readline())
	fi.close()
f.close()