import os
import sys
import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
import math
from dataclasses import dataclass

@dataclass
class Config:
    root_dir_name: str
    out_dir_name: str
    save_results: bool
    show_results: bool
    boxprops: dict
    flierprops: dict
    medianprops: dict
    algorithms: list
    algorithms2: list
    colors: list

    def __init__(self, argv):
        if len(argv) > 1:
            self.root_dir_name = argv[1]
        else:
            self.root_dir_name = 'data_2023-04-11'
        self.out_dir_name = self.root_dir_name + '/plot/'
        self.save_results = False
        self.show_results = False
        self.save_results = True
        #self.show_results = True

        self.boxprops = dict(linestyle='-', linewidth=3, color='black')
        self.flierprops = dict(marker='o', markerfacecolor='black', markersize=4, markeredgecolor='none')
        self.medianprops = dict(linestyle='-', linewidth=4, color='black')

        self.algorithms = [random,single,incremental]

        self.algorithms2 = self.algorithms.copy()
        self.algorithms2.remove(incremental)

        self.colors = ['#2c7bb6', '#abd9e9', '#ffffbf', '#fdae61', '#d7191c']


def main(argv):
    config = Config(argv)

    set_graphics_options(config)
    df_data = prepare_data(config)

    plot_type(df_data.copy(), True, config)
    plot_relative_time(df_data.copy(), config)
    plot_relative_configurations(df_data.copy(), config)
    plot_absolute_time(df_data.copy(), config)
    plot_absolute_configurations(df_data.copy(), config)
    plot_scatter_absolute_configurations(df_data.copy(), config, 240)

    print_error(df_data.copy(), config)
    print_resources(df_data.copy(), config)


def set_graphics_options(config):
    pd.set_option('display.max_columns', None)
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_colwidth', None)

    if config.save_results:
        matplotlib.use('cairo')

    font = {'size'   : 34}
    matplotlib.rc('font', **font)


def create_plot(name, config, factor):
    # Create output directory
    if not os.path.exists(config.out_dir_name):
        try:
            os.mkdir(config.out_dir_name)
        except OSError:
            print ("Failed to create output directory %s" % output_path)
            os.exit(-1)

    size = 15
    plt.gcf().set_size_inches(size, size/factor)

    if config.show_results:
        plt.show()

    if config.save_results:
        file_name = config.out_dir_name + name + '.pdf'
        print('Writing ' + file_name)
        plt.savefig(file_name, format='pdf', dpi=600, bbox_inches='tight', pad_inches=0)

    plt.close()


def create_csv(df, name, config):
    if config.show_results:
        print(df)

    if config.save_results:
        df.to_csv(config.out_dir_name + name + '.csv', index=False, sep = ';')


def create_table(df, name, config):
    table = df.style.format(decimal='.', thousands=',', precision=2, escape="latex").to_latex(multicol_align='c')

    if config.show_results:
        print(table)

    if config.save_results:
        with open(config.out_dir_name + name + '.tex', 'w') as f:
            print(table, file=f)


