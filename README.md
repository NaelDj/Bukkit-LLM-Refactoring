Bukkit
======

## Thesis Replication Notes

This repository is a thesis replication repository based on Bukkit. The original Bukkit README is kept below.

The full replication package is available here: https://github.com/NaelDj/Mutation-LLM-Refactoring

The `base` branch contains the Bukkit version used as the starting point for the thesis runs. Compared to the original project, this branch includes replication-related setup changes:

- the `pitest-maven` plugin was added to `pom.xml`, using PIT version 1.21.1;
- PIT was configured to generate XML, HTML, and CSV reports;
- the Maven Toolchains plugin was added to select JDK 1.8 during the Maven build;
- a `tools` folder was added with scripts for running tests and PIT mutation testing;
- baseline PIT reports were included under `pit/pit-reports-before`.

The branches `run_7` to `run_12` correspond to the six Bukkit runs analysed in the thesis. Each run branch contains the production-code and test-code changes produced during that run.

The project remains licensed under the GNU General Public License version 3.0, as stated in the original Bukkit license file.

A Minecraft Server API.

Website: [http://bukkit.org](http://bukkit.org)  
Bugs/Suggestions: [http://leaky.bukkit.org](http://leaky.bukkit.org)  
Contributing Guidelines: [CONTRIBUTING.md](https://github.com/Bukkit/Bukkit/blob/master/CONTRIBUTING.md)

Compilation
-----------

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`
