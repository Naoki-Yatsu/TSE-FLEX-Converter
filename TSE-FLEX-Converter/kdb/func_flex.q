// Data Writing Functions for TSE FLEX
//

// Execute.
//   writeAllTables[2014.12.15]
//   finish[];

//
//-- CONFIG -------------
//

// table
MarketDepth: ([]time:`timespan$();sym:`$();bidPrice:`float$();askPrice:`float$();bidQuantity:`long$();askQuantity:`long$();bidNumOrder:`long$();askNumOrder:`long$();bidPrices:();askPrices:();bidQuantities:();askQuantities:();bidNumOrders:();askNumOrders:();updateType:`$();updateNo:`int$();serialNo:`long$());
MarketBest: ([]time:`timespan$();sym:`$();bidPrice:`float$();askPrice:`float$();bidQuantity:`long$();askQuantity:`long$();updateType:`$();updateNo:`int$();serialNo:`long$());
MarketTrade: ([]time:`timespan$();sym:`$();side:`$();price:`float$();quantity:`long$();totalQuantity:`long$();totalTurnover:`long$();updateNo:`int$();serialNo:`long$());
CurrentPrice: ([]time:`timespan$();sym:`$();price:`float$();state:`$();updateNo:`int$();serialNo:`long$());
IssueInformation: ([]sym:`$();exchangeCode:`int$();classificationCode:`$());

// database to write to
dbdir: `:/data/kdb/work/flex;

// sortcols of all tables
sortcols: `sym`serialNo;

// write function
writeAllTables: {[date]
    writeAndClear[date;] each tables[];

    /writeAndClear[date; "MarketDepth"];
    /writeAndClear[date; "MarketBest"];
    /writeAndClear[date; "MarketTrade"];
    /writeAndClear[date; "CurrentPrice"];
  };

//
//-- END OF CONFIG ------
//

// maintain a dictionary of the db partitions which have been written to by the loader
partitions: ()!();

// function to print log info
out: {-1(string .z.z)," ",x};

// write data as splayed table
writedata: {[data; date; tablename]
    // generate the write path
    writepath:.Q.par[dbdir;date;`$(tablename,"/")];
    out"Writing ",(string count data)," rows to ",string writepath;

    // splay the table - use an error trap
    .[upsert;(writepath;data);{out"ERROR - failed to save table: ",x}];

    // make sure the written path is in the partition dictionary
    partitions[writepath]:date;
  };

// write data and clear table
writeAndClear:{[date; tablename]
    // enumerate the table - best to do this once
    out "Enumerating ",tablename;
    writedata[;date;tablename] .Q.en[dbdir;] (value tablename);

    // clear table
    delete from `$tablename;

    .Q.gc[];
  };

// set an attribute on a specified column
// return success status
setattribute:{[partition;attrcol;attribute] .[{@[x;y;z];1b};(partition;attrcol;attribute);0b]};

// set the partition attribute (sort the table if required)
sortandsetp:{[partition;sortcols]
    out "Sorting and setting `p# attribute in partition ",string partition;

    // the attribute should be set on the first of the sort cols
    parted:setattribute[partition;first sortcols;`p#];

    // if it fails, resort the table and set the attribute
    if[not parted;
        out "Sorting table";
        sorted:.[{x xasc y;1b};(sortcols;partition);{out"ERROR - failed to sort table: ",x; 0b}];
        // check if the table has been sorted
        if[sorted;
            // try to set the attribute again after the sort
            parted:setattribute[partition;first sortcols;`p#]]];

    // print the status when done
    $[parted; out"`p# attribute set successfully"; out"ERROR - failed to set attribute"];

    .Q.gc[];
  };

finish:{[]
    // re-sort and set attributes on each partition
    sortandsetp[;sortcols] each key partitions;
  };
