# Evaluation replication package for "Incremental Identification of T-Wise Feature Interactions" (Inciident)

## Structure
This replication package consists of two parts:
 - **Data**: The evaluation data reported in the paper and the script for creating corresponding plots.
 - **Executable**: The executable JAR with the evaluation setup and used feature models

### Data
- All raw data measured during the evaluation.
- The data is split over multiple CSV files.
- To run the script for creating plots run:
  - `python plot_data.py`

### Executable
- Jar built from source, as described above, and evaluation setup, including configuration files and feature models
- The evaluation for different feature models uses different configuration files
- To execute the evaluation run:
  - `java -da -Xmx8g -jar inciident-eval-1.0.0-all.jar interaction-finder config_01`
  - `java -da -Xmx8g -jar inciident-eval-1.0.0-all.jar interaction-finder config_02`
  - `java -da -Xmx8g -jar inciident-eval-1.0.0-all.jar interaction-finder config_03`
