# WikimediaDumpExtractor

Extracts pages of a category from Wikimedia/Wikipedia database backup dumps.

[Download the latest release](https://github.com/EML4U/WikimediaDumpExtractor/releases)


## How to run


### Extract pages

```Shell
java -jar WikimediaDumpExtractor.jar pages <input XML file> <output directory> <category> [number of threads, default 3]
```

Files in the output directory will be overwritten.  
An additional index-file with file-names and page-titles will be created above the output directory.


#### Example: Simple run

```Shell
java -jar WikimediaDumpExtractor.jar pages enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people"
```


#### Example: Process large files

You can use multiple threads.
To determine the number of kernels, use e.g. the `nproc` command.
Additionally, set additional parameters like shown below ([source](https://stackoverflow.com/a/50982118)).

```Shell
java -DentityExpansionLimit=2147480000 -DtotalEntitySizeLimit=2147480000 -Djdk.xml.totalEntitySizeLimit=2147480000 -jar WikimediaDumpExtractor.jar pages enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people" 3
```

An extraction of 1 million pages from a 75G file using 3 threads takes 20 minutes.


### Extract categories
 
 ```Shell
java -jar WikimediaDumpExtractor.jar categories <input SQL file> [minimum category size, default 10000]
```
 
Input files are named like enwiki-YYYYMMDD-category.sql in the file dumps.


#### Example: Create CSV file

```Shell
java -jar WikimediaDumpExtractor.jar categories enwiki-category.sql > categories.csv
```


## How to build

Build the project using Maven and the goal _package_.
For releases go into the _target_ directory and rename
_WikimediaDumpExtractor-x.y.z-jar-with-dependencies.jar_
to
_WikimediaDumpExtractor-x.y.z.jar_.


## Notes

Get Wikimedia dumps here:

- [Current dumps of the Wikipedia (english)](https://dumps.wikimedia.org/enwiki/)
- [Archived dumps of the Wikipedia](https://dumps.wikimedia.org/archive/)
- [Wikipedia dumps at archive.org](https://archive.org/details/wikipediadumps)

The available data is described here:

- [Overview of Wikimedia downloads](https://dumps.wikimedia.org/)
- [About Wikimedia dumps](https://meta.wikimedia.org/wiki/Data_dumps)

To generate small test files, you may use the following command and edit the footer of the generated file afterwards to ensure valid XML:

`head -n10000 enwiki-pages-articles-multistream.xml > test.xml`

To count the number of generated files, you may use the following command:

`ls ./ | wc -l`


## Changelog

- 1.1.0 Index of file names; extraction of SQL categories
- 1.0.0 Extraction of pages


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Education and Research (BMBF) within the project [EML4U](https://dice-research.org/EML4U) under the grant no 01IS19080B.