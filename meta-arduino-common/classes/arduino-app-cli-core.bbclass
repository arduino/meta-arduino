# Common source definition for the recipes built from the arduino-app-cli
# repository (arduino-app-cli and arduino-app-assets), so that both always
# use the same version.

HOMEPAGE = "https://github.com/arduino/arduino-app-cli"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-3.0-or-later;md5=1c76c4cc354acaac30ed4d5eefea7245"

PV = "0.14.1"

SRC_URI = "git://github.com/arduino/arduino-app-cli.git;nobranch=1;protocol=https"
SRCREV = "01fd54a3ae64dea285cc87729e392a8d2d5dcbfa"
