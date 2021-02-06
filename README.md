## How to run

You have to provide some arguments:
`<input XML file> <output directory> <category> <optional number of threads>`

1. An input Wikimedia dump XML file
2. An output directory, files will be overwritten
3. A category to exctract
4. (Optional) Numbers of threads for extraction

### Simple run

```Shell
java -jar WikimediaDumpExtractor-1.0.0.jar enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people"
```

### Process large files

You can use multiple threads.
To determine the number of kernels, use e.g. the `nproc` command.
Additionally, set additional parameters like shown below ([source](https://stackoverflow.com/a/50982118)).

```Shell
java -DentityExpansionLimit=2147480000 -DtotalEntitySizeLimit=2147480000 -Djdk.xml.totalEntitySizeLimit=2147480000 -jar WikimediaDumpExtractor-1.0.0.jar enwiki-pages-articles-multistream.xml /tmp/living-people/ "Living people" 3
```

An extraction of 1 million pages from a 75G file using 3 threads takes 20 minutes.

## How to build

Build the project using Maven and the goal _package_.
For releases go into the _target_ directory and rename
_WikimediaDumpExtractor-x.y.z-jar-with-dependencies.jar_
to
_WikimediaDumpExtractor-x.y.z.jar_.


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Education and Research (BMBF) within the project [EML4U](https://dice-research.org/EML4U) under the grant no 01IS19080B.
