import pandas as pd
import matplotlib.pyplot as plt
from glob import glob

# plt.locator_params(axis='x', nbins=10)
file_names = [f for f in glob('./std001randw/*')]
file_names.sort()
print([x for x in file_names])
fig, ax = plt.subplots()
ax.set_prop_cycle('color', ['red', 'green', 'blue', 'yellow', 'hotpink', 'cyan', 'teal'][::-1])
step = 20
for file_name in file_names:
    df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
    plt.plot(df.index[::step], df.iloc[::step],
             linewidth=1,
             label=r"$\eta =$ " + file_name.split('/')[-1].split('.')[0],
             )
plt.xlabel('Number of examples trained with')
plt.ylabel('Accuracy')
plt.title(r"PC's accuracy in function of $\eta$")
# colors = ['red', 'green', 'blue', 'yellow', 'pink', 'cyan', 'orange']
handles, labels = plt.gca().get_legend_handles_labels()
order = [3, 4, 5, 6, 0, 1, 2]
leg = plt.legend(handles=[handles[i] for i in order],
                 labels=[labels[i] for i in order],
                 fontsize=15,
                 loc='best',
                 ncol=2)
for leg_obj in leg.legendHandles:
    leg_obj.set_linewidth(2.0)
plt.show()

#
# for file_name, pattern in zip(file_names, patterns):
#     plt.figure()
#     df = pd.read_csv(file_name, index_col=0, delim_whitespace=True)
#     plt.plot(df.index[::step], df.iloc[::step], linestyle=pattern, linewidth=1, label=file_name.split('/')[-1].split('.out')[0])
# plt.show()