def prepare_data(config):
    df_parts = []
    fragment_dirs = [f.path for f in os.scandir(config.root_dir_name) if f.is_dir()]
    print(fragment_dirs)
    for fragment_dir in fragment_dirs:
        data_dirs = [f.path for f in os.scandir(fragment_dir) if f.is_dir() and os.path.basename(os.path.normpath(f.path)).startswith('data')]
        for data_dir in data_dirs:
            print(data_dir)
            df = pd.read_csv(data_dir + "/runData.csv", sep = ';')
            df_algo = pd.read_csv(data_dir + "/algorithms.csv", sep = ';')
            df_models = pd.read_csv(data_dir + "/models.csv", sep = ';')
            key = ['AlgorithmID']
            df = df.join(df_algo.set_index(key), on=key, rsuffix="_algo")
            key = ['ModelID']
            df = df.join(df_models.set_index(key), on=key, rsuffix="_model")
            df_parts.append(df)

    df_data = pd.concat(df_parts)

    # Columns
    df_data = df_data.rename(columns={"Name": "Algorithm", "Name_model": "ModelName", "ConfigurationVerificationCount": "Configurations"}, errors="raise")
    print(list(df_data.columns))
    print(len(df_data))

    # Filter
    df_data = df_data[df_data['InteractionSize'] > 0]
    df_data = df_data[df_data['T'] > 0]

    df_data = df_data.replace({'Algorithm': 'NaiveRandom'}, random)
    df_data = df_data.replace({'Algorithm': 'Single'}, single)
    df_data = df_data.replace({'Algorithm': 'IterativeNaiveRandom'}, 'IncRandom')
    df_data = df_data.replace({'Algorithm': 'IterativeSingle'}, 'IncSingle')
    df_data = df_data.replace({'Algorithm': 'ForwardBackwardOld'}, oldIncremental)
    df_data = df_data.replace({'Algorithm': 'ForwardBackward'}, incremental)
    df_data = df_data.replace({'Algorithm': 'ForwardBackward2'}, incremental2)
    df_data = df_data.replace({'Algorithm': 'ForwardBackward10'}, incremental10)

    df_data['Type'] = \
        df_data['FoundMergedUpdatedIsSubsetFaultyUpdated'] + \
        df_data['FaultyUpdatedIsSubsetFoundMergedUpdated']

    df_data = df_data.replace({'Type': 'NN'}, 'NoResult')
    df_data = df_data.replace({'Type': 'FF'}, 'Different')
    df_data = df_data.replace({'Type': 'TF'}, 'Subset')
    df_data = df_data.replace({'Type': 'FT'}, 'Superset')
    df_data = df_data.replace({'Type': 'TT'}, 'Equal')

    df_data['Time'] = df_data['Time'] / 1000.0

    return df_data


def print_error(df, config):
    df = df[df['Type'].isin(['Different','Subset','Superset'])]
    df = df[df['Algorithm'].isin(config.algorithms)]

    df['AllLiteralsCount'] = df['CorrectlyFoundLiteralsCount'] + df['MissedLiteralsCount']
    df['Supersetsize'] = (df['IncorrectlyFoundLiteralsCount'] / df['AllLiteralsCount']) + 1
    df['Subsetsize'] = df['CorrectlyFoundLiteralsCount'] / df['AllLiteralsCount']
    df['Missed'] = df['MissedLiteralsCount'] / df['AllLiteralsCount']
    df['Incorrect'] = df['IncorrectlyFoundLiteralsCount'] / df['FoundLiteralsCount']

    df1 = df[df['Type'] == 'Different']
    df1 = df1[['Algorithm', 'Missed', 'Incorrect']]
    df1 = df1.groupby(['Algorithm']).agg(['mean'])

    df2 = df[df['Type'] == 'Subset']
    df2 = df2[['Algorithm', 'Subsetsize']]
    df2 = df2.groupby(['Algorithm']).agg(['mean'])

    df3 = df[df['Type'] == 'Superset']
    df3 = df3[['Algorithm', 'Supersetsize']]
    df3 = df3.groupby(['Algorithm']).agg(['mean'])

    df4 = df1
    df4 = df4.join(df2)
    df4 = df4.join(df3)

    create_table(df4, 'errors', config)


def investigateProblem(df_error, config):
    df_error1 = df_error.copy()
    df_error1 = df_error1[df_error1['Type'] != 'Equal']
    df_error1 = df_error1[df_error1['T'] == df_error1['InteractionSize']]
    df_error1 = df_error1[df_error1['Algorithm'].isin([single,incremental,oldIncremental,incremental2,incremental10])]
    df_error1 = df_error1[['Algorithm','ModelName', 'ModelIteration', 'InteractionSize', 'T', 'MissedLiteralsCount', 'IncorrectlyFoundLiteralsCount']]

    df_error2 = df_error
    df_error2 = df_error2[df_error2['Type'] != 'Equal']
    df_error2 = df_error2[df_error2['T'] > df_error2['InteractionSize']]
    df_error2 = df_error2[df_error2['Algorithm'].isin([incremental,oldIncremental,incremental2,incremental10])]
    df_error2 = df_error2[['Algorithm','ModelName', 'ModelIteration', 'InteractionSize', 'T', 'MissedLiteralsCount', 'IncorrectlyFoundLiteralsCount']]

    df_error3 = pd.concat([df_error1.copy(),df_error2.copy()])

    create_csv(df_error3, 'errors', config)


