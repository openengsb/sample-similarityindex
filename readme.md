# Similarity Index

## Background
The similarity index uses a Lucene index that can be configured with the methods addDocument(EDBObject) and buildQueryString(EDBObject). 

## Configuration
The default implementation indexes all fields (key - value pairs) and uses the levenstein distance for all key value pairs.

## How to write a custom index
* create a new class that extends AbstractIndex
* overwrite the methods addDocument(EDBObject) and buildQueryString(EDBObject) with your custom structure
* test the new structure

There is a sample project https://github.com/chhochreiner/EDBHook that uses the similarity index with two custom indices that use the EDBHook infrastructure.