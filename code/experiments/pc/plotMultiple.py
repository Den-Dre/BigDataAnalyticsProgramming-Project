import pandas as pd
import matplotlib.pyplot as plt
from glob import glob

# plt.locator_params(axis='x', nbins=10)
file_names = [f for f in glob('*.acc') if f.startswith('10E-7') or f.startswith('10E-13') or f.startswith('10E-1.')]
print([x for x in file_names])
step = 20
patterns = ['-', 'dashdot', '-']
for file_name, pattern in zip(file_names, patterns):
    df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
    plt.plot(df.index[::step], df.iloc[::step], linestyle=pattern, linewidth=1,
             label=r"$\eta =$ " + file_name.split('/')[-1].split('.out')[0],
             )
plt.xlabel('Number of examples trained with')
plt.ylabel('Accuracy')
plt.title(r"PC's accuracy in function of $\eta$")
leg = plt.legend(fontsize=15)
for leg_obj in leg.legendHandles:
    leg_obj.set_linewidth(2.0)
plt.show()

#
# for file_name, pattern in zip(file_names, patterns):
#     plt.figure()
#     df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
#     plt.plot(df.index[::step], df.iloc[::step], linestyle=pattern, linewidth=1, label=file_name.split('/')[-1].split('.out')[0])
# plt.show()
