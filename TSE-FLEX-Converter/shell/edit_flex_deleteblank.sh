#!/bin/bash
# ------------------------------------------------------------------
#
# Function : Edit FLEX Market Data
#
# arguments:
#
#
# Author   :
#
#
# ------------------------------------------------------------------
VERSION=0.1.0
SUBJECT=
USAGE="Usage: command -hv args"

MYNAME=`basename $0`
MYDIR=`dirname $0`

# common func
. ${MYDIR}/common_func.sh
# error exit
set -eu

# --- Option processing --------------------------------------------

if [ $# -ne 2 ]; then
    show_error 'Usage: $1 from_dir' 1>&2
    show_error '       $2 to_dir'   1>&2
    exit 1
fi

FROM=$1
TO=$2

# -- Variable setting ----------------------------------------------
BASE_DIR=`pwd ${MYDIR}`
FROM_DIR=${BASE_DIR}/${FROM}
TO_DIR=${BASE_DIR}/${TO}

# check from_dir is exist
if [ ! -e ${FROM_DIR} ]; then
    show_error "From Dir ${FROM_DIR} is not exist."
    exit 1
fi

WORK_DIR=${BASE_DIR}/work

# -- Start ---------------------------------------------------------
echo; show_info "START"

# -- Body ----------------------------------------------------------

# STEP1
show_info "STEP1 : setup"

# create dir if not exist
mkdir -p ${TO_DIR}
mkdir -p ${WORK_DIR}

# from file list
zipfiles=`find ${FROM_DIR} -type f`
show_info "target files = ${zipfiles}"

# STEP2
show_info "STEP2 : unzip & edit files"

# loop for zipfiles
zipfiles=`find ${FROM_DIR} -type f`
for zipfile in ${zipfiles}
do
    show_info "STEP2 : target = ${zipfile}"
    # unzip
    unzip ${zipfile} -d ${WORK_DIR}

    # get unzipped filename
    ## Since split command output current dir only, move workdir.
    cd ${WORK_DIR}
    orgfile=`find . -type f -exec basename {} \;`
    editfile_temp="${orgfile}.temp"
    editfile_last="${orgfile}.line"

    # split file by 1GB
    show_info "STEP2 : split & replace"
    prefix="${orgfile}_split_"
    split -b 1000m ${orgfile} ${prefix}

    # replace
    splitfiles=`find . -type f | grep split | sort`
    for splitfile in ${splitfiles}
    do
        # replace 0x17(021) -> LF
        #cat ${splitfile} | perl -pe "s/\021+/\012/g" >> ${editfile}
        # replace 0x17(021) -> LF, 0x18/0x19(022/023) -> Tab(011)
        cat ${splitfile} | perl -pe "s/\021+/\012/g; s/[\022\023]/\011/g" >> ${editfile_temp}
    done

    # delete blank line
    grep -v '^\s*$' ${editfile_temp} > ${editfile_last}

    # move lf file
    show_info "STEP2 : move editfile = ${editfile_last}"
    mv ${editfile_last} ${TO_DIR}
    cd ${BASE_DIR}

    # delet temp files
    rm -f ${WORK_DIR}/*
done

# STEP3
show_info "STEP3 : delete work dir"
rmdir ${WORK_DIR}

# -- End ---------------------------------------------------------------
show_info "END"
echo
