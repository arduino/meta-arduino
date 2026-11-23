SUMMARY = "Arduino App CLI assets"
DESCRIPTION = "Static assets (docs, examples, compose templates, ...) downloaded at build time with the native arduino-app-cli and installed in the arduino-app-cli data directory."

inherit arduino-app-cli-core

# The native arduino-app-cli is only used to download the assets
DEPENDS += "arduino-app-cli-native"

ARDUINO_DATA_DIR_FIX = "/var/lib/arduino-app-cli"
# Same GID as the arduino group created in arduino-image.inc
ARDUINO_GID ?= "1001"

ASSETS_DIR = "${WORKDIR}/assets"

do_configure[noexec] = "1"

do_compile() {
    ${STAGING_BINDIR_NATIVE}/arduino-app-cli system download-runner-assets --dest ${ASSETS_DIR}
}
do_compile[network] = "1"
do_compile[cleandirs] = "${ASSETS_DIR}"

do_install() {
    install -d ${D}${ARDUINO_DATA_DIR_FIX}
    cp -R --no-preserve=ownership ${ASSETS_DIR} ${D}${ARDUINO_DATA_DIR_FIX}/

    # 775 root:arduino with the setgid bit, so new files inherit the group
    chown root:${ARDUINO_GID} ${D}${ARDUINO_DATA_DIR_FIX}
    chmod 2775 ${D}${ARDUINO_DATA_DIR_FIX}
    chown -R root:${ARDUINO_GID} ${D}${ARDUINO_DATA_DIR_FIX}/assets
    find ${D}${ARDUINO_DATA_DIR_FIX}/assets -type d -exec chmod 2775 {} +
    find ${D}${ARDUINO_DATA_DIR_FIX}/assets -type f -exec chmod u=rwX,g=rwX,o=rX {} +
}

FILES:${PN} += "${ARDUINO_DATA_DIR_FIX}"
