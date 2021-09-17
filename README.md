# WikimediaDumpExtractor

- Extracts pages of a category from Wikimedia/Wikipedia database backup dumps.

- [Download the latest release](https://github.com/EML4U/WikimediaDumpExtractor/releases)


### How to extract texts

```Shell
java -jar WikimediaDumpExtractor.jar pages <input XML file> <output directory> <category> [number of threads, default 3]
```

Files in the output directory will be overwritten.  
An additional index-file with file-names and page-titles will be created above the output directory.


##### Example: Simple run

```Shell
java -jar WikimediaDumpExtractor.jar pages enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people"
```


##### Example: Process large files

You can use multiple threads.
To determine the number of kernels, use e.g. the `nproc` command.
Additionally, set additional parameters like shown below ([source](https://stackoverflow.com/a/50982118)).

```Shell
java -DentityExpansionLimit=2147480000 -DtotalEntitySizeLimit=2147480000 -Djdk.xml.totalEntitySizeLimit=2147480000 -jar WikimediaDumpExtractor.jar pages enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people" 3
```

An extraction of 1 million pages from a 75G file using 3 threads takes 20 minutes.
The dump file of 2021-09-17 is 85G (bz2 archive 20G, extract with `bzip2 -dk filename.bz2` to keep the archive file).

Example data is given in [Anarchism.xml](examples/Anarchism.xml). The first 462 lines of the Wikipedia dump 20210901 have been extracted and `</mediawiki>` was added as last line.
After running WikimediaDumpExtractor 1.1.0 and the category "Anarchism", the file [Anarchism.txt](examples/Anarchism.txt) was extracted.


### How to extract a category overview
 
The execution takes around 1 second.  
Input files are named like enwiki-YYYYMMDD-category.sql in the file dumps.


##### Examples
 
 ```Shell
java -jar WikimediaDumpExtractor.jar categories <input SQL file> [minimum category size, default 10000]
```

```Shell
java -jar WikimediaDumpExtractor.jar categories enwiki-category.sql > categories.csv
```


##### Example result format

```
Number of Pages, "Wikipedia category title"
3652850,"Articles_with_short_description"
1016010,"Living_people"
```


### How to move equal text files of two points of time

Take a look into branch [equal-files](https://github.com/EML4U/WikimediaDumpExtractor/tree/equal-files).


## How to get data

Get Wikimedia dumps here:

- [Current dumps of the Wikipedia (english)](https://dumps.wikimedia.org/enwiki/)
- [Archived dumps of the Wikipedia](https://dumps.wikimedia.org/archive/)
- [Wikipedia dumps at archive.org](https://archive.org/search.php?query=subject%3A%22enwiki%22+AND+subject%3A%22data+dumps%22+AND+collection%3A%22wikimediadownloads%22&sort=-publicdate) and [old dumps](https://archive.org/details/wikipediadumps)

The available data is described here:

- [Overview of Wikimedia downloads](https://dumps.wikimedia.org/)
- [About Wikimedia dumps](https://meta.wikimedia.org/wiki/Data_dumps)
- [Wikipedia database download](https://en.wikipedia.org/wiki/Wikipedia:Database_download)

To generate small test files, you may use the following command and edit the footer of the generated file afterwards to ensure valid XML:

`head -n10000 enwiki-pages-articles-multistream.xml > test.xml`

To count the number of generated files, you may use the following command:

`ls ./ | wc -l`


## How to build a Java jar file

Build the project using Maven and the goal _package_.
For releases go into the _target_ directory and rename
_WikimediaDumpExtractor-x.y.z-jar-with-dependencies.jar_
to
_WikimediaDumpExtractor-x.y.z.jar_.


## Changelog

- 1.1.0 Index of file names; extraction of SQL categories
- 1.0.0 Extraction of pages


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Education and Research (BMBF) within the project [EML4U](https://dice-research.org/EML4U) under the grant no 01IS19080B.