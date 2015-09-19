TSE-FLEX-Converter
==================

# **Under construction...**

TSE-FLEX-Converter converts Tokyo Stock Exchnage FLEX Historical message data to kdb+ or csv file.

##  What is Tokyo Stock Exchnage FLEX Historical data?
See Tokyo Stock Exchnage page...

- English - http://www.jpx.co.jp/english/markets/paid-info-equities/historical/01.html
- Japanese - http://www.jpx.co.jp/markets/paid-info-equities/historical/01.html

## How to Use


## Step 1. Contract the service and downlod zip files.

## Step 2. Convert message zip files to line separated files.

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
[Under construction...]

