#!/bin/bash
# ------------------------------------------------------------------
#  Set Environment
# ------------------------------------------------------------------

# App
APPBASE=/opt/app/
LOGBASE=/var/opt/app


# ATS
ATSHOME=${APPBASE}/ats
ATSLOG=${LOGBASE}/ats/logs


# Shell
SHELLHOME=${APPBASE}/shell
SHELLLOG=${LOGBASE}/shell/logs


# kdb
QHOME=/opt/kdb/q
QBIN=${QHOME}/l32

KDBLOG=/var/opt/kdb/logs

KDBDATA=/data/kdb
KDBDATA_PROD=${KDBDATA}/prod
KDBDATA_HIST=${KDBDATA}/histdata
KDBDATA_BACK=${KDBDATA}/backtest

