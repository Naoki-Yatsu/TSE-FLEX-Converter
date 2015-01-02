#!/bin/bash
# ------------------------------------------------------------------
#
# Function : start ATS application
#
# arguments:
#
# Author   :
#
# ------------------------------------------------------------------
VERSION=1.0.0
SUBJECT=start_tfc
USAGE="Usage: command -hv args"

MYNAME=`basename $0`
MYDIR=`dirname $0`

# set env
. ${MYDIR}/set_env.sh

# common func
. ${MYDIR}/common_func.sh

# error exit
set -e

# --- Option processing --------------------------------------------

options="$*"


# --- Locks -------------------------------------------------------
LOCK_FILE="${MYDIR}/${SUBJECT}.lock"

if [ -f "$LOCK_FILE" ]; then
    echo "Script is already running"
    exit 1
fi

trap "rm -f $LOCK_FILE" EXIT
touch $LOCK_FILE

# -- Start ---------------------------------------------------------
echo; show_info "START"

# -- Body ---------------------------------------------------------

# start app
echo; show_info "start TFC."
java -Xms1024M -Xmx4096M -cp "${MYDIR}/lib/*" -jar TSE-FLEX-Converter-0.9.0-SNAPSHOT.jar ${options}

# -----------------------------------------------------------------

# -- End ---------------------------------------------------------------
show_info "END"
echo