def plot_type(df, normalize, config):
    fig, axes = plt.subplots(nrows=1,
                         ncols=3,
                         sharex=True,
                         sharey=True)
    fig.tight_layout()
    plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0, hspace=0)

    df = df[df['Algorithm'].isin(config.algorithms)]

    dfs = [df[df['T'] < df['InteractionSize']], df[df['T'] == df['InteractionSize']], df[df['T'] > df['InteractionSize']]]
    title = ['t < int. size', 't = int. size', 't > int. size']

    for i in range(len(dfs)):
        ax = axes[i]
        df = dfs[i]

        if normalize:
            df = df[['Algorithm', 'Type']].value_counts(normalize=normalize)
            df = df * len(config.algorithms)
            df = df.to_frame()
        else:
            df = df[['Algorithm', 'Type']].value_counts().to_frame()

        df = df.unstack('Type').fillna(0)
        df.columns = df.columns.droplevel(0)
        df = df.reset_index()

        if 'Equal' not in df:
            df['Equal'] = 0
        if 'Subset' not in df:
            df['Subset'] = 0
        if 'Superset' not in df:
            df['Superset'] = 0
        if 'Different' not in df:
            df['Different'] = 0
        if 'NoResult' not in df:
            df['NoResult'] = 0

        df = df[['Algorithm','Equal','Superset','NoResult','Subset','Different']]

        df = df.set_index('Algorithm').loc[config.algorithms].reset_index()

        df.plot(x='Algorithm', kind='bar', stacked=True, ax=ax, width=0.85, color = config.colors)
        ax.set_xticklabels(config.algorithms, rotation=25, ha='right', rotation_mode='anchor')
        ax.locator_params(axis='y', nbins=11)
        ax.get_legend().remove()
        ax.set_title(title[i])
        ax.set_xlabel(None)
        ax.set_ylabel(None)
        ax.yaxis.grid()

    handles, labels = axes[0].get_legend_handles_labels()
    fig.legend(handles, labels, loc='lower right', bbox_to_anchor=(1, 0.1),ncol=2)
    create_plot('found_interactions', config, 1.7)


def print_resources(df, config):
    ts = df['T'].unique()

    df_filter = df
    df_filter = df_filter[df_filter['Algorithm'].isin(config.algorithms)]
    df_filter = df_filter[df_filter['Type'] == 'Equal']
    df_filter = df_filter[df_filter['T'] >= df_filter['InteractionSize']]
    df_filter = df_filter[df_filter['Time'] >= 0]

    df_filter = df_filter[['ModelID', 'ModelIteration', 'InteractionSize', 'Algorithm', 'T', 'Time', 'Configurations']]

    dfT = df_filter.set_index(['ModelID', 'ModelIteration', 'InteractionSize', 'T', 'Algorithm']).unstack().unstack().reset_index()
    for ti in range(len(ts)):
        t = ts[len(ts)-(ti+1)]
        for algorithm in config.algorithms:
            dfT['Time_Rel', algorithm, t] = dfT['Time', algorithm, t].div(dfT['Time', incremental, t])
            dfT['Configurations_Rel', algorithm, t] = dfT['Configurations', algorithm, t].div(dfT['Configurations', incremental, t])
    dfT = dfT.stack().stack().groupby(['Algorithm','T'])[['Time', 'Configurations', 'Time_Rel', 'Configurations_Rel']].agg(['mean','median','max'])

    dfAll = df_filter.set_index(['ModelID', 'ModelIteration', 'InteractionSize', 'T', 'Algorithm']).unstack().reset_index()
    for algorithm in config.algorithms:
        dfAll['Time_Rel', algorithm] = dfAll['Time', algorithm].div(dfAll['Time', incremental])
        dfAll['Configurations_Rel', algorithm] = dfAll['Configurations', algorithm].div(dfAll['Configurations', incremental])
    dfAll = dfAll.stack().groupby(['Algorithm'])[['Time', 'Configurations', 'Time_Rel', 'Configurations_Rel']].agg(['mean','median','max'])

    df = pd.concat([dfT,dfAll]).reset_index()

    create_csv(df, 'resources', config)


