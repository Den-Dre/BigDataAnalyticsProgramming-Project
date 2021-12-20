import pandas as pd
import matplotlib.pyplot as plt
from glob import glob

plt.locator_params(axis='x', nbins=10)
file_names = glob(r'*.acc')
print([x for x in file_names])
step = 10
for file_name in file_names:
    df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
    plt.plot(df.index[::step], df.iloc[::step], linewidth=1, label=r'$n_{min} = $ ' + file_name.split('/')[-1].split('.out')[0])
plt.xlabel('Number of examples trained with')
plt.ylabel('Accuracy')
plt.title(r"VFDT's accuracy in function of $n_{min}$")
leg = plt.legend()
for l_obj in leg.legendHandles:
    l_obj.set_linewidth(2.0)
plt.show()

