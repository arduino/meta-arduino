# Common source definition for the recipes built from the arduino-app-cli
# repository, so that both the target and native variants always use the
# same version.

HOMEPAGE = "https://github.com/arduino/arduino-app-cli"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245"

PV = "0.14.1"

SRC_URI = "git://github.com/arduino/arduino-app-cli.git;nobranch=1;protocol=https"
SRCREV = "df97be21f39f77a15deab1d0f0902a71a0b9d28f"