def plot_relative_time(df, config):
    fig, ax = plt.subplots(nrows=1,
                         ncols=1,
                         sharex=False,
                         sharey=False)
    fig.tight_layout()
    plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0, hspace=0.2)

    ts = df['T'].unique()

    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]

    df = df[['ModelID', 'ModelIteration', 'T', 'InteractionSize', 'Algorithm', 'Time']]

    df = df.set_index(['ModelID', 'ModelIteration', 'InteractionSize', 'T', 'Algorithm']).unstack().unstack().reset_index()
    df.columns = df.columns.droplevel(0)
    df.columns = df.columns.map('{0[0]}|{0[1]}'.format)

    column_names = []
    globalmax = None
    globalmin = None
    for algorithm in config.algorithms2:
        for ti in range(len(ts)):
            column_name = algorithm + '|' + str(ts[len(ts)-(ti+1)])
            inc_col_name = incremental + '|' + str(ts[len(ts)-(ti+1)])
            column_names.append(column_name)
            df[column_name] = df[column_name] / df[inc_col_name]
            localmax = max(df[column_name])
            if globalmax is None or globalmax < localmax:
                globalmax = localmax
            localmin = min(df[column_name])
            if globalmin is None or globalmin > localmin:
                globalmin = localmin

    df.boxplot(vert=False,ax=ax,positions=[0, 1, 2, 4, 5, 6],column=column_names,widths=0.8,boxprops=config.boxprops, flierprops=config.flierprops, medianprops=config.medianprops)

    ax.set_xscale('log', base=2)
    ticks = [2**i for i in range(math.floor(np.log2(globalmin)),math.ceil(np.log2(globalmax)),1)]
    ax.set_xticks(ticks)
    for label in ax.xaxis.get_ticklabels()[1::2]:
        label.set_visible(False)

    ax.yaxis.grid()
    ax.tick_params(axis='x', which='major', pad=12)
    ax.tick_params(axis='y', which='major', pad=20)
    ax.set_yticklabels([' 3',' 2',' 1',' 3',' 2',' 1'])

    xpos = 2**(math.floor(np.log2(globalmin)))
    if (len(config.algorithms2)):
        ax.text(xpos, 1, config.algorithms2[0], va='center', rotation='vertical', fontsize=34)
        ax.text(xpos, 5, config.algorithms2[1], va='center', rotation='vertical', fontsize=34)
    else:
        for i in range(len(config.algorithms2)):
            ax.text(xpos, i, config.algorithms2[i], va='center', rotation='horizontal', fontsize=34)
    ax.axvline(x = 1, linestyle = '--', linewidth=3)

    ax.set_xlabel("Time relative to "+incremental)
    ax.set_ylabel("Algorithm, t =")

    create_plot('relative_time', config, 3)


