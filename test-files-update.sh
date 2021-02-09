rm -rf test
mkdir test

mkdir test/a
mkdir test/b
mkdir test/c

touch test/a/a
touch test/a/ab
touch test/a/ac
touch test/a/abc
#touch test/a/abcabc

touch test/b/b
#touch test/b/bb
touch test/b/ab
touch test/b/bc
touch test/b/abc
#touch test/b/abcabc

touch test/c/c
#touch test/c/cc
#touch test/c/ccc
touch test/c/ac
touch test/c/bc
touch test/c/abc
#touch test/c/abcabc
