TSE-FLEX-Converter
==================

TSE-FLEX-Converter converts Tokyo Stock Exchnage FLEX Historical message data to kdb+ or csv file.

##  What is Tokyo Stock Exchnage FLEX Historical data?
See Tokyo Stock Exchnage page...

- English - http://www.jpx.co.jp/english/markets/paid-info-equities/historical/01.html
- Japanese - http://www.jpx.co.jp/markets/paid-info-equities/historical/01.html

## How to Use


### Step 1. Contract the service and downlod zip files.

### Step 2. Convert message zip files to line separated files.

Since FullEquities file is one line big file, split lines using bash shell at first.  
Prepare FullEquities_yyyymmdd.zip files in from dir.

```bash  
edit_flex.sh [from dir] [to dir]
```  

Separeted line files are created in to dir.

### Step 3. Prepare kdb+ port. (For kdb mode)

Start kdb port with func_flex.q file.  
Set histporical path to "dbdir: xxxx" in func_flex.q.  

```
q func_flex.q -p 5001
```  

### Step 4. Execute Java application.

#### Step 4.1  Build application

Build jar with maven. pom.xml create all in one jar package.
Use TSE-FLEX-Converter-xxx-jar-with-dependencies.jar for execute.

```
$ mvn package
...
$ ls -l target
---------- 1 Naoki mkpasswd 4731449 Sep 19 19:03 TSE-FLEX-Converter-0.9.0-SNAPSHOT-jar-with-dependencies.jar
---------- 1 Naoki mkpasswd   94189 Sep 19 19:03 TSE-FLEX-Converter-0.9.0-SNAPSHOT.jar
...
```

#### Step 4.2  Change config

"config.properties" is configuration file.  
Change each parameters to fit your needs.
For csv output mode, change "output.dao.class" to CsvDao.

#### Step 4.3  Run application

Start application with -file or -dir option.

```
$ java -cp "TSE-FLEX-Converter-xxx.jar;config.properties" ny2.flex.app.Launch -h
19:17:55.849 [main] INFO  ny2.flex.app.Launch - START Application.
Usage:
 Launch -f file
 Launch -d dir
 Launch -h

Options:
 -d (--dir) VAL  : directory path for loading all files
 -f (--file) VAL : file path
 -h (--help)     : show usage message and exit
```




