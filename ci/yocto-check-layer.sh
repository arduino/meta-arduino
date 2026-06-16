#!/bin/bash -e
# SPDX-License-Identifier: MIT

if [ -z $1 ] || [ -z $2 ] ; then
    echo "The REPO_DIR or WORK_DIR is empty and it needs to point to the corresponding directories."
    echo "Please run it with:"
    echo " $0 REPO_DIR WORK_DIR"
    exit 1
fi

REPO_DIR="$1"
WORK_DIR="$2"

_is_dir(){
    test -d "$1" && return
    echo "The '$1' is not a directory."
    exit 1
}

_is_dir "$REPO_DIR"
_is_dir "$WORK_DIR"

# Creates a temporary build directory to run the yocto-check-layer
# script to avoid a contaminated environment.
BUILDDIR="$(mktemp -p $WORK_DIR -d -t build-yocto-check-layer-XXXX)"
source $WORK_DIR/oe-core/oe-init-build-env $BUILDDIR
git -c advice.detachedHead=false -c init.defaultBranch=master clone --quiet --shared $REPO_DIR meta-arduino

# Yocto Project layer checking tool
CMD="yocto-check-layer"
# Layer to check (meta-arduino-qcom; meta-arduino-common checked as dependency)
CMD="$CMD meta-arduino/meta-arduino-qcom"
# Disable auto layer discovery
CMD="$CMD --no-auto"
# Layers to process for dependencies
CMD="$CMD --dependency"
CMD="$CMD $WORK_DIR/oe-core/meta"
CMD="$CMD $WORK_DIR/meta-qcom"
CMD="$CMD $WORK_DIR/meta-qcom-3rdparty"
CMD="$CMD meta-arduino/meta-arduino-common"
# Disable automatic testing of dependencies
CMD="$CMD --no-auto-dependency"
# Set machines to all machines defined in the qcom BSP sublayer
CMD="$CMD --machines $(echo $(find meta-arduino/meta-arduino-qcom/conf/machine/ -maxdepth 1 -name *.conf -exec basename {} .conf \; ))"

exec $CMD
