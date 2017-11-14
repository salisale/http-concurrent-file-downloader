## HTTP Resumable Concurrent File Downloader

A speedy, resumable, multi-connection file downloader for any RFC-conforming site

In command-line, type
```
xxx -o <output file> [-c [<numConn>]] http://someurl.domain[:port]/path/to/file
```
where ```-c``` specifies the option for concurrent download and
```<numConn>``` specifies the number of threads for the download;
if the number of connection is not provided, the default value will be 5
