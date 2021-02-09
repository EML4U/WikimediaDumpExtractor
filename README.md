# Move Equal Filenames

Moves files, if their names are contained in every directory.

[Download the latest release](https://github.com/EML4U/WikimediaDumpExtractor/releases)


## How to run

```Shell
Usage: java -jar MoveEqualFilenames.jar <input directories> <output directory>
       <input directories> must be separated by |
```

### Example

Given directories and files:

```
a/a
a/ab
a/ac
a/abc
b/b
b/ab
b/bc
b/abc
c/c
c/ac
c/bc
c/abc
```

Execution:

```
java -jar MoveEqualFilenames.jar "a|b|c" move

Input directories: [a, b, c]
1 move/a <- a
1 move/b <- b
1 move/c <- c
Output directory:  move
Seconds:           0.002
```

Resulting directories and files:

```
a/a
a/ab
a/ac
b/b
b/ab
b/bc
c/c
c/ac
c/bc
move/a/abc
move/b/abc
move/c/abc
```


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Education and Research (BMBF) within the project [EML4U](https://dice-research.org/EML4U) under the grant no 01IS19080B.