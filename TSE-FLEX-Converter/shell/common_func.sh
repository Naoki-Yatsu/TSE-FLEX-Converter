#!/bin/bash
# ------------------------------------------------------------------
#  Common functions
# ------------------------------------------------------------------

#
# Function : echo with color
#
# Example  : cecho $red "hello"
#
readonly RED=31
readonly GREEN=32
readonly YELLOW=33
readonly BLUE=34

function cecho {
    color=$1
    shift
    echo -e "\033[${color}m$@\033[m"
}


#
# Function : echo with color
#
# Example  : cecho $red "hello"
#
function show_date {
    message=$@
    echo `date +"%Y-%m-%d %H:%M:%S"`" : ${message}"
}


#
# Function : echo with color
#
# Example  : cecho $red "hello"
#
function show_error {
    message=$@
    cecho "${RED}" `date +"%Y-%m-%d %H:%M:%S"`" : ERROR : ${message}"
}

#
# Function : echo with color
#
# Example  : cecho $red "hello"
#
function show_warn {
    message=$@
    cecho "${YELLOW}" `date +"%Y-%m-%d %H:%M:%S"`" : WARN  : ${message}"
}


#
# Function : echo with color
#
# Example  : cecho $red "hello"
#
function show_info {
    message=$@
    echo `date +"%Y-%m-%d %H:%M:%S"`" : INFO  : ${message}"
}




