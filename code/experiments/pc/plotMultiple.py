import pandas as pd
import matplotlib.pyplot as plt
from glob import glob

plt.locator_params(axis='x', nbins=10)
file_names = glob(r'*.acc')
print([x for x in file_names])
step = 100
for file_name in file_names:
    df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
    plt.plot(df.index[::step], df.iloc[::step], linewidth=1, label=file_name.split('/')[-1].split('.out')[0])
plt.xlabel('Number of examples trained with')
plt.ylabel('Accuracy')
plt.legend()
plt.show()

