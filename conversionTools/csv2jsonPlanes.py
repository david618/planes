import json
import re
import glob
import os

def convert(infile, outfile):
    print "Start"


    PATTERN = re.compile(r'''((?:[^,"']|"[^"]*"|'[^']*')+)''')

    fin = open(infile)
    fout = open(outfile, "w")

    for line in fin:
        
        #print line
        #field = line.strip().split(",")
	field = PATTERN.split(line.strip())[1::2]
	#print field

        row = {}

        row['id'] = int(field[0])
        row['ts'] = long(field[1])
        row['speed'] = float(field[2])
        row['dist'] = float(field[3])
        row['bearing'] = float(field[4])
        row['rtid'] = int(field[5])
        row['orig'] = field[6].replace('"','')
        row['dest'] = field[7].replace('"','')
        row['secsToDep'] = int(field[8])
        row['lon'] = float(field[9])
        row['lat'] = float(field[10])

                
        jsonStr = json.dumps(row)

        #print jsonStr
        fout.write(jsonStr + "\n")

    fin.close()
    fout.close()




if __name__ == '__main__':
	for file in glob.glob("/mnt/resource/s3/rttestdata/planes/lat88/planes0000?"):
		path=os.path.dirname(file)
		basename=os.path.basename(file)
		#if basename == "planes00003" or basename == "planes00004":
		#	continue
		ofile=os.path.join(path,"json",basename + ".json")
		print("convert " + file + " to " + ofile)
	
		convert(file,ofile)
		
        
