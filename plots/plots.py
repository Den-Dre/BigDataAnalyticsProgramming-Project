import pandas as pd
import matplotlib.pyplot as plt
import argparse
from glob import glob

parser = argparse.ArgumentParser()
choices = [c for c in glob(r'../code/savedOutputs/*.acc')]
parser.add_argument('data_file_path', help='The name of the .csv file to plot', choices=choices, type=str)
args = parser.parse_args()

names = ['Number of examples', 'Accuracy']
# plt.locator_params(axis='x', nbins=10)
df = pd.read_csv(f'{args.data_file_path}', index_col=0, names=names,  delim_whitespace=True)
# df[names[1]] *= 100
# print(df.head())

step = 10
plt.plot(df.index[::step], df.iloc[::step], linewidth=0.7)
# df.plot(xlabel='Number of examples trained with', ylabel='Accuracy')
plt.xlabel('Number of examples trained with')
plt.ylabel('Accuracy')
plt.title(r'Learning curve of Perceptron (noisy dataset)')
plt.show()