def plot_relative_configurations(df, config):
    fig, ax = plt.subplots(nrows=1,
                         ncols=1,
                         sharex=False,
                         sharey=False)
    fig.tight_layout()
    plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0, hspace=0.2)

    ts = df['T'].unique()

    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]

    df = df[['ModelID', 'ModelIteration', 'T', 'InteractionSize', 'Algorithm', 'Configurations']]

    df = df.set_index(['ModelID', 'ModelIteration', 'InteractionSize', 'T', 'Algorithm']).unstack().unstack().reset_index()
    df.columns = df.columns.droplevel(0)
    df.columns = df.columns.map('{0[0]}|{0[1]}'.format)

    column_names = []
    globalmax = None
    globalmin = None
    for algorithm in config.algorithms2:
        for ti in range(len(ts)):
            column_name = algorithm + '|' + str(ts[len(ts)-(ti+1)])
            inc_col_name = incremental + '|' + str(ts[len(ts)-(ti+1)])
            column_names.append(column_name)
            df[column_name] = df[column_name] / df[inc_col_name]
            localmax = max(df[column_name])
            if globalmax is None or globalmax < localmax:
                globalmax = localmax
            localmin = min(df[column_name])
            if globalmin is None or globalmin > localmin:
                globalmin = localmin

    df.boxplot(vert=False,ax=ax,positions=[0, 1, 2, 4, 5, 6],column=column_names,widths=0.8,boxprops=config.boxprops, flierprops=config.flierprops, medianprops=config.medianprops)

    ax.set_xscale('log', base=2)
    ticks = [2**i for i in range(math.floor(np.log2(globalmin)),math.ceil(np.log2(globalmax)),1)]
    ax.set_xticks(ticks)

    ax.yaxis.grid()
    ax.tick_params(axis='x', which='major', pad=12)
    ax.tick_params(axis='y', which='major', pad=20)
    ax.set_yticklabels([' 3',' 2',' 1',' 3',' 2',' 1'])

    xpos = 2**(math.floor(np.log2(globalmin)))
    if (len(config.algorithms2)):
        ax.text(xpos, 1, config.algorithms2[0], va='center', rotation='vertical', fontsize=34)
        ax.text(xpos, 5, config.algorithms2[1], va='center', rotation='vertical', fontsize=34)
    else:
        for i in range(len(config.algorithms2)):
            ax.text(xpos, i, config.algorithms2[i], va='center', rotation='horizontal', fontsize=34)

    ax.axvline(x = 1, linestyle = '--', linewidth=3)

    ax.set_xlabel("Tested configurations relative to "+incremental)
    ax.set_ylabel("Algorithm, t =")

    create_plot('relative_configurations', config, 3)


def plot_absolute_time(df, config):
    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]
    df = df[df['T'] == 3]

    for algo in config.algorithms:
        df_plot = df[df['Algorithm'] == algo]

        dfg = df_plot.groupby('ModelID')

        fig, ax = plt.subplots(nrows=1,
                            ncols=1,
                            sharex=False,
                            sharey=False)
        fig.tight_layout()
        plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0.13, hspace=0)

        dfg.boxplot(column='Time',ax=ax,subplots=False,widths=0.8,grid=True,boxprops=config.boxprops, flierprops=config.flierprops, medianprops=config.medianprops)
        ax.set_yscale('log', base=2)
        ax.set_yticks([2**i for i in range(math.floor(np.log2(min(df_plot['Time']))),math.ceil(np.log2(max(df_plot['Time']))),1)])
        for label in ax.yaxis.get_ticklabels()[1::2]:
            label.set_visible(False)
        ax.set_xlabel('Feature models (sorted by number of features)')
        ax.set_ylabel('Time (s)')
        ax.set_xticklabels([], rotation=0)

        create_plot('absolute_time_'+algo+'_t3', config, 3)


def plot_absolute_configurations(df, config):
    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]
    df = df[df['T'] == 3]

    for algo in config.algorithms:
        df_plot = df[df['Algorithm'] == algo]

        dfg = df_plot.groupby('ModelID')

        fig, ax = plt.subplots(nrows=1,
                            ncols=1,
                            sharex=False,
                            sharey=False)
        fig.tight_layout()
        plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0.13, hspace=0)

        dfg.boxplot(column='Configurations',ax=ax,subplots=False,widths=0.8,grid=True,boxprops=config.boxprops, flierprops=config.flierprops, medianprops=config.medianprops)
        ax.set_yscale('log', base=2)
        ax.set_yticks([2**i for i in range(math.floor(np.log2(min(df['Configurations']))),math.ceil(np.log2(max(df['Configurations']))),1)])
        ax.set_xlabel('Feature models (sorted by number of features)')
        ax.set_ylabel('Configurations')
        ax.set_xticklabels([], rotation=0)

        create_plot('absolute_configurations_'+algo+'_t3', config, 3)


