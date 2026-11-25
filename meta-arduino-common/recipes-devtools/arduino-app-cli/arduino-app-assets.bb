SUMMARY = "Arduino App CLI assets"
DESCRIPTION = "Static assets (docs, examples, compose templates, ...) downloaded at build time with the native arduino-app-cli and installed in the arduino-app-cli data directory."

inherit arduino-app-cli-core

# The native arduino-app-cli downloads the assets; the target one creates the
# arduino group used by do_install and at runtime
DEPENDS += "arduino-app-cli-native arduino-app-cli"
RDEPENDS:${PN} += "arduino-app-cli"

ARDUINO_APP_CLI_DATA_DIR = "/var/lib/arduino-app-cli"

ASSETS_DIR = "${WORKDIR}/assets"
# download-examples creates an examples/ folder inside its destination
# TODO Move in a dedicated recipe
EXAMPLES_DEST = "${WORKDIR}/examples-download"
EXAMPLES_DIR = "${EXAMPLES_DEST}/examples"

do_configure[noexec] = "1"

do_compile() {
    # The CLI creates its folders at startup (and refuses to run as root
    # without ALLOW_ROOT): keep all of them inside WORKDIR, not in the build
    # host's /var/lib and $HOME.
    export HOME=${WORKDIR}/home
    export ARDUINO_APP_CLI__DATA_DIR=${WORKDIR}/data
    export ARDUINO_APP_CLI__APPS_DIR=${WORKDIR}/apps
    export ARDUINO_APP_BRICKS__CUSTOM_MODEL_DIR=${WORKDIR}/custom-models
    export MODELS_PATH=${WORKDIR}/models
    export ARDUINO_APP_CLI__ALLOW_ROOT=true
    install -d ${HOME}

    ${STAGING_BINDIR_NATIVE}/arduino-app-cli system download-runner-assets --dest ${ASSETS_DIR}
    ${STAGING_BINDIR_NATIVE}/arduino-app-cli system download-examples --dest ${EXAMPLES_DEST}
}
do_compile[network] = "1"
do_compile[cleandirs] = "${ASSETS_DIR} ${EXAMPLES_DEST}"

do_install() {
    install -d ${D}${ARDUINO_APP_CLI_DATA_DIR}
    cp -R --no-preserve=ownership ${ASSETS_DIR} ${D}${ARDUINO_APP_CLI_DATA_DIR}/
    cp -R --no-preserve=ownership ${EXAMPLES_DIR} ${D}${ARDUINO_APP_CLI_DATA_DIR}/

    # 775 root:arduino with the setgid bit, so new files inherit the group
    chown root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}
    chmod 2775 ${D}${ARDUINO_APP_CLI_DATA_DIR}
    chown -R root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets -type d -exec chmod 2775 {} +
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/assets -type f -exec chmod u=rwX,g=rwX,o=rX {} +

    chown -R root:arduino ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples -type d -exec chmod 2775 {} +
    find ${D}${ARDUINO_APP_CLI_DATA_DIR}/examples -type f -exec chmod u=rwX,g=rwX,o=rX {} +
}

FILES:${PN} += "${ARDUINO_APP_CLI_DATA_DIR}"
