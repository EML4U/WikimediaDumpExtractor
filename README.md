# WikimediaDumpExtractor

- WikimediaDumpExtractor extracts pages from Wikimedia/Wikipedia database backup dumps.
- [Download the latest release](https://github.com/EML4U/WikimediaDumpExtractor/releases)


### Usage

```Shell
Usage: java -jar WikimediaDumpExtractor.jar
 pages      <input XML file> <output directory> <categories> <search terms>
 categories <input SQL file> [minimum category size, default 10000]
The values <categories> and <search terms> can contain multiple entries separated by '|'
Website: https://github.com/EML4U/WikimediaDumpExtractor
```

### Example

Download the [example XML file](src/test/resources/enwiki-20080103-pages-articles-example.xml). 
It contains 4 pages extracted from the enwiki 20080103 dump.
Then run the following command:

```Shell
java -jar WikimediaDumpExtractor.jar pages enwiki-20080103-pages-articles-example.xml ./ "Social philosophy" altruism
```

Afterwards, files similar to [example result](src/test/resources/enwiki-20080103-pages-articles-example/) will be created.


### Process large files

To process large XML files (e.g. enwiki 20080103 has 15 GB, enwiki 20210901 has 85 GB), set the following 3 parameters:

```Shell
java -DentityExpansionLimit=0 -DtotalEntitySizeLimit=0 -Djdk.xml.totalEntitySizeLimit=0 -jar WikimediaDumpExtractor.jar ...
```


## How to get data

Get Wikimedia dumps here:

- dumps.wikimedia.org
    - [Current dumps of the Wikipedia (english)](https://dumps.wikimedia.org/enwiki/) (now)
    - [Archived dumps of the Wikipedia](https://dumps.wikimedia.org/archive/) (2001 – 2010)
- archive.org
    - [Collection wikimediadownloads + enwiki + data dumps](https://archive.org/search.php?query=subject%3A%22enwiki%22+AND+subject%3A%22data+dumps%22+AND+collection%3A%22wikimediadownloads%22&sort=-publicdate) (2012 – now)
    - [Collection wikipediadumps](https://archive.org/details/wikipediadumps) (2010 – 2011)
- Additional information
    - [Overview of Wikimedia downloads](https://dumps.wikimedia.org/)
    - [About Wikimedia dumps](https://meta.wikimedia.org/wiki/Data_dumps)
    - [Wikipedia database download](https://en.wikipedia.org/wiki/Wikipedia:Database_download)
- Note: Dump files can be extracted with `bzip2 -dk filename.bz2` to keep archive files.


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Education and Research (BMBF) within the project [EML4U](https://dice-research.org/EML4U) under the grant no 01IS19080B.