def plot_absolute_diff(df, config):
    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]
    df = df[df['T'] == 3]

    res = ['Time','Configurations']
    key = ['ModelID','ModelIteration','InteractionSize','T']

    for r in res:
        for algo1 in config.algorithms:
            df_algo1 = df[df['Algorithm'] == algo1]
            for algo2 in config.algorithms:
                if algo1 != algo2:
                    df_algo2 = df[df['Algorithm'] == algo2].copy()

                    df_algo2 = df_algo2.join(df_algo1.set_index(key), on=key, rsuffix="_2")
                    df_algo2['diff'] = df_algo2[r] - df_algo2[r + '_2']

                    dfg = df_algo2.groupby('ModelID')

                    fig, ax = plt.subplots(nrows=1,
                                        ncols=1,
                                        sharex=False,
                                        sharey=False)
                    fig.tight_layout()
                    plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0.13, hspace=0)

                    dfg.boxplot(column='diff',ax=ax,subplots=False,widths=0.8,grid=True,boxprops=config.boxprops, flierprops=config.flierprops, medianprops=config.medianprops)
                    ax.set_title(r)
                    ax.set_xlabel(None)
                    ax.set_ylabel(None)
                    ax.set_xticklabels([], rotation=0)

                    create_plot('absolute_diff_'+r+'_'+algo2+'_'+algo1+'_t3', config, 3)


def plot_scatter_absolute_configurations(df, config, filter_outliers):
    fig, ax = plt.subplots(nrows=1,
                         ncols=1,
                         sharex=False,
                         sharey=False)
    fig.tight_layout()
    plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0.13, hspace=0)

    df = df[df['T'] >= df['InteractionSize']]
    df = df[df['Time'] >= 0]

    ts = df['T'].unique()

    colors = [config.colors[4],config.colors[3],config.colors[0],config.colors[1],config.colors[2]]

    for algo in config.algorithms:
        df_plot = df[df['Algorithm'] == algo]

        fig, ax = plt.subplots(nrows=1,
                            ncols=1,
                            sharex=False,
                            sharey=False)
        fig.tight_layout()
        plt.subplots_adjust(left=0.09, bottom=0.095, right=1, top=1, wspace=0.13, hspace=0)


        dfg = df_plot.groupby(['#Variables','ModelName','T'])['Configurations'].median().unstack().reset_index()
        dfg['max'] = dfg[ts].max(axis=1)
        #print(dfg)

        if filter_outliers >= 0:
            print(dfg[dfg['max'] >= filter_outliers])
            dfg = dfg[dfg['max'] < filter_outliers]
            maxy = filter_outliers
        else:
            maxv = max(dfg['max'])
            maxvfloor = 10 ** math.floor(np.log10(maxv))
            maxy = math.ceil(maxv / maxvfloor) * maxvfloor

        for i in range(len(ts)):

            dfg.plot.scatter(x='#Variables', y=ts[i], color=colors[i], s=60, ax=ax, label='t = ' + str(i+1))

        ax.set_xscale('log', base=2)
        miny = 0
        ax.set_yticks([i for i in range(miny,maxy,40)])
        ax.set_xticks([2**i for i in range(math.floor(np.log2(min(df_plot['#Variables']))),math.ceil(np.log2(max(df_plot['#Variables']))),1)])
        for label in ax.xaxis.get_ticklabels()[1::2]:
            label.set_visible(False)
        ax.tick_params(axis='x', which='major', pad=12)
        ax.tick_params(axis='y', which='major', pad=6)
        ax.yaxis.grid()
        ax.set_xlabel('Number of features')
        ax.set_ylabel('Configurations')
        ax.legend(loc='upper left',markerscale=3)

        create_plot('scatter_configurations_' + algo, config, 3)


if __name__ == "__main__":
    random = 'Random'
    single = 'iident'
    incremental = 'Inciident'
    incremental2 = 'Inciident2'
    incremental10 = 'Inciident10'
    oldIncremental = 'OldIncremental'
    main(sys.argv)
