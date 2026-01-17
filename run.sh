#!/bin/bash

echo "Compilation..."
mkdir -p bin

# Compiler tous les fichiers Java
find src -name "*.java" > sources.txt
javac -d bin -sourcepath src @sources.txt

if [ $? -eq 0 ]; then
    echo "Lancement de Power Grid Tycoon..."
    java -cp bin Main
else
    echo "Erreur de compilation."
    exit 1
fi

rm -f sources.txt
