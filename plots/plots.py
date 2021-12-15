import pandas as pd
import matplotlib.pyplot as plt
import argparse
from glob import glob

parser = argparse.ArgumentParser()
choices = [c.split('/')[-1] for c in glob(r'../code/*.acc')]
parser.add_argument('data_file_path', help='The name of the .csv file to plot', choices=choices, type=str)
args = parser.parse_args()

names = ['Number of examples', 'Accuracy']
df = pd.read_csv(f'../code/{args.data_file_path}', index_col=0, names=names,  delim_whitespace=True)
# df[names[1]] *= 100
# print(df.head())
df.plot(xlabel='Number of examples trained with', ylabel='Accuracy')
plt.show()
